package com.github.alexthe666.iceandfire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

// TODO
public class RenderDreadlandsAurora extends IRenderHandler {
    private static final ResourceLocation AURORA_TEXTURES = new ResourceLocation("iceandfire:textures/environment/dread_aurora.png");

    private final int skyboxList = -1;

    
    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        this.renderClouds(mc);
    }

    public void renderClouds(Minecraft mc) {

    }
}