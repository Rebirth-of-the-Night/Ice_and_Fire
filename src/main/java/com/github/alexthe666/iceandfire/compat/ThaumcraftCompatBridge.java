package com.github.alexthe666.iceandfire.compat;

import com.github.alexthe666.iceandfire.compat.thaumcraft.ThaumcraftCompat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Joseph on 6/23/2018.
 */
public class ThaumcraftCompatBridge {

    public static ThaumcraftCompat INSTANCE;
    private static final String TC_MOD_ID = "thaumcraft";

    public static void loadThaumcraftCompat() {
        if (Loader.isModLoaded(TC_MOD_ID)) {
            INSTANCE = new ThaumcraftCompat();
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }
    }

    // Specifically for Thaumic Additions removing I&F's Draco aspect from the registry.
    public static void finalizeThaumcraftCompat() {
        if (Loader.isModLoaded(TC_MOD_ID)) {
            INSTANCE.remapDraco();
        }
    }
}