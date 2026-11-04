package com.github.alexthe666.iceandfire.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/** Replaces LLibrary's deleted patron-list download with an empty completed result. */
public class LLibraryPatronTransformer implements IClassTransformer {
    private static final String TARGET = "net.ilexiconn.llibrary.client.ClientProxy";
    private static final String DEAD_URL = "https://gist.githubusercontent.com/gegy1000/7a6d39cf7a2c1f794ffb9037e8146adc/raw/llibrary_patrons.json";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET.equals(transformedName)) {
            return basicClass;
        }
        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);
        boolean changed = false;
        for (MethodNode method : node.methods) {
            if (!"onPreInit".equals(method.name) || !"()V".equals(method.desc)) continue;
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (!(insn instanceof LdcInsnNode) || !DEAD_URL.equals(((LdcInsnNode) insn).cst)) continue;
                AbstractInsnNode next = insn.getNext();
                while (next != null && next.getOpcode() < 0) next = next.getNext();
                if (!(next instanceof MethodInsnNode)) continue;
                MethodInsnNode call = (MethodInsnNode) next;
                if (call.getOpcode() != Opcodes.INVOKESTATIC
                        || !"net/ilexiconn/llibrary/server/util/WebUtils".equals(call.owner)
                        || !"readURLAsync".equals(call.name)
                        || !"(Ljava/lang/String;)Lcom/google/common/util/concurrent/ListenableFuture;".equals(call.desc)) continue;
                // Same stack shape and return type; existing frames remain valid.
                ((LdcInsnNode) insn).cst = "[]";
                method.instructions.set(call, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/google/common/util/concurrent/Futures", "immediateFuture",
                        "(Ljava/lang/Object;)Lcom/google/common/util/concurrent/ListenableFuture;", false));
                changed = true;
            }
        }
        if (!changed) return basicClass;
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }
}
