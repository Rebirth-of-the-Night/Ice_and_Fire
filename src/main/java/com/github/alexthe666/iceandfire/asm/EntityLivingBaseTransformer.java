package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class EntityLivingBaseTransformer implements IClassTransformer {
    private static final String ENTITY_LIVING_BASE = "net/minecraft/entity/EntityLivingBase";
    private static final String INTERFACE_NAME = "com/github/alexthe666/iceandfire/client/model/util/IEntityLivingBaseRenderContext";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }

        String transformedClassPath = transformedName.replace('.', '/');

        try {
            // First check if this is EntityLivingBase itself
            if (ENTITY_LIVING_BASE.equals(transformedClassPath)) {
                return transformClass(basicClass, transformedClassPath);
            }

            // Check if this class extends EntityLivingBase
            ClassReader reader = new ClassReader(basicClass);
            InheritanceChecker checker = new InheritanceChecker();
            reader.accept(checker, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            if (checker.extendsEntityLivingBase) {
                return transformClass(basicClass, transformedClassPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
    }

    private byte[] transformClass(byte[] basicClass, String className) {
        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            EntityLivingBaseVisitor visitor = new EntityLivingBaseVisitor(writer, className);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return basicClass;
        }
    }

    // Helper class to check if a class extends EntityLivingBase
    private static class InheritanceChecker extends ClassVisitor {
        public boolean extendsEntityLivingBase = false;

        public InheritanceChecker() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            // Check if the superclass is EntityLivingBase
            if (ENTITY_LIVING_BASE.equals(superName)) {
                extendsEntityLivingBase = true;
                return;
            }

            // Could extend checking for other classes in inheritance hierarchy
            // but this basic check will work for direct subclasses
        }
    }

    private static class EntityLivingBaseVisitor extends ClassVisitor {
        private final String className;
        private boolean alreadyImplementsInterface = false;

        public EntityLivingBaseVisitor(ClassVisitor cv, String className) {
            super(Opcodes.ASM5, cv);
            this.className = className;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            // Check if the class already implements our interface
            for (String iface : interfaces) {
                if (INTERFACE_NAME.equals(iface)) {
                    alreadyImplementsInterface = true;
                    break;
                }
            }

            if (!alreadyImplementsInterface) {
                // Add IEntityLivingBaseRenderContext interface to the class
                String[] newInterfaces = new String[interfaces.length + 1];
                System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
                newInterfaces[interfaces.length] = INTERFACE_NAME;
                super.visit(version, access, name, signature, superName, newInterfaces);
            } else {
                super.visit(version, access, name, signature, superName, interfaces);
            }
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            // Check if the field already exists
            if ("iceAndFire$isRenderingWithGlint".equals(name)) {
                return super.visitField(access, name, descriptor, signature, value);
            }
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            // Skip adding methods if they already exist
            if (("iceAndFire$setGlintContext".equals(name) && "(Z)V".equals(descriptor)) ||
                    ("iceAndFire$getGlintContext".equals(name) && "()Z".equals(descriptor))) {
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            // Add the field if not already implemented
            if (!alreadyImplementsInterface) {
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
                setterMv.visitMaxs(2, 2);
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
                getterMv.visitMaxs(1, 1);
                getterMv.visitEnd();
            }

            super.visitEnd();
        }
    }
}