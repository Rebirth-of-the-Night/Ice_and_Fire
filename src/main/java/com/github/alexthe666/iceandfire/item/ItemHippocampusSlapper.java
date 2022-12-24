package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class ItemHippocampusSlapper extends ItemSword {

    public ItemHippocampusSlapper() {
        super(IafItemRegistry.hippocampus_sword_tools);
        this.setTranslationKey("iceandfire.hippocampus_slapper");
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, "hippocampus_slapper");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.iceandfire.legendary_weapon.desc"));
        tooltip.add(I18n.format("item.iceandfire.hippocampus_slapper.desc_0"));
        tooltip.add(I18n.format("item.iceandfire.hippocampus_slapper.desc_1"));
    }
}