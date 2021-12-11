package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockVenerableStump extends Block {
    @SuppressWarnings("PointlessArithmeticExpression")
    static AxisAlignedBB boundingBox = new AxisAlignedBB(
            -16.0 / 16.0,
            0.0 / 16.0,
            -16.0 / 16.0,
            29.0 / 16.0,
            16.0 / 16.0,
            29.0 / 16.0
    );

    public BlockVenerableStump() {
        super(Material.WOOD);
        this.setTranslationKey("iceandfire.venerable_stump");
        this.setHarvestLevel("axe", 0);
        this.setHardness(12F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setLightOpacity(0);
        this.setLightLevel(1f);
        translucent = true;
        this.fullBlock = false;
        setRegistryName(IceAndFire.MODID, "venerable_stump");
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
        return boundingBox;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return boundingBox;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return boundingBox;
    }
}
