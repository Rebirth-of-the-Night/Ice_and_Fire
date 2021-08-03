package com.github.alexthe666.iceandfire.client.render.entity;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningCharge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

public class RenderDragonLightningCharge extends Render<EntityDragonLightningCharge> {

    public static final ResourceLocation TEXTURE_CORE = new ResourceLocation("iceandfire:textures/models/lightningdragon/charge_core.png");
    public static final ResourceLocation LAYER_CHARGE = new ResourceLocation("iceandfire:textures/models/lightningdragon/charge.png");
    private static final ModelDreadLichSkull MODEL_SPIRIT = new ModelDreadLichSkull();

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
        this.bindEntityTexture(entity);
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
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255.0F, 0.0F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(yaw - 180, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * 20, 0.0F, 1.0F, 0.0F);
        new ModelDreadLichSkull().render(entity, 0, 0, partialTicks, 0, 0, 0.0625F);
        this.bindTexture(LAYER_CHARGE);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(0F, 0.5F, 0F);
        GlStateManager.translate(0F, -0.25F, 0F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255.0F, 0.0F);
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
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.scale(2.5F, 2.5F, 2.5F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255.0F, 0.0F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(yaw - 180, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * 10, 0.0F, 1.0F, 0.0F);
        new ModelDreadLichSkull().render(entity, 0, 0, partialTicks, 0, 0, 0.0625F);
        this.bindTexture(LAYER_CHARGE);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();   
        
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private float interpolateValue(float start, float end, float pct) {
        return start + (end - start) * pct;
    }

    @Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityDragonLightningCharge entity) {
		return TEXTURE_CORE;
	}
}