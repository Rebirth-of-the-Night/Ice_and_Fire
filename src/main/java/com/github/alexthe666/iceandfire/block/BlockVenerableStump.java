package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockVenerableStump extends Block {
    public static final PropertyEnum<BlockVenerableStump.StumpPart> PART =
            PropertyEnum.create("part", BlockVenerableStump.StumpPart.class);

    public BlockVenerableStump() {
        super(Material.WOOD);
        this.setTranslationKey("iceandfire.venerable_stump");
        this.setHarvestLevel("axe", 0);
        this.setHardness(12F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setLightOpacity(0);
        this.setLightLevel(1f);
        this.translucent = true;
        this.fullBlock = false;
        this.setRegistryName(IceAndFire.MODID, "venerable_stump");
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(PART).boundingBox;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getValue(PART).boundingBox;
    }

    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PART);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return meta < StumpPart.values().length ? getDefaultState().withProperty(PART, StumpPart.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).ordinal();
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == EnumFacing.DOWN) return BlockFaceShape.SOLID;
        return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public enum StumpPart implements IStringSerializable {
        // --
        NORTH_WEST(
            new Vec3i(-1, 0, -1),
            new AxisAlignedBB(
                3.0 / 16.0,
                0.0 / 16.0,
                3.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // -0
        WEST(
            new Vec3i(-1, 0, 0),
            new AxisAlignedBB(
                3.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // -+
        SOUTH_WEST(
            new Vec3i(-1, 0, +1),
            new AxisAlignedBB(
                3.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                13.0 / 16.0
            )
        ),
        // 0-
        NORTH(
            new Vec3i(0, 0, -1),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                3.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // 00
        MIDDLE(
            new Vec3i(0, 0, 0),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // 0+
        SOUTH(
            new Vec3i(0, 0, +1),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                16.0 / 16.0,
                14.0 / 16.0,
                13.0 / 16.0
            )
        ),
        // +-
        NORTH_EAST(
            new Vec3i(+1, 0, -1),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                3.0 / 16.0,
                13.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // +0
        EAST(
            new Vec3i(+1, 0, 0),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                13.0 / 16.0,
                14.0 / 16.0,
                16.0 / 16.0
            )
        ),
        // ++
        SOUTH_EAST(
            new Vec3i(+1, 0, +1),
            new AxisAlignedBB(
                0.0 / 16.0,
                0.0 / 16.0,
                0.0 / 16.0,
                13.0 / 16.0,
                14.0 / 16.0,
                13.0 / 16.0
            )
        );

        private final Vec3i fromCenter;
        private final AxisAlignedBB boundingBox;

        StumpPart(Vec3i fromCenter, AxisAlignedBB boundingBox) {
            this.fromCenter = fromCenter;
            this.boundingBox = boundingBox;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.toString().toLowerCase();
        }

        public Vec3i getFromCenter() {
            return fromCenter;
        }

        public AxisAlignedBB getBoundingBox() {
            return boundingBox;
        }
    }
}
