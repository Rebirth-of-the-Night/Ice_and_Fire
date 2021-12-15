package com.github.alexthe666.iceandfire.item.block;

import com.github.alexthe666.iceandfire.block.BlockVenerableStump;
import com.github.alexthe666.iceandfire.block.BlockVenerableStump.StumpPart;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemBlockVenerableStump extends ItemBlock {
    public ItemBlockVenerableStump(Block block) {
        super(block);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(facing == EnumFacing.UP || facing == EnumFacing.DOWN)) {
            return EnumActionResult.FAIL;
        }
        IBlockState originState = world.getBlockState(pos);
        if (!originState.getBlock().isReplaceable(world, pos)) {
            pos = pos.offset(facing);
        }

        ItemStack heldStack = player.getHeldItem(hand);

        for (StumpPart part : StumpPart.values()) {
            BlockPos partPos = pos.add(part.getFromCenter());
            if (!(player.canPlayerEdit(partPos, facing, heldStack) && this.block.canPlaceBlockAt(world, partPos)))
                return EnumActionResult.FAIL;
        }

        placeStump(world, pos, this.block);
        SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
        world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        heldStack.shrink(1);

        return EnumActionResult.SUCCESS;
    }

    public static void placeStump(World world, BlockPos pos, Block block) {
        for (StumpPart part : StumpPart.values()) {
            BlockPos partPos = pos.add(part.getFromCenter());
            IBlockState blockState = block.getDefaultState().withProperty(BlockVenerableStump.PART, part);
            world.setBlockState(partPos, blockState, 2);
            world.notifyNeighborsOfStateChange(partPos, block, true); // TODO maybe false like doors?
        }
    }
}
