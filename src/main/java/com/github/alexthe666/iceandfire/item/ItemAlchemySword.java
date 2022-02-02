package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlchemySword extends ItemSword {

    public ItemAlchemySword(ToolMaterial toolmaterial, String gameName, String name) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (this == IafItemRegistry.dragonbone_sword_fire) {
            if (target instanceof EntityIceDragon) {
                target.attackEntityFrom(DamageSource.IN_FIRE, 13.5F);
            }
			
			if (!(target instanceof EntityCow)) {
	            target.setFire(5);
            }
			
			if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (this == IafItemRegistry.dragonbone_sword_ice) {
			if (target instanceof EntityFireDragon) {
                target.attackEntityFrom(DamageSource.DROWN, 13.5F);
            }
			
            if (!(target instanceof EntityHippogryph)) {
	            FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
	            frozenProps.setFrozenFor(200);
	            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
	            target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 2));
            }
			
			if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (this == IafItemRegistry.dragonbone_sword_lightning) {
            if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
                target.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 9.5F);
            }
            
            boolean flag = true;
            if(attacker instanceof EntityPlayer) {
                if(((EntityPlayer)attacker).swingProgress > 0.2) {
                    flag = false;
                }
            } 
            
            if(!attacker.world.isRemote && flag && !target.isDead && !(target instanceof EntityCyclops)) {
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
        tooltip.add(I18n.format("item.iceandfire.legendary_weapon.desc"));
        if (this == IafItemRegistry.dragonbone_sword_fire) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_fire.hurt1"));
            tooltip.add(TextFormatting.DARK_RED + StatCollector.translateToLocal("dragon_sword_fire.hurt2"));
        }
        if (this == IafItemRegistry.dragonbone_sword_ice) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_ice.hurt1"));
            tooltip.add(TextFormatting.AQUA + StatCollector.translateToLocal("dragon_sword_ice.hurt2"));
        }
        if (this == IafItemRegistry.dragonbone_sword_lightning) {
        	tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("dragon_sword_lightning.hurt1"));
            tooltip.add(TextFormatting.DARK_PURPLE + StatCollector.translateToLocal("dragon_sword_lightning.hurt2"));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
