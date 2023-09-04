package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningBolt;
import com.github.alexthe666.iceandfire.util.IsImmune;
import com.github.alexthe666.iceandfire.util.ItemUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
        if(!player.world.isRemote && player.swingProgress < 0.2 && !target.isDead) {
        	target.world.spawnEntity(new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, player, target));
        }
        if(level > 1) {
        	ItemUtil.knockbackWithDragonsteel(target, player);
        }
    }
    
    
    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    	if(!IsImmune.toDragonLightning(target) && player instanceof EntityPlayer && player.swingProgress == 0) {
            return newDamage + (float)IceAndFire.CONFIG.dragonAttackDamageLightning / 2 * level;
        }
    	return newDamage;
    }
}