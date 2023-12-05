package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCannoli extends ItemFood {

    public ItemCannoli() {
        super(20, 2.0F, false);
        this.setAlwaysEdible();
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey("iceandfire.cannoli");
        this.setRegistryName(IceAndFire.MODID, "cannoli");
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer livingEntity) {
        livingEntity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 3600, 2));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.format("item.iceandfire.cannoli.desc"));
    }
}