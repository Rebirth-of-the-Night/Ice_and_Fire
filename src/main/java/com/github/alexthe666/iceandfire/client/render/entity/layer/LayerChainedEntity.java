package com.github.alexthe666.iceandfire.client.render.entity.layer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerChainedEntity implements LayerRenderer<EntityLivingBase> {
    public LayerChainedEntity(Render<? extends Entity> renderIn) {
    }

    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

    }

    public boolean shouldCombineTextures() {
        return false;
    }
}