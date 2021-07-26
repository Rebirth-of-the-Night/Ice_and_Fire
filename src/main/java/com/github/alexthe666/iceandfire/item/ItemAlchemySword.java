package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
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

import javax.annotation.Nullable;

import java.util.List;

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
        	if(IceAndFire.CONFIG.fireDragonsteelAbility) {
            if (target instanceof EntityIceDragon) {
                target.attackEntityFrom(DamageSource.IN_FIRE, 13.5F);
            }
            target.setFire(5);
        	}
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        if (this == IafItemRegistry.dragonbone_sword_ice) {
        	if(IceAndFire.CONFIG.iceDragonsteelAbility) {
            if (target instanceof EntityFireDragon) {
                target.attackEntityFrom(DamageSource.DROWN, 13.5F);
            }
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
            lightningBolt.move(MoverType.SELF, target.posX - attacker.posX, target.posY, target.posZ - attacker.posZ); 
            }
            if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
                target.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 9.5F);
            }
        }
        	if(IceAndFire.CONFIG.dragonsteelKnockback) {  
        		target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        	}
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
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
