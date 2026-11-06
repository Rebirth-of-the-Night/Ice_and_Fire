package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Fallback switch for petrification / freeze rendering on GPUs or drivers that fail to draw the overlay layers.
 * When active, mobs are rendered with their normal model tinted instead of the stone/ice overlay textures.
 */
public final class SafeRender {

    private static boolean failed = false;

    private SafeRender() {
    }

    public static boolean isFallback() {
        return failed || IceAndFire.CONFIG.safeStoneIceRender;
    }

    public static void reportFailure(String what, Throwable t) {
        restoreState();
        if (!failed) {
            failed = true;
            IceAndFire.logger.error("Ice and Fire: " + what + " rendering failed, switching to safe fallback rendering.", t);
        }
    }

    /** Resets GL state that the stone/ice layers touch, so a failed draw cannot leak state. */
    public static void restoreState() {
        try {
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.disableNormalize();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable ignored) {
        }
    }
}
