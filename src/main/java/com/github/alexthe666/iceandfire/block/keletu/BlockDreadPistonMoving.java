package com.github.alexthe666.iceandfire.block.keletu;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IBlockWithoutItem;
import com.github.alexthe666.iceandfire.entity.tile.keletu.TileEntityDreadPiston;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDreadPistonMoving extends BlockPistonMoving implements IBlockWithoutItem {

    public static final PropertyDirection FACING = BlockDreadPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockDreadPistonExtension.TYPE;

    public BlockDreadPistonMoving()
    {

        this.setTranslationKey("iceandfire.piston_dread_moving");
        this.setRegistryName(IceAndFire.MODID, "piston_dread_moving");
        
    }

    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return null;
    }

    public static TileEntity createTilePiston(IBlockState blockStateIn, EnumFacing facingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn)
    {
        return new TileEntityDreadPiston(blockStateIn, facingIn, extendingIn, shouldHeadBeRenderedIn);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityDreadPiston)
        {
            ((TileEntityDreadPiston)tileentity).clearPistonTileEntity();
        }
        else
        {
            super.breakBlock(worldIn, pos, state);
        }
    }

    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() instanceof BlockDreadPistonBase && ((Boolean)iblockstate.getValue(BlockDreadPistonBase.EXTENDED)).booleanValue())
        {
            worldIn.setBlockToAir(blockpos);
        } else {
            super.onPlayerDestroy(worldIn, blockpos, iblockstate);
        }
    }
}