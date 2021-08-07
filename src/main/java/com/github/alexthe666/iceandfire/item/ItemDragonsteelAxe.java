package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemDragonsteelAxe extends ItemAxe {

    public ItemDragonsteelAxe(ToolMaterial toolmaterial, String gameName, String name) {
        super(ToolMaterial.DIAMOND);
        this.toolMaterial = toolmaterial;
        this.attackDamage = toolmaterial.getAttackDamage() + 8;
        this.attackSpeed = -3;
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
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
        if (this == IafItemRegistry.dragonsteel_fire_sword) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_fire.hurt2"));
        }
        if (this == IafItemRegistry.dragonsteel_ice_sword) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_ice.hurt2"));
        }
        if (this == IafItemRegistry.dragonsteel_lightning_sword) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_lightning.hurt2"));
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

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getDestroySpeed(stack, state) : this.efficiency;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return true;
    }
}