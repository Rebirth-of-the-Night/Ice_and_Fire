package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.*;

public class EntityAccessorTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.entity.Entity".equals(transformedName)) {
            System.out.println("[BoatDeleteBegone] Transforming Entity class: " + name);
            return transformEntity(basicClass);
        }
        return basicClass;
    }

    private byte[] transformEntity(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        String nbtClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap("net/minecraft/nbt/NBTTagCompound");
        String nbtDesc = "L" + nbtClassName + ";";

        boolean methodExists = classNode.methods.stream()
                .anyMatch(method -> "setCustomEntityData".equals(method.name) &&
                        ("(" + nbtDesc + ")V").equals(method.desc));

        if (!methodExists) {
            addSetCustomEntityDataMethod(classNode, nbtDesc);
            System.out.println("[BoatDeleteBegone] Successfully added setCustomEntityData method");
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return "java/lang/Object";
            }
        };
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void addSetCustomEntityDataMethod(ClassNode classNode, String nbtDesc) {
        String fieldName = "customEntityData";

        boolean fieldExists = classNode.fields.stream()
                .anyMatch(field -> fieldName.equals(field.name) && nbtDesc.equals(field.desc));

        if (!fieldExists) {
            FieldNode field = new FieldNode(ACC_PRIVATE, fieldName, nbtDesc, null, null);
            classNode.fields.add(field);
        }

        MethodNode method = new MethodNode(
                ACC_PUBLIC,
                "setCustomEntityData",
                "(" + nbtDesc + ")V",
                null,
                null
        );

        InsnList instructions = new InsnList();
        instructions.add(new VarInsnNode(ALOAD, 0)); // this
        instructions.add(new VarInsnNode(ALOAD, 1)); // compound
        instructions.add(new FieldInsnNode(PUTFIELD, classNode.name, fieldName, nbtDesc));
        instructions.add(new InsnNode(RETURN));

        method.instructions = instructions;
        method.maxStack = 2;
        method.maxLocals = 2;

        classNode.methods.add(method);
    }
}