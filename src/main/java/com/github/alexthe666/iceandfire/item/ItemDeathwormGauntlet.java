package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.MiscEntityProperties;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeathwormGauntlet extends Item {

    public ItemDeathwormGauntlet(String color) {
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey("iceandfire.deathworm_gauntlet_" + color);
        this.maxStackSize = 1;
        this.setMaxDamage(500);
        this.setRegistryName(IceAndFire.MODID, "deathworm_gauntlet_" + color);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        playerIn.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(player, MiscEntityProperties.class);
        if (properties == null || properties.deathwormUseConsumed || !properties.deathwormGauntlet.isEmpty()
                || player.getCooldownTracker().hasCooldown(this)) {
            return;
        }

        properties.deathwormUseConsumed = true;
        properties.deathwormGauntlet = stack;
        properties.deathwormGauntletHand = player.getActiveHand();
        stack.setTagInfo("HolderID", new NBTTagInt(player.getEntityId()));
        player.getCooldownTracker().setCooldown(this, 10);
        player.playSound(IafSoundRegistry.DEATHWORM_ATTACK, 1F, 1F);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, MiscEntityProperties.class);
        if (properties != null) {
            properties.deathwormUseConsumed = false;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        // Durability sync can replace the stack while the player is still holding use.
        return oldStack.getItem() == newStack.getItem();
    }

    public static void tickLunge(EntityPlayer player) {
        MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(player, MiscEntityProperties.class);
        if (properties == null) {
            return;
        }
        if (!player.isHandActive() || !(player.getActiveItemStack().getItem() instanceof ItemDeathwormGauntlet)) {
            properties.deathwormUseConsumed = false;
        }

        properties.prevDeathwormLungeTicks = properties.deathwormLungeTicks;
        ItemStack gauntlet = properties.deathwormGauntlet;
        if (gauntlet == ItemStack.EMPTY) {
            return;
        }

        ItemStack held = player.getHeldItem(properties.deathwormGauntletHand);
        // Inventory sync can replace the client stack after a durability change.
        if (player.world.isRemote && held != gauntlet && !held.isEmpty() && held.getItem() == gauntlet.getItem()) {
            gauntlet = held;
            properties.deathwormGauntlet = held;
            held.setTagInfo("HolderID", new NBTTagInt(player.getEntityId()));
        }
        if (!player.isEntityAlive() || gauntlet.isEmpty() || held != gauntlet) {
            finishLunge(properties);
            return;
        }

        if (properties.deathwormReceded) {
            properties.deathwormLungeTicks -= 4;
            if (properties.deathwormLungeTicks <= 0) {
                finishLunge(properties);
            }
        } else {
            properties.deathwormLungeTicks += 4;
            if (properties.deathwormLungeTicks > 20) {
                properties.deathwormReceded = true;
                if (!player.world.isRemote) {
                    strike(player, gauntlet);
                }
            }
        }
    }

    private static void finishLunge(MiscEntityProperties properties) {
        properties.deathwormGauntlet.removeSubCompound("HolderID");
        properties.deathwormGauntlet = ItemStack.EMPTY;
        properties.deathwormLungeTicks = 0;
        properties.deathwormReceded = false;
    }

    private static void strike(EntityPlayer player, ItemStack gauntlet) {
        Vec3d look = player.getLook(1.0F).normalize();
        AxisAlignedBB area = new AxisAlignedBB(player.posX, player.posY, player.posZ,
                player.posX, player.posY, player.posZ).grow(5.0D);
        int hits = 0;
        for (EntityLiving target : player.world.getEntitiesWithinAABB(EntityLiving.class, area)) {
            if (EntityGorgon.isPetrifiedEntity(target)) {
                continue;
            }
            Vec3d offset = new Vec3d(target.posX - player.posX, target.posY - player.posY, target.posZ - player.posZ);
            double distance = offset.length();
            if (look.dotProduct(offset.normalize()) > 1.0D - 0.5D / distance && player.canEntityBeSeen(target)) {
                hits++;
                target.attackEntityFrom(DamageSource.causePlayerDamage(player), 3F);
                target.knockBack(target, 0.5F, offset.x, offset.z);
            }
        }
        if (hits > 0) {
            gauntlet.damageItem(hits, player);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("item.iceandfire.legendary_weapon.desc"));
        tooltip.add(I18n.format("item.iceandfire.deathworm_gauntlet.desc_0"));
        tooltip.add(I18n.format("item.iceandfire.deathworm_gauntlet.desc_1"));
    }
}
