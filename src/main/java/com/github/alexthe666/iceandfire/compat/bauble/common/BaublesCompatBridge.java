package com.github.alexthe666.iceandfire.compat.bauble.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

public class BaublesCompatBridge {
    private static final String COMPAT_MOD_ID = "baubles";
    public static boolean isWearSpecificBauble(EntityLivingBase player, Item bauble) {
        if (Loader.isModLoaded(COMPAT_MOD_ID)) {
            return PlayerWearBaubleEvent.arePlayerWearBaubles(player, bauble);
        } else
            return false;
    }
}
