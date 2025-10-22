package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderGhost extends RenderLiving<EntityGhost> {

    public static final ResourceLocation TEXTURE_0 = new ResourceLocation("iceandfire:textures/models/ghost/ghost_white.png");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation("iceandfire:textures/models/ghost/ghost_blue.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation("iceandfire:textures/models/ghost/ghost_green.png");
    public static final ResourceLocation TEXTURE_SHOPPING_LIST = new ResourceLocation("iceandfire:textures/models/ghost/haunted_shopping_list.png");

    public RenderGhost(RenderManager renderManager) {
        super(renderManager, new ModelGhost(0.0F), 0.55F);
    }

    public static ResourceLocation getGhostOverlayForType(int ghost) {
        switch (ghost) {
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case -1:
                return TEXTURE_SHOPPING_LIST;
            default:
                return TEXTURE_0;
        }
    }

    @Override
    public void doRender(EntityGhost entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.shadowSize = 0.0F;
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected void renderModel(EntityGhost entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag = !entity.isInvisible();
        boolean flag1 = !flag && !entity.isInvisibleToPlayer(net.minecraft.client.Minecraft.getMinecraft().player);

        if (flag || flag1) {
            if (!this.bindEntityTexture(entity)) {
                return;
            }

            if (flag1) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            // Setup ghost rendering with proper blending
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();

            if (entity.isDaytimeMode()) {
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            } else {
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            }

            GlStateManager.depthMask(false);

            // Calculate alpha with proper partial ticks
            float alphaForRender = this.getAlphaForRender(entity, ageInTicks);

            // Set color with alpha
            GlStateManager.color(1.0F, 1.0F, 1.0F, alphaForRender);

            if (entity.isHauntedShoppingList()) {
                // Render shopping list
                this.renderShoppingList(entity, ageInTicks, alphaForRender);
            } else {
                // Render normal ghost model
                this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            }

            // Restore OpenGL state
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

            if (flag1) {
                GlStateManager.enableLighting();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }

    private void renderShoppingList(EntityGhost entity, float ageInTicks, float alpha) {
        GlStateManager.pushMatrix();

        // Bind shopping list texture
        this.bindTexture(TEXTURE_SHOPPING_LIST);

        // Position and animate
        float bobbing = MathHelper.sin(ageInTicks * 0.15F) * 0.1F;
        GlStateManager.translate(0, 0.8F + bobbing, 0);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Set color with alpha for shopping list
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        // Front face
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-1, -2, 0).tex(1.0, 0.0).endVertex();
        buffer.pos(1, -2, 0).tex(0.5, 0.0).endVertex();
        buffer.pos(1, 2, 0).tex(0.5, 1.0).endVertex();
        buffer.pos(-1, 2, 0).tex(1.0, 1.0).endVertex();
        tessellator.draw();

        // Back face
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-1, -2, 0).tex(0.0, 0.0).endVertex();
        buffer.pos(1, -2, 0).tex(0.5, 0.0).endVertex();
        buffer.pos(1, 2, 0).tex(0.5, 1.0).endVertex();
        buffer.pos(-1, 2, 0).tex(0.0, 1.0).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public float getAlphaForRender(EntityGhost entityIn, float partialTicks) {
        if (entityIn.isDaytimeMode()) {
            return MathHelper.clamp((101 - Math.min(entityIn.getDaytimeCounter(), 100)) / 100F, 0, 1);
        }
        return MathHelper.clamp((MathHelper.sin((entityIn.ticksExisted + partialTicks) * 0.1F) + 1F) * 0.5F + 0.1F, 0.5F, 1F);
    }

    @Override
    protected float getDeathMaxRotation(EntityGhost entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    protected void preRenderCallback(EntityGhost entitylivingbaseIn, float partialTickTime) {
        // Custom scaling or transformations can go here
    }

    @Override
    @Nullable
    protected ResourceLocation getEntityTexture(EntityGhost entity) {
        switch (entity.getColor()) {
            case 1:
                return TEXTURE_1;
            case 2:
                return TEXTURE_2;
            case -1:
                return TEXTURE_SHOPPING_LIST;
            default:
                return TEXTURE_0;
        }
    }
}