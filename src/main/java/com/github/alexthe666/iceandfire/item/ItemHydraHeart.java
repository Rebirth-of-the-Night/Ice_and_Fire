package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import com.github.alexthe666.iceandfire.compat.bauble.common.IafBaublesIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHydraHeart extends Item {
    public ItemHydraHeart() {
        super();
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey("iceandfire.hydra_heart");
        this.setRegistryName(IceAndFire.MODID, "hydra_heart");
        this.maxStackSize = 1;
        this.setMaxDamage(800); // placeholder so the item is damageable; real value comes from getMaxDamage(ItemStack)
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean unused2) {
        if(entity instanceof EntityPlayer && slot >= 0 && slot <= 8) {
            applyRegeneration(stack, (EntityPlayer) entity);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return IceAndFire.CONFIG.hydraHeartDurability;
    }

    public static void applyRegeneration(ItemStack stack, EntityLivingBase living) {
        double healthPercentage = living.getHealth() / Math.max(1, living.getMaxHealth());
        if(healthPercentage < 1.0D && !living.isPotionActive(MobEffects.REGENERATION)){
            int level = IceAndFire.CONFIG.hydraHeartRegenAmplifierBelow100;
            if(healthPercentage < 0.25D){
                level = IceAndFire.CONFIG.hydraHeartRegenAmplifierBelow25;
            } else if(healthPercentage < 0.5D){
                level = IceAndFire.CONFIG.hydraHeartRegenAmplifierBelow50;
            } else if(healthPercentage < 0.75D){
                level = IceAndFire.CONFIG.hydraHeartRegenAmplifierBelow75;
            }
            if(level >= 0) {
                living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, IceAndFire.CONFIG.hydraHeartRegenSeconds * 20, level, true, false));
                stack.damageItem(1, living);
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return IafBaublesIntegration.enabled() ? IafBaublesIntegration.createCharmProvider((s, living) -> applyRegeneration(s, living)) : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("item.iceandfire.legendary_weapon.desc"));
        tooltip.add(I18n.format("item.iceandfire.hydra_heart.desc_0"));
        tooltip.add(I18n.format("item.iceandfire.hydra_heart.desc_1"));
    }
}
