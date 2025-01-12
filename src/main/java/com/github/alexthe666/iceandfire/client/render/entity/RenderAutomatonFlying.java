package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelAutomatonFlying;
import com.github.alexthe666.iceandfire.entity.EntityAutomatonFlying;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderAutomatonFlying extends RenderLiving<EntityAutomatonFlying> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/automaton/automaton_flying.png");

    public RenderAutomatonFlying(RenderManager renderManager) {
        super(renderManager, new ModelAutomatonFlying(), 0.8F);

    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityAutomatonFlying entity) {
        return TEXTURE;
    }
}
