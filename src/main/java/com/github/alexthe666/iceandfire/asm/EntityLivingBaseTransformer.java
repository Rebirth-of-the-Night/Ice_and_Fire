package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class EntityLivingBaseTransformer implements IClassTransformer {
    private static final String[] TARGET_CLASSES = {
            "net/minecraft/entity/EntityLivingBase",
            "net/minecraft/client/entity/EntityPlayerSP",
            "net/minecraft/entity/player/EntityPlayerMP",
            "net/minecraft/client/entity/EntityOtherPlayerMP",
            "net/minecraft/entity/player/EntityPlayer"
    };

    private static final String INTERFACE_NAME = "com/github/alexthe666/iceandfire/client/model/util/IEntityLivingBaseRenderContext";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String transformedClassPath = transformedName.replace('.', '/');

        // Check if this is one of our target classes
        boolean isTarget = false;
        for (String targetClass : TARGET_CLASSES) {
            if (targetClass.equals(transformedClassPath)) {
                isTarget = true;
                break;
            }
        }

        if (!isTarget) {
            return basicClass;
        }

        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            EntityLivingBaseVisitor visitor = new EntityLivingBaseVisitor(writer, transformedClassPath);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
    }

    private static class EntityLivingBaseVisitor extends ClassVisitor {
        private final String className;

        public EntityLivingBaseVisitor(ClassVisitor cv, String className) {
            super(Opcodes.ASM5, cv);
            this.className = className;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            // Add IEntityLivingBaseRenderContext interface to the class
            String[] newInterfaces = new String[interfaces.length + 1];
            System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
            newInterfaces[interfaces.length] = INTERFACE_NAME;

            super.visit(version, access, name, signature, superName, newInterfaces);
        }

        @Override
        public void visitEnd() {
            // Add the new field
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE,
                    "iceAndFire$isRenderingWithGlint", "Z", null, null);
            if (fv != null) {
                fv.visitEnd();
            }

            // Add setter method
            MethodVisitor setterMv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                    "iceAndFire$setGlintContext", "(Z)V", null, null);
            setterMv.visitCode();
            setterMv.visitVarInsn(Opcodes.ALOAD, 0);
            setterMv.visitVarInsn(Opcodes.ILOAD, 1);
            setterMv.visitFieldInsn(Opcodes.PUTFIELD,
                    className,
                    "iceAndFire$isRenderingWithGlint", "Z");
            setterMv.visitInsn(Opcodes.RETURN);
            setterMv.visitMaxs(2, 2);  // explicit values instead of 0, 0
            setterMv.visitEnd();

            // Add getter method
            MethodVisitor getterMv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                    "iceAndFire$getGlintContext", "()Z", null, null);
            getterMv.visitCode();
            getterMv.visitVarInsn(Opcodes.ALOAD, 0);
            getterMv.visitFieldInsn(Opcodes.GETFIELD,
                    className,
                    "iceAndFire$isRenderingWithGlint", "Z");
            getterMv.visitInsn(Opcodes.IRETURN);
            getterMv.visitMaxs(1, 1);  // explicit values instead of 0, 0
            getterMv.visitEnd();

            super.visitEnd();
        }
    }
}