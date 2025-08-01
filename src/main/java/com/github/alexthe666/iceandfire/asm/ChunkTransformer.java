package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.*;

public class ChunkTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.world.chunk.Chunk".equals(transformedName)) {
            return transformChunk(name, transformedName, basicClass);
        }
        return basicClass;
    }

    private byte[] transformChunk(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        // 使用 FML 反混淆映射器获取正确的方法名
        String addEntityMethodName = getMethodName("net.minecraft.world.chunk.Chunk", "addEntity", "(Lnet/minecraft/entity/Entity;)V");

        for (MethodNode method : classNode.methods) {
            if (addEntityMethodName.equals(method.name)) {
                transformAddEntity(classNode.name, method);
                break;
            }
        }

        addHasValidVehicleNBTMethod(classNode);

        // 修改 ClassWriter 的创建方式，避免计算堆栈帧
        ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return "java/lang/Object";
            }
        };
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void transformAddEntity(String chunkClassName, MethodNode method) {
        InsnList instructions = method.instructions;
        AbstractInsnNode[] nodes = instructions.toArray();

        for (int i = 0; i < nodes.length; i++) {
            AbstractInsnNode node = nodes[i];
            if (node instanceof MethodInsnNode) {
                MethodInsnNode methodNode = (MethodInsnNode) node;
                if ("warn".equals(methodNode.name) &&
                        "org/apache/logging/log4j/Logger".equals(methodNode.owner) &&
                        "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V".equals(methodNode.desc)) {

                    InsnList replacement = createAdditionalCheckInstructions(method, chunkClassName);

                    removeLoggerWarnCall(chunkClassName, instructions, i);

                    instructions.insert(node, replacement);
                    break;
                }
            }
        }

        removeSetDeadCall(instructions);
    }

    private void removeLoggerWarnCall(String chunkClassName, InsnList instructions, int warnCallIndex) {
        AbstractInsnNode[] nodes = instructions.toArray();
        int startRemoveIndex = warnCallIndex;

        // 获取 LOGGER 字段的混淆名称
        String loggerFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "LOGGER");

        for (int i = warnCallIndex - 1; i >= 0; i--) {
            AbstractInsnNode node = nodes[i];
            if (node instanceof FieldInsnNode) {
                FieldInsnNode fieldNode = (FieldInsnNode) node;
                if (loggerFieldName.equals(fieldNode.name) && fieldNode.getOpcode() == GETSTATIC) {
                    startRemoveIndex = i;
                    break;
                }
            }
        }

        for (int i = startRemoveIndex; i <= warnCallIndex; i++) {
            instructions.remove(nodes[i]);
        }
    }

    private void removeSetDeadCall(InsnList instructions) {
        AbstractInsnNode[] nodes = instructions.toArray();
        
        // 获取 Entity.setDead() 方法的混淆名称
        String setDeadMethodName = getMethodName("net.minecraft.entity.Entity", "setDead", "()V");
        String entityClassName = getClassName("net.minecraft.entity.Entity");

        for (int i = 0; i < nodes.length; i++) {
            AbstractInsnNode node = nodes[i];
            if (node instanceof MethodInsnNode) {
                MethodInsnNode methodNode = (MethodInsnNode) node;
                if (setDeadMethodName.equals(methodNode.name) && entityClassName.equals(methodNode.owner)) {
                    // 简单地用 POP 替换，不修改堆栈帧
                    instructions.insertBefore(node, new InsnNode(POP));
                    instructions.remove(node);
                    break;
                }
            }
        }
    }

    private InsnList createAdditionalCheckInstructions(MethodNode method, String chunkClassName) {
        InsnList list = new InsnList();

        int entityIndex = 1;
        int entityChunkXIndex = method.maxLocals++;
        int entityChunkZIndex = method.maxLocals++;

        LabelNode skipLabel = new LabelNode();

        // 获取混淆名称
        String entityClassName = getClassName("net.minecraft.entity.Entity");
        String nbtClassName = getClassName("net.minecraft.nbt.NBTTagCompound");
        String mathHelperClassName = getClassName("net.minecraft.util.math.MathHelper");
        String getEntityDataMethodName = getMethodName("net.minecraft.entity.Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;");
        String floorMethodName = getMethodName("net.minecraft.util.math.MathHelper", "floor", "(D)I");
        String posXFieldName = getFieldName("net.minecraft.entity.Entity", "posX");
        String posZFieldName = getFieldName("net.minecraft.entity.Entity", "posZ");
        String chunkXFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "x");
        String chunkZFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "z");
        String loggerFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "LOGGER");
        String setDeadMethodName = getMethodName("net.minecraft.entity.Entity", "setDead", "()V");

        // 检查是否有有效的载具 NBT 数据
        list.add(new VarInsnNode(ALOAD, 0)); // this
        list.add(new VarInsnNode(ALOAD, entityIndex)); // entity
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, 
                "()L" + nbtClassName + ";", false));
        list.add(new MethodInsnNode(INVOKESPECIAL, chunkClassName, "hasValidVehicleNBT", 
                "(L" + nbtClassName + ";)Z", false));
        list.add(new JumpInsnNode(IFNE, skipLabel));

        // 计算实体所在的区块坐标
        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new FieldInsnNode(GETFIELD, entityClassName, posXFieldName, "D"));
        list.add(new LdcInsnNode(16.0));
        list.add(new InsnNode(DDIV));
        list.add(new MethodInsnNode(INVOKESTATIC, mathHelperClassName, floorMethodName, "(D)I", false));
        list.add(new VarInsnNode(ISTORE, entityChunkXIndex));

        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new FieldInsnNode(GETFIELD, entityClassName, posZFieldName, "D"));
        list.add(new LdcInsnNode(16.0));
        list.add(new InsnNode(DDIV));
        list.add(new MethodInsnNode(INVOKESTATIC, mathHelperClassName, floorMethodName, "(D)I", false));
        list.add(new VarInsnNode(ISTORE, entityChunkZIndex));

        // 记录警告日志
        list.add(new FieldInsnNode(GETSTATIC, chunkClassName, loggerFieldName, "Lorg/apache/logging/log4j/Logger;"));
        list.add(new LdcInsnNode("Wrong location! ({}, {}) should be ({}, {}), {}"));

        list.add(new VarInsnNode(ILOAD, entityChunkXIndex));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

        list.add(new VarInsnNode(ILOAD, entityChunkZIndex));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

        list.add(new VarInsnNode(ALOAD, 0)); // this
        list.add(new FieldInsnNode(GETFIELD, chunkClassName, chunkXFieldName, "I"));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

        list.add(new VarInsnNode(ALOAD, 0)); // this
        list.add(new FieldInsnNode(GETFIELD, chunkClassName, chunkZFieldName, "I"));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

        list.add(new VarInsnNode(ALOAD, entityIndex)); // entity

        list.add(new MethodInsnNode(INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "warn", 
                "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", true));

        // 设置实体为死亡状态
        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, setDeadMethodName, "()V", false));

        list.add(skipLabel);
        // 添加 Frame 指令确保堆栈帧正确
        list.add(new FrameNode(F_SAME, 0, null, 0, null));

        return list;
    }

    private void addHasValidVehicleNBTMethod(ClassNode classNode) {
        String nbtClassName = getClassName("net.minecraft.nbt.NBTTagCompound");
        String hasKeyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "hasKey", "(Ljava/lang/String;)Z");
        String getCompoundTagMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "getCompoundTag", "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;");
        String getIntegerMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "getInteger", "(Ljava/lang/String;)I");
        String chunkXFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "x");
        String chunkZFieldName = getFieldName("net.minecraft.world.chunk.Chunk", "z");

        MethodNode method = new MethodNode(
                ACC_PRIVATE,
                "hasValidVehicleNBT",
                "(L" + nbtClassName + ";)Z",
                null,
                null
        );

        InsnList instructions = new InsnList();
        LabelNode returnFalseLabel = new LabelNode();
        LabelNode returnTrueLabel = new LabelNode();

        int vehiclePosNBTIndex = 2;
        int vehicleChunkXIndex = 3;
        int vehicleChunkZIndex = 4;

        // 添加初始帧
        instructions.add(new FrameNode(F_SAME, 0, null, 0, null));

        // 检查是否有 RiddenVehicle 键
        instructions.add(new VarInsnNode(ALOAD, 1)); // entityData
        instructions.add(new LdcInsnNode("RiddenVehicle"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, nbtClassName, hasKeyMethodName, "(Ljava/lang/String;)Z", false));
        instructions.add(new JumpInsnNode(IFEQ, returnFalseLabel));

        // 获取 RiddenVehicle 数据
        instructions.add(new VarInsnNode(ALOAD, 1)); // entityData
        instructions.add(new LdcInsnNode("RiddenVehicle"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, nbtClassName, getCompoundTagMethodName, 
                "(Ljava/lang/String;)L" + nbtClassName + ";", false));
        instructions.add(new VarInsnNode(ASTORE, vehiclePosNBTIndex));

        // 获取 vehicleChunkX
        instructions.add(new VarInsnNode(ALOAD, vehiclePosNBTIndex));
        instructions.add(new LdcInsnNode("vehicleChunkX"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, nbtClassName, getIntegerMethodName, "(Ljava/lang/String;)I", false));
        instructions.add(new VarInsnNode(ISTORE, vehicleChunkXIndex));

        // 获取 vehicleChunkZ
        instructions.add(new VarInsnNode(ALOAD, vehiclePosNBTIndex));
        instructions.add(new LdcInsnNode("vehicleChunkZ"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, nbtClassName, getIntegerMethodName, "(Ljava/lang/String;)I", false));
        instructions.add(new VarInsnNode(ISTORE, vehicleChunkZIndex));

        // 比较 vehicleChunkX == this.x
        instructions.add(new VarInsnNode(ILOAD, vehicleChunkXIndex));
        instructions.add(new VarInsnNode(ALOAD, 0)); // this
        instructions.add(new FieldInsnNode(GETFIELD, classNode.name, chunkXFieldName, "I"));
        instructions.add(new JumpInsnNode(IF_ICMPNE, returnFalseLabel));

        instructions.add(new VarInsnNode(ILOAD, vehicleChunkZIndex));
        instructions.add(new VarInsnNode(ALOAD, 0)); // this
        instructions.add(new FieldInsnNode(GETFIELD, classNode.name, chunkZFieldName, "I"));
        instructions.add(new JumpInsnNode(IF_ICMPNE, returnFalseLabel));

        instructions.add(returnTrueLabel);
        instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        instructions.add(new InsnNode(ICONST_1));
        instructions.add(new InsnNode(IRETURN));

        instructions.add(returnFalseLabel);
        instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        instructions.add(new InsnNode(ICONST_0));
        instructions.add(new InsnNode(IRETURN));

        method.instructions = instructions;
        method.maxStack = 3;
        method.maxLocals = 5;

        classNode.methods.add(method);
    }

    private String getClassName(String deobfName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(deobfName.replace('.', '/'));
    }

    private String getMethodName(String className, String methodName, String methodDesc) {
        String mappedClassName = getClassName(className);
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(mappedClassName, methodName, methodDesc);
    }

    private String getFieldName(String className, String fieldName) {
        String mappedClassName = getClassName(className);
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(mappedClassName, fieldName, null);
    }
}