package com.github.alexthe666.iceandfire.util;

import java.util.List;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningBolt;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtil {
    public static boolean getIsRepairable(Item.ToolMaterial toolMaterial,ItemStack toRepair, ItemStack repair, boolean toReturn) {
        ItemStack mat = toolMaterial.getRepairItemStack();
        if (toolMaterial == IafItemRegistry.silverTools) {
            NonNullList<ItemStack> silverItems = OreDictionary.getOres("ingotSilver");
            for (ItemStack ingot : silverItems) {
                if (OreDictionary.itemMatches(repair, ingot, false)) {
                    return true;
                }
            }
        }
        if (toolMaterial == IafItemRegistry.copperTools) {
            NonNullList<ItemStack> copperItems = OreDictionary.getOres("ingotCopper");
            for (ItemStack ingot : copperItems) {
                if (OreDictionary.itemMatches(repair, ingot, false)) {
                    return true;
                }
            }
        }
        if (!mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false)) return true;
        return toReturn;
    }
    
    public static void hitWithSilver(EntityLivingBase target, ToolMaterial toolMaterial, float damageVal) {
        if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            target.attackEntityFrom(DamageSource.MAGIC, toolMaterial.getAttackDamage() + damageVal);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void getSilverComment(List<String> tooltip) {
        tooltip.add(TextFormatting.GREEN + I18n.format("silvertools.hurt"));
    }
    
    public static void hitWithMyrmex(EntityLivingBase target, ToolMaterial toolMaterial, float damageVal) {
    	hitWithMyrmex(target, toolMaterial, damageVal, false);
    }
    
    public static void hitWithMyrmex(EntityLivingBase target, ToolMaterial toolMaterial, float damageVal, boolean isPoison) {
        if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD || target instanceof EntityDeathWorm) {
            target.attackEntityFrom(DamageSource.GENERIC, toolMaterial.getAttackDamage() + damageVal);
            if(isPoison) {
                target.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void getMyrmexComment(List<String> tooltip) {
    	getMyrmexComment(tooltip, false);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getMyrmexComment(List<String> tooltip, boolean isPoison) {
    	if(isPoison) {
            tooltip.add(TextFormatting.DARK_GREEN + I18n.format("myrmextools.poison"));
    	}
    	tooltip.add(TextFormatting.GREEN + I18n.format("myrmextools.hurt"));
    }
    
    public static void hitWithFireDragonsteel(EntityLivingBase target, EntityLivingBase attacker) {
        if (!IsImmune.toDragonFire(target)) {
	        target.setFire(15);
        }
        knockbackWithDragonsteel(target, attacker);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getFireDragonsteelComment(List<String> tooltip) {
    	getFireDragonsteelComment(tooltip, false);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getFireDragonsteelComment(List<String> tooltip, boolean isBone) {
    	if(isBone) {
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_fire.hurt1"));
    	}
        tooltip.add(TextFormatting.DARK_RED + I18n.format("dragon_sword_fire.hurt2"));
    }
    
    public static void hitWithIceDragonsteel(EntityLivingBase target, EntityLivingBase attacker) {
        if (!IsImmune.toDragonIce(target)) {
    		FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
            frozenProps.setFrozenFor(300);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 2));
        }
        knockbackWithDragonsteel(target, attacker);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getIceDragonsteelComment(List<String> tooltip) {
    	getIceDragonsteelComment(tooltip, false);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getIceDragonsteelComment(List<String> tooltip, boolean isBone) {
    	if(isBone) {
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_ice.hurt2"));
    	}
        tooltip.add(TextFormatting.AQUA + I18n.format("dragon_sword_ice.hurt2"));
    }
    
    public static void hitWithLightningDragonsteel(EntityLivingBase target, EntityLivingBase attacker) {
        boolean flag = true;
        if(attacker instanceof EntityPlayer) {
        	EntityPlayer player = (EntityPlayer)attacker;
            if(player.swingProgress > 0.2) {
                flag = false;
            }
        }
        if(!attacker.world.isRemote && flag && !target.isDead && !IsImmune.toDragonLightning(target)) {
        	EntityDragonLightningBolt dragonLightningBolt = new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, attacker);
        	target.world.spawnEntity(dragonLightningBolt);
        }
        knockbackWithDragonsteel(target, attacker);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getLightningDragonsteelComment(List<String> tooltip) {
    	getLightningDragonsteelComment(tooltip, false);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getLightningDragonsteelComment(List<String> tooltip, boolean isBone) {
    	if(isBone) {
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_lightning.hurt2"));
    	}
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("dragon_sword_lightning.hurt2"));
    }
    
    private static void knockbackWithDragonsteel(EntityLivingBase target, EntityLivingBase attacker) {
        if(IceAndFire.CONFIG.dragonsteelKnockback) {  
    		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
    	}
    }
}
