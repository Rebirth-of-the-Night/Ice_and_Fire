package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.event.FishingRippleEvents;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemFishingSpear extends Item {

    public static final int CHARGE_TICKS = 40;
    public static final int COOLDOWN_TICKS = 60;

    public ItemFishingSpear() {
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey("iceandfire.fishing_spear");
        this.maxStackSize = 1;
        this.setMaxDamage(64);
        this.setRegistryName(IceAndFire.MODID, "fishing_spear");
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (worldIn.isRemote || !(entityLiving instanceof EntityPlayer)
                || getMaxItemUseDuration(stack) - timeLeft < CHARGE_TICKS) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityLiving;
        if (player.getCooldownTracker().hasCooldown(this)) {
            return;
        }
        RayTraceResult hit = traceWater(worldIn, player);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK
                || !isFishingWater(worldIn, hit.getBlockPos())) {
            return;
        }
        // Failed catches still consume durability and start the three-second cooldown.
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        stack.damageItem(1, player);
        player.addStat(StatList.getObjectUseStats(this));
        if (FishingRippleEvents.consumeRipple(worldIn, hit.hitVec) || worldIn.rand.nextInt(4) == 0) {
            int roll = worldIn.rand.nextInt(100);
            int fish = roll < 75 ? 0 : roll < 98 ? 1 : roll < 99 ? 2 : 3;
            BlockPos pos = hit.getBlockPos();
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 1.5D,
                    pos.getZ() + 0.5D, new ItemStack(Items.FISH, 1, fish)));
        }
        worldIn.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F,
                1.0F / (worldIn.rand.nextFloat() * 0.4F + 1.2F) + 0.1F);
    }

    private RayTraceResult traceWater(World world, EntityPlayer player) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        return world.rayTraceBlocks(eyes, eyes.add(player.getLook(1.0F).scale(5.0D)), true);
    }

    public static boolean isFishingWater(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos) || !world.isBlockLoaded(pos.down(2))) {
            return false;
        }
        IBlockState surface = world.getBlockState(pos);
        return surface.getMaterial() == Material.WATER
                && surface.getBlock() instanceof BlockLiquid
                && surface.getValue(BlockLiquid.LEVEL) == 0
                && world.isAirBlock(pos.up())
                && world.getBlockState(pos.down()).getMaterial() == Material.WATER
                && world.getBlockState(pos.down(2)).getMaterial() == Material.WATER;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        // Let the player charge freely and track a moving ripple. Validate the
        // water target only on release, when the fishing attempt actually happens.
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
