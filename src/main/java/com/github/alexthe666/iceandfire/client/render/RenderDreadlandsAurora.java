package com.github.alexthe666.iceandfire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

final class RenderDreadlandsAurora {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/environment/dread_aurora.png");
    private static final int SEGMENTS = 32;

    private RenderDreadlandsAurora() {
    }

    static void render(Minecraft mc, float phase, float visibility) {
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        mc.getTextureManager().bindTexture(TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();

        drawRibbon(tessellator, phase, 0.3F, 18.0F, visibility * 0.55F);
        drawRibbon(tessellator, phase, 2.6F, 24.0F, visibility * 0.4F);
        drawRibbon(tessellator, phase, 4.7F, 16.0F, visibility * 0.45F);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableTexture2D();
    }

    private static void drawRibbon(Tessellator tessellator, float phase, float direction, float height, float opacity) {
        BufferBuilder buffer = tessellator.getBuffer();
        // Two strips meet at the bright fold and fade away above and below it.
        for (int band = 0; band < 2; band++) {
            buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (int segment = 0; segment <= SEGMENTS; segment++) {
                float u = (float) segment / SEGMENTS;
                float angle = direction + (u - 0.5F) * 1.9F;
                float wave = u * 12.0F + phase + direction;
                float radius = 56.0F + 6.0F * MathHelper.sin(wave);
                float x = radius * MathHelper.cos(angle);
                float z = radius * MathHelper.sin(angle);
                float fold = height + 3.0F * MathHelper.sin(wave + 1.0F);
                float alpha = opacity * MathHelper.sin(u * (float) Math.PI)
                        * (0.85F + 0.15F * MathHelper.sin(wave - phase * 2.0F));
                if (band == 0) {
                    buffer.pos(x, fold + 23.0F, z).tex(u, 0.0D).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
                    buffer.pos(x, fold, z).tex(u, 0.72D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
                } else {
                    buffer.pos(x, fold, z).tex(u, 0.72D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
                    buffer.pos(x, fold - 7.0F, z).tex(u, 1.0D).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
                }
            }
            tessellator.draw();
        }
    }
}
