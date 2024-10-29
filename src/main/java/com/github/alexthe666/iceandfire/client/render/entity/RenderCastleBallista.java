package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelBallista;
import com.github.alexthe666.iceandfire.entity.EntityCastleBallista;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


@SideOnly(Side.CLIENT)
public class RenderCastleBallista extends RenderLiving<EntityCastleBallista>
{
    public static final ResourceLocation xz = new ResourceLocation("iceandfire", "textures/models/ballista/ballista.png");

    public RenderCastleBallista(RenderManager rm) {
        super(rm, new ModelBallista(), 1.0f);
    }

    protected float getSwingProgress(EntityCastleBallista e, float p_77040_2_) {
        e.loadProgressForRender = e.getSwingProgress(p_77040_2_);
        e.renderYawOffset = 0.0F;
        e.prevRenderYawOffset = 0.0F;
        return super.getSwingProgress(e, p_77040_2_);
    }

    protected void preRenderCallback(EntityCastleBallista entitylivingbaseIn, float partialTickTime) {
        entitylivingbaseIn.renderYawOffset = 0.0F;
        entitylivingbaseIn.prevRenderYawOffset = 0.0F;
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    @Override
    public void doRender(EntityCastleBallista entity, double x, double y, double z, float entityYaw, float partialTicks) {
        bindTexture(xz);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, z);
        GlStateManager.pushMatrix();

        GlStateManager.popMatrix();
        GlStateManager.translate(0, 0, 0);
        GlStateManager.rotate(entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks,
                0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, 0, 0);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCastleBallista entityCastleBallista) {
        return xz;
    }
}