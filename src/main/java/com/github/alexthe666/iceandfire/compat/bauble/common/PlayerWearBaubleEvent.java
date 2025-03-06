package com.github.alexthe666.iceandfire.compat.bauble.common;

import baubles.api.BaublesApi;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class PlayerWearBaubleEvent {
    public static boolean arePlayerWearBaubles(EntityLivingBase player, Item bauble){
        return player instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) player, bauble) != -1;
    }
}
