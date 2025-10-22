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
        if ("net.minecraft.world.chunk.storage.AnvilChunkLoader".equals(transformedName)) {
            return transformAnvilChunkLoader(basicClass);
        }
        return basicClass;
    }

    private byte[] transformAnvilChunkLoader(byte[] basicClass) {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

            String methodName = "readChunkEntity";
            String methodDesc = "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/entity/Entity;";

            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
                    "net/minecraft/world/chunk/storage/AnvilChunkLoader", methodName, methodDesc
            );

            for (MethodNode method : classNode.methods) {
                if (mappedMethodName.equals(method.name) && method.desc.startsWith("(Lnet/minecraft/nbt/NBTTagCompound;")) {
                    System.out.println("[Ice and Fire] Transforming method: " + mappedMethodName);
                    transformReadChunkEntity(method);
                    break;
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            System.err.println("[Ice and Fire] Failed to transform AnvilChunkLoader. Returning original class bytes.");
            e.printStackTrace();
            return basicClass;
        }
    }

    private void transformReadChunkEntity(MethodNode method) {
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

        for (AbstractInsnNode node : method.instructions.toArray()) {
            if (node.getOpcode() == ARETURN) {
                InsnList toInsert = createRemoveVehicleNBTInstructions();

                method.instructions.insertBefore(node, toInsert);
            }
        }
        System.out.println("[Ice and Fire] Injected vehicle NBT cleanup logic.");
    }

    private InsnList createInsertVehicleNBTInstructions(MethodNode method) {
        InsnList list = new InsnList();

        int vehicleNBTIndex = 1;
        int entityIndex = 4;

        int currentMaxLocals = method.maxLocals;
        int chunkXIndex = currentMaxLocals++;
        int chunkZIndex = currentMaxLocals++;
        int riddenVehicleNBTIndex = currentMaxLocals++;
        int passengerListNBTIndex = currentMaxLocals++;
        int iIndex = currentMaxLocals++;
        int riderNBTIndex = currentMaxLocals++;
        int posListNBTIndex = currentMaxLocals++;
        int forgeDataNBTIndex = currentMaxLocals++;

        method.maxLocals = currentMaxLocals;

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

        String entityClassName = getClassName("net.minecraft.entity.Entity");
        String nbtCompoundClassName = getClassName("net.minecraft.nbt.NBTTagCompound");

        String getEntityDataMethodName = getMethodName("net.minecraft.entity.Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;");
        String hasKeyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "hasKey", "(Ljava/lang/String;)Z");
        String removeTagMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "removeTag", "(Ljava/lang/String;)V");
        String isEmptyMethodName = getMethodName("net.minecraft.nbt.NBTTagCompound", "isEmpty", "()Z");

        LabelNode endLabel = new LabelNode();
        LabelNode emptyCheckLabel = new LabelNode();

        list.add(new InsnNode(DUP));
        list.add(new JumpInsnNode(IFNULL, endLabel));

        list.add(new InsnNode(DUP));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, hasKeyMethodName, "(Ljava/lang/String;)Z", false));
        list.add(new JumpInsnNode(IFEQ, emptyCheckLabel));

        list.add(new InsnNode(DUP));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new LdcInsnNode("RiddenVehicle"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, removeTagMethodName, "(Ljava/lang/String;)V", false));

        list.add(emptyCheckLabel);
        list.add(new InsnNode(DUP));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, entityClassName, getEntityDataMethodName, "()L" + nbtCompoundClassName + ";", false));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, nbtCompoundClassName, isEmptyMethodName, "()Z", false));
        list.add(new JumpInsnNode(IFEQ, endLabel));

        list.add(new InsnNode(DUP));
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