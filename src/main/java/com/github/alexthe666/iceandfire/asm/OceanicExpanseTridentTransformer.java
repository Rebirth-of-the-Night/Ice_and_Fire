package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/** Adds Tide Trident eligibility to OE versions without configurable trident items. */
public class OceanicExpanseTridentTransformer implements IClassTransformer {
    private static final String PREFIX = "com.sirsquidly.oe.enchantment.Enchantment";
    private static final String STACK_PREDICATE = "(Lnet/minecraft/item/ItemStack;)Z";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null || !((PREFIX + "Channeling").equals(transformedName)
                || (PREFIX + "Impaling").equals(transformedName)
                || (PREFIX + "Loyalty").equals(transformedName)
                || (PREFIX + "Riptide").equals(transformedName))) {
            return bytes;
        }
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        for (MethodNode method : node.methods) {
            if ("canApplyAtEnchantingTable".equals(method.name) && STACK_PREDICATE.equals(method.desc)) {
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction instanceof MethodInsnNode) {
                        MethodInsnNode call = (MethodInsnNode) instruction;
                        if ("com/sirsquidly/oe/init/OEEnchants".equals(call.owner)
                                && "canApplyTridentEnchantment".equals(call.name)
                                && STACK_PREDICATE.equals(call.desc)) {
                            return bytes;
                        }
                    }
                }
                LabelNode original = new LabelNode();
                InsnList hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/github/alexthe666/iceandfire/compat/OceanicExpanseCompat", "isTideTrident", STACK_PREDICATE, false));
                hook.add(new JumpInsnNode(Opcodes.IFEQ, original));
                hook.add(new InsnNode(Opcodes.ICONST_1));
                hook.add(new InsnNode(Opcodes.IRETURN));
                hook.add(original);
                hook.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                method.instructions.insert(hook);
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
