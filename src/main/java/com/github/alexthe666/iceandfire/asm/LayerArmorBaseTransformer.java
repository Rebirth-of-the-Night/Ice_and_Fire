package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class LayerArmorBaseTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "net/minecraft/client/renderer/entity/layers/LayerArmorBase";
    private static final String RENDER_METHOD = "renderEnchantedGlint";
    private static final String RENDER_METHOD_DEOBF = "func_188364_a";
    private static final String RENDER_METHOD_DESC = "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V";

    private static final String RENDER_TARGET = "net/minecraft/client/model/ModelBase";
    private static final String RENDER_TARGET_METHOD = "render";
    private static final String RENDER_TARGET_DESC = "(Lnet/minecraft/entity/Entity;FFFFFF)V";

    private static final String INTERFACE_NAME = "com/github/alexthe666/iceandfire/client/model/util/IEntityLivingBaseRenderContext";
    private static final String GLINT_METHOD = "iceAndFire$setGlintContext";
    private static final String GLINT_METHOD_DESC = "(Z)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String transformedClassPath = transformedName.replace('.', '/');

        // Check if this is our target class
        if (!TARGET_CLASS.equals(transformedClassPath)) {
            return basicClass;
        }

        boolean isObfuscated = !name.equals(transformedName);

        try {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            String methodName = isObfuscated ? RENDER_METHOD_DEOBF : RENDER_METHOD;

            // Find the renderEnchantedGlint method
            for (MethodNode method : classNode.methods) {
                if (methodName.equals(method.name) && RENDER_METHOD_DESC.equals(method.desc)) {
                    transformRenderEnchantedGlintMethod(method);
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return basicClass;
    }

    private void transformRenderEnchantedGlintMethod(MethodNode method) {
        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);

            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;

                if (RENDER_TARGET.equals(methodInsn.owner) &&
                        RENDER_TARGET_METHOD.equals(methodInsn.name) &&
                        RENDER_TARGET_DESC.equals(methodInsn.desc)) {

                    // Insert pre-render code
                    InsnList preInstructions = new InsnList();

                    // Safe type checking before cast
                    LabelNode continueLabel = new LabelNode();
                    preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load EntityLivingBase parameter
                    preInstructions.add(new TypeInsnNode(Opcodes.INSTANCEOF, INTERFACE_NAME));
                    preInstructions.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel)); // Jump if not an instance

                    // Actual cast and method call
                    preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load EntityLivingBase parameter
                    preInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, INTERFACE_NAME));
                    preInstructions.add(new InsnNode(Opcodes.ICONST_1)); // true parameter
                    preInstructions.add(new MethodInsnNode(
                            Opcodes.INVOKEINTERFACE,
                            INTERFACE_NAME,
                            GLINT_METHOD,
                            GLINT_METHOD_DESC,
                            true));

                    preInstructions.add(continueLabel);

                    method.instructions.insertBefore(insn, preInstructions);

                    // Insert post-render code
                    InsnList postInstructions = new InsnList();

                    // Safe type checking before cast
                    LabelNode skipLabel = new LabelNode();
                    postInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load EntityLivingBase parameter
                    postInstructions.add(new TypeInsnNode(Opcodes.INSTANCEOF, INTERFACE_NAME));
                    postInstructions.add(new JumpInsnNode(Opcodes.IFEQ, skipLabel)); // Jump if not an instance

                    // Actual cast and method call
                    postInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Load EntityLivingBase parameter
                    postInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, INTERFACE_NAME));
                    postInstructions.add(new InsnNode(Opcodes.ICONST_0)); // false parameter
                    postInstructions.add(new MethodInsnNode(
                            Opcodes.INVOKEINTERFACE,
                            INTERFACE_NAME,
                            GLINT_METHOD,
                            GLINT_METHOD_DESC,
                            true));

                    postInstructions.add(skipLabel);

                    method.instructions.insert(insn, postInstructions);

                    // Skip the added instructions
                    i += preInstructions.size() + postInstructions.size();
                    break;
                }
            }
        }
    }
}
