package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.*;

public class AnvilChunkLoaderTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // 目标类名在生产环境中是混淆的，但在开发环境中是清晰的。
        // 使用 transformedName 进行比较更可靠。
        if ("net.minecraft.world.chunk.storage.AnvilChunkLoader".equals(transformedName)) {
            // 注意：这里我移除了多余的参数传递，保持代码整洁
            return transformAnvilChunkLoader(basicClass);
        }
        return basicClass;
    }

    private byte[] transformAnvilChunkLoader(byte[] basicClass) {
        try {
            ClassNode classNode = new ClassNode();
            // 修复 1: 使用 EXPAND_FRAMES 读取类，这有助于ASM更好地理解现有的栈帧结构，以便进行重建
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

            // 这里的逻辑似乎有误，方法签名是 (L...;L...;L...;)Lnet/minecraft/entity/Entity;
            // 而不是 V (void)。你需要根据实际的方法描述符来查找。
            // 从之前的错误日志看，正确的方法是 readChunkEntity
            String methodName = "readChunkEntity"; // 在开发环境中是这个名字
            String methodDesc = "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/entity/Entity;";

            // 在生产环境中需要反混淆
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
                "net/minecraft/world/chunk/storage/AnvilChunkLoader", methodName, methodDesc
            );

            for (MethodNode method : classNode.methods) {
                // 同时比较方法名和描述符，确保唯一性
                if (mappedMethodName.equals(method.name) && method.desc.startsWith("(Lnet/minecraft/nbt/NBTTagCompound;")) {
                    System.out.println("[Ice and Fire] Transforming method: " + mappedMethodName);
                    transformReadChunkEntity(method);
                    break;
                }
            }

            // 修复 2: 使用 COMPUTE_FRAMES 和 COMPUTE_MAXS，让ASM自动计算所有内容
            // 这样就不再需要手动管理栈帧和局部变量/操作数栈大小
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            System.err.println("[Ice and Fire] Failed to transform AnvilChunkLoader. Returning original class bytes.");
            e.printStackTrace();
            return basicClass; // 如果转换失败，返回原始字节码，防止游戏崩溃
        }
    }

    private void transformReadChunkEntity(MethodNode method) {
        // 寻找注入点：在第一个 getTagList 调用之前
        String getTagListMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName("net/minecraft/nbt/NBTTagCompound", "getTagList", "(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;");
        String nbtClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap("net/minecraft/nbt/NBTTagCompound");

        AbstractInsnNode injectionPoint = null;
        for (AbstractInsnNode node : method.instructions.toArray()) {
            if (node instanceof MethodInsnNode) {
                MethodInsnNode methodNode = (MethodInsnNode) node;
                if (getTagListMethodName.equals(methodNode.name) && nbtClassName.equals(methodNode.owner)) {
                    injectionPoint = node;
                    break;
                }
            }
        }
        
        if (injectionPoint != null) {
            InsnList toInsert = createInsertVehicleNBTInstructions(method);
            method.instructions.insertBefore(injectionPoint, toInsert);
            System.out.println("[Ice and Fire] Injected vehicle NBT loading logic.");
        }

        // 寻找返回点：在所有 ARETURN 指令之前
        for (AbstractInsnNode node : method.instructions.toArray()) {
            if (node.getOpcode() == ARETURN) {
                InsnList toInsert = createRemoveVehicleNBTInstructions();
                // DUP 指令会复制栈顶的 entity 引用，一个用于我们的逻辑，另一个用于原始的返回指令
                method.instructions.insertBefore(node, toInsert);
            }
        }
        System.out.println("[Ice and Fire] Injected vehicle NBT cleanup logic.");
    }

    private InsnList createInsertVehicleNBTInstructions(MethodNode method) {
        InsnList list = new InsnList();

        // 修复 3: 移除所有手动添加的 FrameNode
        // list.add(new FrameNode(F_SAME, 0, null, 0, null)); // <--- 删除这类代码

        // 参数索引 (非静态方法，0是this，但这里我们不需要)
        // 实际上，readChunkEntity是非静态的，但我们不需要AnvilChunkLoader的实例'this'
        // 局部变量表从0开始:
        // 0: this (AnvilChunkLoader)
        // 1: compound (NBTTagCompound)
        // 2: world (World)
        // 3: chunk (Chunk)
        // 4: entity (Entity) - 这是方法内部创建的
        int vehicleNBTIndex = 1; 
        int entityIndex = 4;

        // 局部变量索引
        int currentMaxLocals = method.maxLocals;
        int chunkXIndex = currentMaxLocals++;
        int chunkZIndex = currentMaxLocals++;
        int riddenVehicleNBTIndex = currentMaxLocals++;
        int passengerListNBTIndex = currentMaxLocals++;
        int iIndex = currentMaxLocals++;
        int riderNBTIndex = currentMaxLocals++;
        int posListNBTIndex = currentMaxLocals++;
        int forgeDataNBTIndex = currentMaxLocals++;

        method.maxLocals = currentMaxLocals; // 更新方法的最大局部变量数

        // 获取混淆后的名称
        String entityClassName = getClassName("net.minecraft.entity.Entity");
        String nbtCompoundClassName = getClassName("net.minecraft.nbt.NBTTagCompound");
        String nbtListClassName = getClassName("net.minecraft.nbt.NBTTagList");
        String mathHelperClassName = getClassName("net.minecraft.util.math.MathHelper");
        
        String getEntityDataMethodName = getMethodName("net.minecraft.entity.Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;");
        String hasKeyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "hasKey", "(Ljava/lang/String;)Z");
        String getTagListMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "getTagList", "(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagList;");
        String getCompoundTagMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "getCompoundTag", "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;");
        String getIntegerMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "getInteger", "(Ljava/lang/String;)I");
        String setIntegerMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "setInteger", "(Ljava/lang/String;I)V");
        String setTagMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "setTag", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V");
        String tagCountMethodName = getMethodName("net.minecraft.nbt.NBTTagList", "tagCount", "()I");
        String getDoubleAtMethodName = getMethodName("net.minecraft.nbt.NBTTagList", "getDoubleAt", "(I)D");
        String getCompoundTagAtMethodName = getMethodName("net.minecraft.nbt.NBTTagList", "getCompoundTagAt", "(I)Lnet/minecraft/nbt/NBTTagCompound;");
        String floorMethodName = getMethodName("net.minecraft.util.math.MathHelper", "floor", "(D)I");

        LabelNode skipLabel = new LabelNode();
        LabelNode hasRiddenVehicleLabel = new LabelNode();
        LabelNode calculatePosLabel = new LabelNode();
        LabelNode processPassengersLabel = new LabelNode();
        LabelNode loopStartLabel = new LabelNode();
        LabelNode loopEndLabel = new LabelNode();
        LabelNode hasForgeDataLabel = new LabelNode();
        LabelNode createForgeDataLabel = new LabelNode();
        LabelNode endLoopLabel = new LabelNode();

        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new JumpInsnNode(IFNULL, skipLabel));
        list.add(new VarInsnNode(ALOAD, vehicleNBTIndex));
        list.add(new LdcInsnNode("Passengers"));
        list.add(new IntInsnNode(BIPUSH, 9)); // NBT.TAG_LIST is 9
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getTagListMethodName, "(Ljava/lang/String;I)L" + nbtListClassName + ";", false));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtListClassName, tagCountMethodName, "()I", false));
        list.add(new JumpInsnNode(IFEQ, skipLabel));
        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, hasKeyMethodName, "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFNE, hasRiddenVehicleLabel));

        list.add(calculatePosLabel);
        list.add(new VarInsnNode(ALOAD, vehicleNBTIndex));
        list.add(new LdcInsnNode("Pos"));
        list.add(new IntInsnNode(BIPUSH, 6)); // NBT.TAG_DOUBLE is 6
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getTagListMethodName, "(Ljava/lang/String;I)L" + nbtListClassName + ";", false));
        list.add(new VarInsnNode(ASTORE, posListNBTIndex));
        list.add(new VarInsnNode(ALOAD, posListNBTIndex));
        list.add(new InsnNode(ICONST_0));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtListClassName, getDoubleAtMethodName, "(I)D", false));
        list.add(new LdcInsnNode(16.0));
        list.add(new InsnNode(DDIV));
        list.add(new MethodInsnNode(INVOKESTATIC, mathHelperClassName, floorMethodName, "(D)I", false));
        list.add(new VarInsnNode(ISTORE, chunkXIndex));
        list.add(new VarInsnNode(ALOAD, posListNBTIndex));
        list.add(new InsnNode(ICONST_2));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtListClassName, getDoubleAtMethodName, "(I)D", false));
        list.add(new LdcInsnNode(16.0));
        list.add(new InsnNode(DDIV));
        list.add(new MethodInsnNode(INVOKESTATIC, mathHelperClassName, floorMethodName, "(D)I", false));
        list.add(new VarInsnNode(ISTORE, chunkZIndex));
        list.add(new JumpInsnNode(GOTO, processPassengersLabel));

        list.add(hasRiddenVehicleLabel);
        list.add(new VarInsnNode(ALOAD, entityIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getCompoundTagMethodName, "(Ljava/lang/String;)L" + nbtCompoundClassName + ";", false));
        list.add(new VarInsnNode(ASTORE, riddenVehicleNBTIndex));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new LdcInsnNode("vehicleChunkX"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getIntegerMethodName, "(Ljava/lang/String;)I", false));
        list.add(new VarInsnNode(ISTORE, chunkXIndex));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new LdcInsnNode("vehicleChunkZ"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getIntegerMethodName, "(Ljava/lang/String;)I", false));
        list.add(new VarInsnNode(ISTORE, chunkZIndex));

        list.add(processPassengersLabel);
        list.add(new TypeInsnNode(NEW, nbtCompoundClassName));
        list.add(new InsnNode(DUP));
        list.add(new MethodInsnNode(INVOKESPECIAL, nbtCompoundClassName, "<init>", "()V", false));
        list.add(new VarInsnNode(ASTORE, riddenVehicleNBTIndex));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new LdcInsnNode("vehicleChunkX"));
        list.add(new VarInsnNode(ILOAD, chunkXIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, setIntegerMethodName, "(Ljava/lang/String;I)V", false));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new LdcInsnNode("vehicleChunkZ"));
        list.add(new VarInsnNode(ILOAD, chunkZIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, setIntegerMethodName, "(Ljava/lang/String;I)V", false));
        list.add(new VarInsnNode(ALOAD, vehicleNBTIndex));
        list.add(new LdcInsnNode("Passengers"));
        list.add(new IntInsnNode(BIPUSH, 10)); // NBT.TAG_COMPOUND is 10
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getTagListMethodName, "(Ljava/lang/String;I)L" + nbtListClassName + ";", false));
        list.add(new VarInsnNode(ASTORE, passengerListNBTIndex));
        list.add(new InsnNode(ICONST_0));
        list.add(new VarInsnNode(ISTORE, iIndex));

        list.add(loopStartLabel);
        list.add(new VarInsnNode(ILOAD, iIndex));
        list.add(new VarInsnNode(ALOAD, passengerListNBTIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtListClassName, tagCountMethodName, "()I", false));
        list.add(new JumpInsnNode(IF_ICMPGE, loopEndLabel));
        list.add(new VarInsnNode(ALOAD, passengerListNBTIndex));
        list.add(new VarInsnNode(ILOAD, iIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtListClassName, getCompoundTagAtMethodName, "(I)L" + nbtCompoundClassName + ";", false));
        list.add(new VarInsnNode(ASTORE, riderNBTIndex));
        list.add(new VarInsnNode(ALOAD, riderNBTIndex));
        list.add(new LdcInsnNode("ForgeData"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, hasKeyMethodName, "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFEQ, createForgeDataLabel));

        list.add(hasForgeDataLabel);
        list.add(new VarInsnNode(ALOAD, riderNBTIndex));
        list.add(new LdcInsnNode("ForgeData"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, getCompoundTagMethodName, "(Ljava/lang/String;)L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, setTagMethodName, "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false));
        list.add(new JumpInsnNode(GOTO, endLoopLabel));

        list.add(createForgeDataLabel);
        list.add(new TypeInsnNode(NEW, nbtCompoundClassName));
        list.add(new InsnNode(DUP));
        list.add(new MethodInsnNode(INVOKESPECIAL, nbtCompoundClassName, "<init>", "()V", false));
        list.add(new VarInsnNode(ASTORE, forgeDataNBTIndex));
        list.add(new VarInsnNode(ALOAD, forgeDataNBTIndex));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new VarInsnNode(ALOAD, riddenVehicleNBTIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, setTagMethodName, "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false));
        list.add(new VarInsnNode(ALOAD, riderNBTIndex));
        list.add(new LdcInsnNode("ForgeData"));
        list.add(new VarInsnNode(ALOAD, forgeDataNBTIndex));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, setTagMethodName, "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false));

        list.add(endLoopLabel);
        list.add(new IincInsnNode(iIndex, 1));
        list.add(new JumpInsnNode(GOTO, loopStartLabel));

        list.add(loopEndLabel);
        list.add(skipLabel);

        return list;
    }

    private InsnList createRemoveVehicleNBTInstructions() {
        InsnList list = new InsnList();

        // 修复 3: 移除所有手动添加的 FrameNode

        String entityClassName = getClassName("net.minecraft.entity.Entity");
        String nbtCompoundClassName = getClassName("net.minecraft.nbt.NBTTagCompound");
        
        String getEntityDataMethodName = getMethodName("net.minecraft.entity.Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;");
        String hasKeyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "hasKey", "(Ljava/lang/String;)Z");
        String removeTagMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "removeTag", "(Ljava/lang/String;)V");
        String isEmptyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "isEmpty", "()Z");

        LabelNode endLabel = new LabelNode();
        LabelNode emptyCheckLabel = new LabelNode();

        // 栈顶现在是 entity 引用，我们需要复制它来进行操作，同时保留原始引用以供返回
        list.add(new InsnNode(DUP)); 
        list.add(new JumpInsnNode(IFNULL, endLabel)); // 如果 entity 为 null，直接跳到末尾

        list.add(new InsnNode(DUP)); // 复制 entity
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, hasKeyMethodName, "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFEQ, emptyCheckLabel));

        list.add(new InsnNode(DUP)); // 复制 entity
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, removeTagMethodName, "(Ljava/lang/String;)V", false));

        list.add(emptyCheckLabel);
        list.add(new InsnNode(DUP)); // 复制 entity
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, isEmptyMethodName, "()Z", false));
        list.add(new JumpInsnNode(IFEQ, endLabel));

        list.add(new InsnNode(DUP)); // 复制 entity
        list.add(new TypeInsnNode(CHECKCAST, "com/github/alexthe666/iceandfire/asm/EntityAccessorTransformer"));
        list.add(endLabel);

        return list;
    }

    private String getClassName(String dotClassName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(dotClassName.replace('.', '/'));
    }

    private String getMethodName(String owner, String methodName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner.replace('.', '/'), methodName, desc);
    }
}