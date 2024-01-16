package com.github.alexthe666.iceandfire.block.keletu;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IBlockWithoutItem;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IDreadBlock;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDreadPistonExtension extends BlockPistonExtension implements IDreadBlock, IDragonProof, IBlockWithoutItem {

    public BlockDreadPistonExtension() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(SHORT, Boolean.valueOf(false)));
        this.setSoundType(SoundType.STONE);
        this.setHardness(-1F);

        this.setTranslationKey("iceandfire.dread_piston_extension");
        this.setRegistryName(IceAndFire.MODID, "dread_piston_extension");
        
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
            Block block = worldIn.getBlockState(blockpos).getBlock();

            if (block == IafBlockRegistry.dread_piston || block == IafBlockRegistry.dread_sticky_piston)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if ((iblockstate.getBlock() == IafBlockRegistry.dread_piston || iblockstate.getBlock() == IafBlockRegistry.dread_sticky_piston) && ((Boolean)iblockstate.getValue(BlockDreadPistonBase.EXTENDED)).booleanValue())
        {
            iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() != IafBlockRegistry.dread_piston && iblockstate.getBlock() != IafBlockRegistry.dread_sticky_piston &&
                iblockstate.getBlock() != Blocks.PISTON && iblockstate.getBlock() != Blocks.STICKY_PISTON)
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
        }
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? IafBlockRegistry.dread_sticky_piston : IafBlockRegistry.dread_piston);
    }
}