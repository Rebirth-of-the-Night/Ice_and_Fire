package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitThunder extends ModifierTrait {
    private int level;

    public TraitThunder(int level) {
        super("thunder" + (level == 1 ? "" : level), 4528791, 1, 1);
        this.level = level;
    }

    @Override
    public boolean canApplyTogether(IToolMod toolmod) {
        String id = toolmod.getIdentifier();
        if (level == 1) {
            return !id.equals(TinkersCompat.LIGHTNING_II.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_II.getIdentifier()) && !id.equals(TinkersCompat.BURN_I.getIdentifier()) && !id.equals(TinkersCompat.BURN_II.getIdentifier());
        } else {
            return !id.equals(TinkersCompat.LIGHTNING_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_II.getIdentifier()) && !id.equals(TinkersCompat.BURN_I.getIdentifier()) && !id.equals(TinkersCompat.BURN_II.getIdentifier());
        }
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    	if(IceAndFire.CONFIG.lightningDragonsteelAbility) {
        boolean flag = true;
        if(player instanceof EntityPlayer) {
            if(((EntityPlayer)player).swingProgress > 0.2) {
                flag = false;
            }
        }
        if(!player.world.isRemote && flag) {
		EntityLightningBolt lightningBolt = new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false);
        target.world.addWeatherEffect(lightningBolt);
    	if(IceAndFire.CONFIG.saferBoltStrike) {
        lightningBolt.move(MoverType.SELF, target.posX - player.posX, target.posY, target.posZ - player.posZ); 
    	    }
        }
    }
    	if(IceAndFire.CONFIG.dragonsteelKnockback) {
        if (level >= 2) {
            target.knockBack(target, 1F, player.posX - target.posX, player.posZ - target.posZ);
            }
        }  
    }
}
