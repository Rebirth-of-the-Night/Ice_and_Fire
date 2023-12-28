package com.github.alexthe666.iceandfire.compat;

import com.github.alexthe666.iceandfire.compat.spartanandfire.ItemRegistryCompatSFire;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class SAFCompatBridge {
    private static final String SAF_MOD_ID = "spartanfire";

    public static void loadSpartanAndLightning() {
        if (Loader.isModLoaded(SAF_MOD_ID)) {
            MinecraftForge.EVENT_BUS.register(ItemRegistryCompatSFire.class);
        }
    }


}
