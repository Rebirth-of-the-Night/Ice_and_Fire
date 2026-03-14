package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class EntityLivingBaseTransformer implements IClassTransformer {
    private static final String ENTITY_LIVING_BASE = "net/minecraft/entity/EntityLivingBase";
    private static final String INTERFACE_NAME = "com/github/alexthe666/iceandfire/client/model/util/IEntityLivingBaseRenderContext";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return basicClass;
        }

        String transformedClassPath = transformedName.replace('.', '/');

        // Transform all entity classes, check at runtime via interface
        if (!isEntityClass(transformedClassPath, basicClass)) {
            return basicClass;
        }

        return transformClass(basicClass, transformedClassPath);
    }

    private boolean isEntityClass(String className, byte[] classBytes) {
        // Always transform EntityLivingBase
        if (ENTITY_LIVING_BASE.equals(className)) {
            return true;
        }

        // Check if it's in entity package (simple heuristic)
        if (!className.startsWith("net/minecraft/entity/") &&
                !className.contains("/entity/")) {
            return false;
        }

        try {
            ClassReader reader = new ClassReader(classBytes);
            EntityClassChecker checker = new EntityClassChecker();
            reader.accept(checker, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return checker.isEntityClass;
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] transformClass(byte[] basicClass, String className) {
        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new SafeClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            EntityLivingBaseVisitor visitor = new EntityLivingBaseVisitor(writer, className);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return basicClass;
        }
    }

    private static class EntityClassChecker extends ClassVisitor {
        public boolean isEntityClass = false;

        public EntityClassChecker() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            // Check if superclass looks like an entity
            if (superName != null && (
                    superName.startsWith("net/minecraft/entity/") ||
                            superName.contains("/entity/") ||
                            ENTITY_LIVING_BASE.equals(superName))) {
                isEntityClass = true;
            }
        }
    }

    private static class SafeClassWriter extends ClassWriter {
        public SafeClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            return "java/lang/Object";
        }
    }

    private static class EntityLivingBaseVisitor extends ClassVisitor {
        private final String className;
        private boolean alreadyImplementsInterface = false;
        private boolean hasField = false;
        private boolean hasSetter = false;
        private boolean hasGetter = false;

        public EntityLivingBaseVisitor(ClassVisitor cv, String className) {
            super(Opcodes.ASM5, cv);
            this.className = className;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            for (String iface : interfaces) {
                if (INTERFACE_NAME.equals(iface)) {
                    alreadyImplementsInterface = true;
                    break;
                }
            }

            if (!alreadyImplementsInterface) {
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
            if ("iceAndFire$isRenderingWithGlint".equals(name)) {
                hasField = true;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if ("iceAndFire$setGlintContext".equals(name) && "(Z)V".equals(descriptor)) {
                hasSetter = true;
            }
            if ("iceAndFire$getGlintContext".equals(name) && "()Z".equals(descriptor)) {
                hasGetter = true;
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            if (!alreadyImplementsInterface) {
                if (!hasField) {
                    FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE,
                            "iceAndFire$isRenderingWithGlint", "Z", null, null);
                    if (fv != null) {
                        fv.visitEnd();
                    }
                }

                if (!hasSetter) {
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
                }

                if (!hasGetter) {
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
            }

            super.visitEnd();
        }
    }
}