package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("SuspiciousNameCombination")
public class BlockIceSpikes extends BlockDirectional {
    protected static final double heightUpMin = 0.0;
    protected static final double heightUpMax = 0.6875;
    protected static final double heightDownMin = 0.3125;
    protected static final double heightDownMax = 1.0;
    protected static final double squareMin = 0.0625;
    protected static final double squareMax = 0.9375;
    protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(heightUpMin, squareMin, squareMin, heightUpMax, squareMax, squareMax);
    protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(heightDownMin, squareMin, squareMin, heightDownMax, squareMax, squareMax);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(squareMin, squareMin, heightUpMin, squareMax, squareMax, heightUpMax);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(squareMin, squareMin, heightDownMin, squareMax, squareMax, heightDownMax);
    protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(squareMin, heightUpMin, squareMin, squareMax, heightUpMax, squareMax);
    protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(squareMin, heightDownMin, squareMin, squareMax, heightDownMax, squareMax);
    public Item itemBlock;

    public BlockIceSpikes() {
        super(Material.PACKED_ICE);
        this.setHardness(2.5F);
        this.setTranslationKey("iceandfire.dragon_ice_spikes");
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setSoundType(SoundType.GLASS);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setRegistryName(IceAndFire.MODID, "dragon_ice_spikes");
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!(entityIn instanceof EntityIceDragon)) {
            entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
            if (entityIn instanceof EntityLivingBase && entityIn.motionX != 0 && entityIn.motionZ != 0) {
                ((EntityLivingBase) entityIn).knockBack(entityIn, 0.5F, entityIn.motionX, entityIn.motionZ);
            }
        }
    }

    @Override
    @Nonnull
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @Nonnull
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        // add one so that 0 equals up which was the default last version
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta + 1));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        // add 5 for the modular subtraction of one (-1 % 6 seems to be negative, and we can't have that)
        return (state.getValue(FACING).getIndex() + 5) % 6;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing enumfacing = state.getValue(FACING);

        switch (enumfacing) {
            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case SOUTH:
                return AABB_SOUTH;
            case NORTH:
            default:
                return AABB_NORTH;
            case UP:
                return AABB_UP;
            case DOWN:
                return AABB_DOWN;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canPlaceBlock(worldIn, pos, side);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (canPlaceBlock(worldIn, pos, enumfacing)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(worldIn, blockpos, direction) == BlockFaceShape.SOLID;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlock(worldIn, pos, state.getValue(FACING))) {
            worldIn.setBlockToAir(pos);
        }
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
