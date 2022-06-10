package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningBolt;

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
        boolean flag = true;
        if(player instanceof EntityPlayer) {
            if(((EntityPlayer)player).swingProgress > 0.2) {
                flag = false;
            }
        }
        if(!player.world.isRemote && flag && !target.isDead) {
        	EntityDragonLightningBolt dragonLightningBolt = new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, player);
        	target.world.spawnEntity(dragonLightningBolt);
        }
        if (level >= 2 && IceAndFire.CONFIG.dragonsteelKnockback) {
            target.knockBack(target, 1F, player.posX - target.posX, player.posZ - target.posZ);
        }
    }
}