package com.github.alexthe666.iceandfire.compat;

import com.github.alexthe666.iceandfire.compat.frt.FRTCompat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.Loader;

public class FRTCompatBridge {
    private static final String COMPAT_MOD_ID = "fireresistancetiers";

    public static boolean loadResistantTweaks(EntityLivingBase entity) {
        if (Loader.isModLoaded(COMPAT_MOD_ID)) {
            return FRTCompat.isActuallyImmuneFire(entity);
        } else
            return entity.isPotionActive(MobEffects.FIRE_RESISTANCE);
    }
}