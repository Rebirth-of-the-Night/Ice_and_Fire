package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemDragonFlesh extends ItemFood {

    int dragonType;

    public ItemDragonFlesh(int dragonType) {
        super(8, 0.8F, true);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey(getTransitionKeyForType(dragonType));
        this.setRegistryName(IceAndFire.MODID, getNameForType(dragonType));
        this.dragonType = dragonType;
    }
    private static String getNameForType(int dragonType) {
        switch (dragonType) {
            case 0:
                return "fire_dragon_flesh";
            case 1:
                return "ice_dragon_flesh";
            case 2:
                return "lightning_dragon_flesh";
        }
        return "fire_dragon_flesh";
    }
    private static String getTransitionKeyForType(int dragonType) {
        switch (dragonType) {
            case 0:
                return "iceandfire.fire_dragon_flesh";
            case 1:
                return "iceandfire.ice_dragon_flesh";
            case 2:
                return "iceandfire.lightning_dragon_flesh";
        }
        return "iceandfire.fire_dragon_flesh";
    }
    
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            if (dragonType == 0) {
                player.setFire(5);
            } else if(dragonType == 1) {
                player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
            } else {
                if(!player.world.isRemote) { 
                	EntityLightningBolt lightningBolt = new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false);
                    player.world.addWeatherEffect(lightningBolt);
                }
            }
        }
    }
}
