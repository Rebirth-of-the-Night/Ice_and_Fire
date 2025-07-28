package com.github.alexthe666.iceandfire.compat.spartanweaponry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

/*
 *
 * Spartan Weaponry Compat From Ice and Fire - RLCraft Edition
 * Code by Shivaxi, FonnyMunkey and Kotlin-Programmer
 * Under LGPL-3.0 License
 * Port by keletu
 *
 * */
public class SpartanWeaponryCompat {
    public static ResourceLocation RETURN_ENCHANTMENT = new ResourceLocation("spartanweaponry","return");

    public static boolean hasReturnEnchantment() {
        return getReturnEnchantment() != null;
    }

    @Nullable
    public static Enchantment getReturnEnchantment() {
        return Enchantment.REGISTRY.getObject(RETURN_ENCHANTMENT);
    }

    @Nullable
    public static SoundEvent getReturnSoundEvent() {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("spartanweaponry", "throwing_weapon_return"));
    }

    @Nullable
    public static SoundEvent getThrowingWeaponSoundEvent() {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("spartanweaponry", "throwing_weapon_throw"));
    }
}