package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningBolt;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.util.IsImmune;
import com.github.alexthe666.iceandfire.util.ItemUtil;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlchemySword extends ItemSword {
	private final int toolID;

    public ItemAlchemySword(ToolMaterial toolmaterial, String gameName, String name, int toolID) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName); 
        this.toolID = toolID;
    }

    
    /*
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (this == IafItemRegistry.dragonbone_sword_fire) {
            if (target instanceof EntityIceDragon) {
            	target.hurtResistantTime = 0;
                target.attackEntityFrom(DamageSource.IN_FIRE, 8.0F);
            	target.hurtResistantTime = 20;
            }
			
			if (!IsImmune.toDragonFire(target)) {
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
            	EntityDragonLightningBolt lightningBolt = new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, false, attacker);
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
            tooltip.add(TextFormatting.GREEN + I18n.format("dragon_sword_fire.hurt1"));
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
    
    */
    
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    	switch(this.toolID) {
    	case 0 :
            if (target instanceof EntityIceDragon) {
            	target.hurtResistantTime = 0;
                target.attackEntityFrom(DamageSource.IN_FIRE, 8.0F);
            	target.hurtResistantTime = 20;
            }
			if (!(target instanceof EntityCow) && !IsImmune.toDragonFire(target)) {
	            target.setFire(5);
            }
			if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
			break;
    	case 1 :
			if (target instanceof EntityFireDragon) {
                target.attackEntityFrom(DamageSource.DROWN, 8.0F);
            }
            if (!(target instanceof EntityHippogryph) && !IsImmune.toDragonIce(target)) {
	            FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
	            frozenProps.setFrozenFor(200);
	            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
	            target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 2));
            }
			if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
			break;
    	case 2:
            if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
                target.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 4.0F);
            }
            boolean flag = true;
            if(attacker instanceof EntityPlayer) {
            	EntityPlayer player = (EntityPlayer)attacker;
                if(player.swingProgress > 0.2) {
                    flag = false;
                }
            }
            if(!attacker.world.isRemote && flag && !target.isDead && !(target instanceof EntityCyclops) && !IsImmune.toDragonLightning(target)) {
            	EntityDragonLightningBolt dragonLightningBolt = new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, attacker);
            	target.world.spawnEntity(dragonLightningBolt);
            } 
            if(IceAndFire.CONFIG.dragonsteelKnockback) {
            	target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
            }
            break;
    	}
    	return super.hitEntity(stack, target, attacker);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	switch(this.toolID) {
    	case 0:
    		ItemUtil.getFireDragonsteelComment(tooltip, true);
    		break;
    	case 1:
    		ItemUtil.getIceDragonsteelComment(tooltip, true);
    		break;
    	case 2:
    		ItemUtil.getLightningDragonsteelComment(tooltip, true);
    		break;
    	}
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
