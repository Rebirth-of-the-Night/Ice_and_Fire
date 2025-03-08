package com.github.alexthe666.iceandfire.compat.bauble.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;

public class BaublesCompatBridgeClient {
    private static final String COMPAT_MOD_ID = "baubles";

    public static void loadBaublesClientModels() {
        if (Loader.isModLoaded(COMPAT_MOD_ID)) {
            addRenderLayers();
        }
    }

    public static void addRenderLayers() {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

        addLayersToSkin(skinMap.get("default"));
        addLayersToSkin(skinMap.get("slim"));
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer) {
        renderPlayer.addLayer(new LayerBauble(renderPlayer));
    }
}
