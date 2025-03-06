package com.github.alexthe666.iceandfire.compat.bauble.client;

import baubles.api.BaublesApi;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class LayerBauble implements LayerRenderer<EntityPlayer> {
    private static final ResourceLocation BLINDFOLD = new ResourceLocation(IceAndFire.MODID, "textures/models/armor/blindfold_layer_1.png");
    private static final ResourceLocation EAR_PLUGS = new ResourceLocation(IceAndFire.MODID, "textures/models/armor/earplugs_layer_1.png");
    protected RenderPlayer renderPlayer;
    protected ModelPlayer modelPlayer;
    protected boolean slim;

    public LayerBauble(RenderPlayer renderPlayer) {
        this(renderPlayer, false);
    }

    public LayerBauble(RenderPlayer renderPlayer, boolean slim) {
        this.renderPlayer = renderPlayer;
        this.modelPlayer = renderPlayer.getMainModel();
        this.slim = slim;
    }

    @Override
    public final void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //if(player.getActivePotionEffect(MobEffects.INVISIBILITY) != null) return;

        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();

        GlStateManager.pushMatrix();
        if (BaublesApi.isBaubleEquipped(player, IafItemRegistry.earplugs) != -1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(EAR_PLUGS);

            if (player.isSneaking())
                GlStateManager.translate(0, 0.2F, 0);
            modelPlayer.bipedHead.postRender(scale);
            new ModelHeadBauble().render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (BaublesApi.isBaubleEquipped(player, IafItemRegistry.blindfold) != -1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(BLINDFOLD);

            if (player.isSneaking())
                GlStateManager.translate(0, 0.2F, 0);
            modelPlayer.bipedHead.postRender(scale);
            new ModelHeadBauble().render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}