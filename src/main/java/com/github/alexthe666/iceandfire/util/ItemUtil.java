package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

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
    
    @SideOnly(Side.CLIENT)
    public static void getSilverComment(List<String> tooltip) {
        tooltip.add(TextFormatting.GREEN + I18n.format("silvertools.hurt"));
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
            if(frozenProps != null) frozenProps.setFrozenFor(80);
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
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_ice.hurt1"));
    	}
        tooltip.add(TextFormatting.AQUA + I18n.format("dragon_sword_ice.hurt2"));
    }
    
    @SideOnly(Side.CLIENT)
    public static void getLightningDragonsteelComment(List<String> tooltip) {
    	getLightningDragonsteelComment(tooltip, false);
    }
    
    @SideOnly(Side.CLIENT)
    public static void getLightningDragonsteelComment(List<String> tooltip, boolean isBone) {
    	if(isBone) {
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_lightning.hurt1"));
    	}
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("dragon_sword_lightning.hurt2"));
    }
    
    public static void knockbackWithDragonsteel(EntityLivingBase target, EntityLivingBase attacker) {
        if(IceAndFire.CONFIG.dragonsteelKnockback) {  
    		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
    	}
    }
}
