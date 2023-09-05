package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityDragonLightningCharge;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDragonLightningCharge extends Render<EntityDragonLightningCharge> {

    public static final ResourceLocation TEXTURE_CORE = new ResourceLocation("iceandfire:textures/models/lightningdragon/charge_core.png");
    public static final ResourceLocation LAYER_CHARGE = new ResourceLocation("iceandfire:textures/models/lightningdragon/charge.png");

    public RenderDragonLightningCharge(RenderManager manager) {
        super(manager);
    }

    public void doRender(EntityDragonLightningCharge entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float f = (float)entity.ticksExisted + partialTicks;
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(0F, 0.5F, 0F);
        GlStateManager.translate(0F, -0.25F, 0F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(yaw - 180, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * 20, 0.0F, 1.0F, 0.0F);
        new ModelDreadLichSkull().render(entity, 0, 0, partialTicks, 0, 0, 0.0625F);
        this.bindEntityTexture(entity);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(0F, 0.5F, 0F);
        GlStateManager.translate(0F, -0.25F, 0F);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(yaw - 180, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * 15, 0.0F, 1.0F, 0.0F);
        new ModelDreadLichSkull().render(entity, 0, 0, partialTicks, 0, 0, 0.0625F);
        this.bindTexture(LAYER_CHARGE);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(0F, 0.75F, 0F);
        GlStateManager.translate(0F, -0.25F, 0F);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.scale(2.5F, 2.5F, 2.5F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(yaw - 180, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * 10, 0.0F, 1.0F, 0.0F);
        new ModelDreadLichSkull().render(entity, 0, 0, partialTicks, 0, 0, 0.0625F);
        this.bindTexture(LAYER_CHARGE);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();   

        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityDragonLightningCharge entity) {
		return TEXTURE_CORE;
	}
}