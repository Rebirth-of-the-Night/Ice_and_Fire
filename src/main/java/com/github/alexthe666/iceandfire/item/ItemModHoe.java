package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

import java.util.List;

public class ItemModHoe extends ItemHoe {

    public ItemModHoe(ToolMaterial toolmaterial, String gameName, String name) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.toolMaterial.getRepairItemStack();
        if (this.toolMaterial == IafItemRegistry.silverTools) {
            NonNullList<ItemStack> silverItems = OreDictionary.getOres("ingotSilver");
            for (ItemStack ingot : silverItems) {
                if (OreDictionary.itemMatches(repair, ingot, false)) {
                    return true;
                }
            }
        }
        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (this == IafItemRegistry.silver_hoe) {
            if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), toolMaterial.getAttackDamage() + 3.0F);
            }
        }
        if (this.toolMaterial == IafItemRegistry.myrmexChitin) {
            if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD || target instanceof EntityDeathWorm) {
                target.attackEntityFrom(DamageSource.GENERIC, toolMaterial.getAttackDamage() + 6.0F);
            }
        }
        if (this == IafItemRegistry.myrmex_desert_sword_venom || this == IafItemRegistry.myrmex_jungle_sword_venom) {
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_fire_tools) {
        	if(IceAndFire.CONFIG.fireDragonsteelAbility) {
        		target.setFire(15);
        	}
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_ice_tools) {
        	if(IceAndFire.CONFIG.iceDragonsteelAbility) {
        		FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
        		frozenProps.setFrozenFor(300);
        		target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 2));
        	}
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
            target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_lightning_tools) {
        	if(IceAndFire.CONFIG.lightningDragonsteelAbility) {
            boolean flag = true;
            if(attacker instanceof EntityPlayer) {
                if(((EntityPlayer)attacker).swingProgress > 0.2) {
                    flag = false;
                }
            }
            if(!attacker.world.isRemote && flag) {
			EntityLightningBolt lightningBolt = new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false);
            target.world.addWeatherEffect(lightningBolt);
        	if(IceAndFire.CONFIG.saferBoltStrike) {
            lightningBolt.move(MoverType.SELF, target.posX - attacker.posX, target.posY, target.posZ - attacker.posZ); 
        	    }
            }
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
                target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
            	} 
        	}
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this == IafItemRegistry.silver_hoe) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
        }
        if (this == IafItemRegistry.myrmex_desert_hoe || this == IafItemRegistry.myrmex_jungle_hoe) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_fire_tools) {
            tooltip.add(TextFormatting.DARK_RED + StatCollector.translateToLocal("dragon_sword_fire.hurt2"));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_ice_tools) {
            tooltip.add(TextFormatting.AQUA + StatCollector.translateToLocal("dragon_sword_ice.hurt2"));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_lightning_tools) {
            tooltip.add(TextFormatting.DARK_PURPLE + StatCollector.translateToLocal("dragon_sword_lightning.hurt2"));
        }
    }
}
