package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelBallistaArrow;
import com.github.alexthe666.iceandfire.entity.EntityBallistaArrow;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderBallistaArrow extends Render<EntityBallistaArrow> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/ballista/ballista_arrow.png");

    private final ModelBallistaArrow skeletonHeadModel = new ModelBallistaArrow();

    public RenderBallistaArrow(RenderManager renderManager) {
        super(renderManager);
    }

    private float getRenderYaw(float p_82400_1_, float p_82400_2_, float p_82400_3_) {
        float f;
        for(f = p_82400_2_ - p_82400_1_; f < -180.0F; f += 360.0F) {
        }

        while(f >= 180.0F) {
            f -= 360.0F;
        }

        return p_82400_1_ + p_82400_3_ * f;
    }

    public void doRender(EntityBallistaArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        float f = this.getRenderYaw(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.translate((float)x, (float)y + 0.5F , (float)z);
        float f2 = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        this.bindEntityTexture(entity);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.skeletonHeadModel.render(entity, 0.0F, 0.0F, 0.0F, f, f1, 0.0625F);
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBallistaArrow entityBallistaArrow) {
        return TEXTURE;
    }
}
