package com.github.alexthe666.iceandfire.compat.frt;

import com.eksekk.fireresistancetiers.config.ConfigFields;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FRTCompat {
    public static boolean isActuallyImmuneFire(EntityLivingBase entity) {
        PotionEffect effect = entity.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
        if (effect != null) {
            return effect.getAmplifier() + 1 >= ConfigFields.numberOfTiers;
        } else {
            return false;
        }
    }
}