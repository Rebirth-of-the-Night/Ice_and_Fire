package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class ItemAmphithereMacuahuitl extends ItemSword {
    public ItemAmphithereMacuahuitl() {
        super(IafItemRegistry.amphithere_sword_tools);
        this.setTranslationKey("iceandfire.amphithere_macuahuitl");
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, "amphithere_macuahuitl");
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.iceandfire.legendary_weapon.desc"));
        tooltip.add(I18n.format("item.iceandfire.amphithere_macuahuitl.desc_0"));
        tooltip.add(I18n.format("item.iceandfire.amphithere_macuahuitl.desc_1"));
    }
}
