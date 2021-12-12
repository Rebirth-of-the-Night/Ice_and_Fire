package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerGenericGlowing<T extends EntityLiving> implements LayerRenderer<T> {
    private final RenderLiving<T> render;
    private final ResourceLocation texture;

    public LayerGenericGlowing(RenderLiving<T> renderIn, ResourceLocation texture) {
        this.render = renderIn;
        this.texture = texture;
    }

    public void doRenderLayer(T gorgon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        StoneEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(gorgon, StoneEntityProperties.class);
        if (properties == null || !properties.isStone) {
            this.render.bindTexture(texture);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(!gorgon.isInvisible());
            
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);
            GlStateManager.enableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.render.getMainModel().render(gorgon, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            this.render.setLightmap(gorgon);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}