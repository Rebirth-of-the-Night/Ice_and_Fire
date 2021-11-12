package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.FrozenEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModSword extends ItemSword {

    private final Item.ToolMaterial toolMaterial;

    public ItemModSword(ToolMaterial toolmaterial, String gameName, String name) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName);
        this.toolMaterial = toolmaterial;
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
        if (this == IafItemRegistry.silver_sword) {
            if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                target.attackEntityFrom(DamageSource.MAGIC, 3.0F + this.getAttackDamage() + 3.0F);
            }
        }
        if (this.toolMaterial == IafItemRegistry.myrmexChitin) {
            if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD || target instanceof EntityDeathWorm) {
                target.attackEntityFrom(DamageSource.GENERIC, 3.0F + this.getAttackDamage() + 5.0F);
            }
        }
        if (this == IafItemRegistry.myrmex_desert_sword_venom || this == IafItemRegistry.myrmex_jungle_sword_venom) {
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_fire_tools) {
	        if (!(target instanceof EntityCow)) {
		        target.setFire(15);
	        }
            target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_ice_tools) {
        	if (!(target instanceof EntityHippogryph)) {
        		FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(target, FrozenEntityProperties.class);
	            frozenProps.setFrozenFor(300);
	            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 2));
	        }
            target.knockBack(target, 1F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this == IafItemRegistry.silver_sword) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
        }
        if (this == IafItemRegistry.myrmex_desert_sword_venom || this == IafItemRegistry.myrmex_jungle_sword_venom) {
            tooltip.add(TextFormatting.DARK_GREEN + StatCollector.translateToLocal("myrmextools.poison"));
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
        }
        if (this == IafItemRegistry.myrmex_desert_sword || this == IafItemRegistry.myrmex_jungle_sword) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("myrmextools.hurt"));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_fire_tools) {
            tooltip.add(TextFormatting.DARK_RED + StatCollector.translateToLocal("dragon_sword_fire.hurt2"));
        }
        if (toolMaterial == IafItemRegistry.dragonsteel_ice_tools) {
            tooltip.add(TextFormatting.AQUA + StatCollector.translateToLocal("dragon_sword_ice.hurt2"));
        }
    }
}
