package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.enums.EnumBloodedDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface IElementalThornArmor {

    EnumDragonEgg getEggType();

    public default void applyEffect(EntityPlayer player, EntityLivingBase attacker) {
        if (isCooldownActive(player)) {
            return;
        }
        DragonType type = getEggType().dragonType;
        if (type == DragonType.FIRE) {
            if (attacker instanceof EntityIceDragon) {
                attacker.attackEntityFrom(IceAndFire.dragonFire, 13.5F);
            }
            attacker.setFire(5);
            attacker.knockBack(attacker, 1F, player.posX - attacker.posX, player.posZ - attacker.posZ);
        } else if (type == DragonType.ICE) {
            if (attacker instanceof EntityFireDragon) {
                attacker.attackEntityFrom(IceAndFire.dragonIce, 13.5F);
            }
            if (!player.world.isRemote) {
                FrozenEntityProperties capability = EntityPropertiesHandler.INSTANCE.getProperties(attacker, FrozenEntityProperties.class);
                if (capability != null) capability.setFrozenFor(200);
            }
            attacker.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
            attacker.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 2));
            attacker.knockBack(attacker, 1F, player.posX - attacker.posX, player.posZ - attacker.posZ);
        } else if (type == DragonType.LIGHTNING) {
            float amount = (float) IceAndFire.CONFIG.dragonAttackDamageLightning;
            if (attacker instanceof EntityFireDragon || attacker instanceof EntityIceDragon) {
                amount += 4f;
            }
            
            attacker.attackEntityFrom(IceAndFire.dragonLightning, amount);
            attacker.world.spawnEntity(new EntityDragonLightningBolt(attacker.world, attacker.posX, attacker.posY, attacker.posZ, player, attacker));
            attacker.knockBack(attacker, 1F, player.posX - attacker.posX, player.posZ - attacker.posZ);
        }
    }

    public static void applySetEffect(EntityPlayer player, EntityLivingBase attacker) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty() || !(helmet.getItem() instanceof IElementalThornArmor)) {
            return;
        }
        ItemStack chestplate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chestplate.isEmpty() || !(chestplate.getItem() instanceof IElementalThornArmor)) {
            return;
        }
        ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (leggings.isEmpty() || !(leggings.getItem() instanceof IElementalThornArmor)) {
            return;
        }
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (boots.isEmpty() || !(boots.getItem() instanceof IElementalThornArmor)) {
            return;
        }
        switch (player.world.rand.nextInt(4)) {
            case 0:
                ((IElementalThornArmor) helmet.getItem()).applyEffect(player, attacker);
                break;
            case 1:
                ((IElementalThornArmor) chestplate.getItem()).applyEffect(player, attacker);
                break;
            case 2:
                ((IElementalThornArmor) leggings.getItem()).applyEffect(player, attacker);
                break;
            default:
                ((IElementalThornArmor) boots.getItem()).applyEffect(player, attacker);
        }
    }

    default boolean isCooldownActive(EntityPlayer player) {
        Item item = EnumBloodedDragonArmor.armor_black.chestplate;
        if (player.getCooldownTracker().hasCooldown(item)) {
            return true;
        }
        player.getCooldownTracker().setCooldown(item, IceAndFire.CONFIG.bloodedDragonArmorSetEffectCooldown);
        return false;
    }
}
