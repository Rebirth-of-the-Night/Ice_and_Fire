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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemModAxe extends ItemAxe {

    public ItemModAxe(ToolMaterial toolmaterial, String gameName, String name) {
        super(ToolMaterial.DIAMOND);
        this.toolMaterial = toolmaterial;
        this.attackDamage = toolMaterial == IafItemRegistry.dragonsteel_fire_tools || toolMaterial == IafItemRegistry.dragonsteel_ice_tools || toolMaterial == IafItemRegistry.dragonsteel_lightning_tools? toolmaterial.getAttackDamage() + 8 : toolmaterial.getAttackDamage() + 5;
        this.attackSpeed = -3;
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
        if (this.toolMaterial == IafItemRegistry.copperTools) {
            NonNullList<ItemStack> copperItems = OreDictionary.getOres("ingotCopper");
            for (ItemStack ingot : copperItems) {
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
        if (this == IafItemRegistry.silver_axe) {
            if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                target.attackEntityFrom(DamageSource.MAGIC, 4.0F + toolMaterial.getAttackDamage() + 4.0F);
            }
        }
        if (this.toolMaterial == IafItemRegistry.myrmexChitin) {
            if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD || target instanceof EntityDeathWorm) {
                target.attackEntityFrom(DamageSource.GENERIC, toolMaterial.getAttackDamage() + 10.0F);
            }
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_fire_tools) {
        	target.setFire(15);
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_ice_tools) {
        	FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
        		frozenProps.setFrozenFor(300);
        		target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 2));
        		if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        			target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        		}
            }
        if (toolMaterial == IafItemRegistry.dragonsteel_lightning_tools) {
            boolean flag = true;
            if(attacker instanceof EntityPlayer) {
                if(((EntityPlayer)attacker).swingProgress > 0.2) {
                    flag = false;
                }
            }
            if(!attacker.world.isRemote && flag && !target.isDead) {
            EntityLightningBolt lightningBolt = new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false);
            if(IceAndFire.CONFIG.saferBoltStrike) {
                lightningBolt.move(MoverType.SELF, target.posX - attacker.posX, target.posY, target.posZ - attacker.posZ);
                }
            	target.world.addWeatherEffect(lightningBolt);
            }
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
                target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
            	}
        	}
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this == IafItemRegistry.silver_axe) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
        }
        if (this == IafItemRegistry.myrmex_desert_axe || this == IafItemRegistry.myrmex_jungle_axe) {
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

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getDestroySpeed(stack, state) : this.efficiency;
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return true;
    }
}
