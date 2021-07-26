package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitFreeze extends ModifierTrait {

    private int level;

    public TraitFreeze(int level) {
        super("frost" + (level == 1 ? "" : level), 0XA3ECE8, 1, 1);
        this.level = level;
    }

    @Override
    public boolean canApplyTogether(IToolMod toolmod) {
        String id = toolmod.getIdentifier();
        return !id.equals(TinkersCompat.FREEZE_II.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_I.getIdentifier()) && !id.equals(TinkersCompat.BURN_I.getIdentifier()) && !id.equals(TinkersCompat.BURN_II.getIdentifier());
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    	if(IceAndFire.CONFIG.iceDragonsteelAbility) {
        FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
        if (frozenProps != null) {
            frozenProps.setFrozenFor(level == 1 ? 200 : 300);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 150 * (level), 2));
        }
        	if(IceAndFire.CONFIG.dragonsteelKnockback) { 
            if (level >= 2) {
                target.knockBack(target, 1F, player.posX - target.posX, player.posZ - target.posZ);
                }
            }
        }
    }
}