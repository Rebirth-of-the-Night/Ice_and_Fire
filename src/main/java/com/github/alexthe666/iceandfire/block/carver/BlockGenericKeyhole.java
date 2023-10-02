package com.github.alexthe666.iceandfire.block.carver;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IDreadBlock;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class BlockGenericKeyhole extends Block implements IDragonProof, IDreadBlock {
    public static final PropertyBool PLAYER_PLACED = PropertyBool.create("player_placed");

    public BlockGenericKeyhole(String name) {
        super(Material.ROCK);
        this.setHardness(4F);
        this.setResistance(1000F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setTranslationKey("iceandfire." + name);
        this.setRegistryName(IceAndFire.MODID, name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PLAYER_PLACED, Boolean.FALSE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return blockState.getValue(PLAYER_PLACED) ? super.getBlockHardness(blockState, worldIn, pos) : -1;
    }

    /**
     * @return whether the stack passed is a valid key
     */
    public abstract boolean isValidKey(ItemStack stack);

    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (isValidKey(stack)) {
            if (!playerIn.isCreative())
                stack.shrink(1);

            deleteNearbyWood(worldIn, pos, pos);

            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 1, 1, false);
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1, 2, false);
        }
        return false;
    }

    private void deleteNearbyWood(World worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.getDistance(startPos.getX(), startPos.getY(), startPos.getZ()) < 32) {
            if (worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.dreadwood_planks || worldIn.getBlockState(pos).getBlock() == this) {
                worldIn.destroyBlock(pos, false);
                for (EnumFacing facing : EnumFacing.values()) {
                    deleteNearbyWood(worldIn, pos.offset(facing), startPos);
                }
            }
        }
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PLAYER_PLACED);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PLAYER_PLACED, meta > 0);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(PLAYER_PLACED) ? 1 : 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(PLAYER_PLACED, true));
    }
}
