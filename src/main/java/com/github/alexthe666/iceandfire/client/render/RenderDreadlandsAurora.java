package com.github.alexthe666.iceandfire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

// TODO
public class RenderDreadlandsAurora extends IRenderHandler {
    @SuppressWarnings("unused")
	private static final ResourceLocation AURORA_TEXTURES = new ResourceLocation("iceandfire:textures/environment/dread_aurora.png");

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        this.renderClouds(mc);
    }

    public void renderClouds(Minecraft mc) {

    }
}