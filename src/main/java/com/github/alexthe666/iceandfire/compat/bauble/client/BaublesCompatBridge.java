package com.github.alexthe666.iceandfire.compat.bauble.client;

import com.github.alexthe666.iceandfire.compat.bauble.common.PlayerWearBaubleEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

import java.util.Map;

public class BaublesCompatBridge {
    private static final String COMPAT_MOD_ID = "baubles";

    public static boolean isWearSpecificBauble(EntityLivingBase player, Item bauble) {
        if (Loader.isModLoaded(COMPAT_MOD_ID)) {
            return PlayerWearBaubleEvent.arePlayerWearBaubles(player, bauble);
        } else
            return false;
    }

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
