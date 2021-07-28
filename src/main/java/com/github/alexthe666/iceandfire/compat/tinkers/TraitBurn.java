package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitBurn extends ModifierTrait {

    private int level;

    public TraitBurn(int level) {
        super("flame" + (level == 1 ? "" : level), 0XB53007, 1, 1);
        this.level = level;
    }

    @Override
    public boolean canApplyTogether(IToolMod toolmod) {
        String id = toolmod.getIdentifier();
        if (level == 1) {
            return !id.equals(TinkersCompat.BURN_II.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_II.getIdentifier());
        } else {
            return !id.equals(TinkersCompat.BURN_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_I.getIdentifier()) && !id.equals(TinkersCompat.FREEZE_II.getIdentifier());
        }
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    	if(IceAndFire.CONFIG.fireDragonsteelAbility) {
        if(IceAndFife.CONFIG.dragonsCustomDamageSourceUsage) {
        target.attackEntityFrom(IceAndFire.dragonFire, level * 2F);
        } else {
        target.attackEntityFrom(DamageSource.IN_FIRE, level * 2F);
        }
        target.setFire(level == 1 ? 10 : 15);
    	}
    	if(IceAndFire.CONFIG.dragonsteelKnockback) { 
        if (level >= 2) {
            target.knockBack(target, 1F, player.posX - target.posX, player.posZ - target.posZ);
            }
        }
    }

}
