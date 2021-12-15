package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

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
        return fullBlock;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return fullBlock;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return fullBlock;
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
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return boundingBox;
    }

    enum StumpPart implements IStringSerializable {
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

        Vec3i fromCenter;
        AxisAlignedBB boundingBox;

        StumpPart(Vec3i fromCenter, AxisAlignedBB boundingBox) {
            this.fromCenter = fromCenter;
            this.boundingBox = boundingBox;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.toString().toLowerCase();
        }
    }
}
