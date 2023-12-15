package com.github.alexthe666.iceandfire.block.keletu;

import java.util.List;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IBlockWithoutItem;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IDreadBlock;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.keletu.TileEntityDreadPiston;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockDreadPistonBase extends BlockPistonBase implements IDreadBlock, IDragonProof, IBlockWithoutItem {

    protected final boolean isSticky;

    public BlockDreadPistonBase(boolean isSticky) {
        super(isSticky);
        this.isSticky = isSticky;

        this.setSoundType(SoundType.STONE);
        this.setHardness(-1F);
        this.setTranslationKey(isSticky ? "iceandfire.dread_piston_sticky" : "iceandfire.dread_piston");
        this.setRegistryName(IceAndFire.MODID, isSticky ? "dread_piston_sticky" : "dread_piston");

    }

    //necessary
    @Override
    public void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !(Boolean) state.getValue(EXTENDED))
        {
            //this line
            if ((new BlockDreadPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove())
            {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        }
        else if (!flag && state.getValue(EXTENDED))
        {
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    //necessary, also yucky
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (!worldIn.isRemote)
        {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && id == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.TRUE), 2);
                return false;
            }

            if (!flag && id == 0)
            {
                return false;
            }
        }

        if (id == 0)
        {
            if (!this.doMove(worldIn, pos, enumfacing, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.TRUE), 3);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (id == 1)
        {
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityDreadPiston)
            {
                ((TileEntityDreadPiston)tileentity1).clearPistonTileEntity();
            }

            //this line
            worldIn.setBlockState(pos, IafBlockRegistry.dread_piston_moving.getDefaultState().withProperty(BlockDreadPistonMoving.FACING, enumfacing).withProperty(BlockDreadPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockDreadPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getXOffset() * 2, enumfacing.getYOffset() * 2, enumfacing.getZOffset() * 2);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                boolean flag1 = false;

                //this line
                if (block == IafBlockRegistry.dread_piston_moving)
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityDreadPiston)
                    {
                        TileEntityDreadPiston tileentitypiston = (TileEntityDreadPiston)tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos) && canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getPushReaction() == EnumPushReaction.NORMAL || block == IafBlockRegistry.dread_piston || block == IafBlockRegistry.dread_sticky_piston))
                {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    //necessary, static
    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing pushDir, boolean destroyBlocks, EnumFacing blockFace)
    {
        Block block = blockStateIn.getBlock();

        if ((block == IafBlockRegistry.dread_sticky_piston || block == IafBlockRegistry.dread_piston) && block.getMetaFromState(blockStateIn) > 5)
        {
            return false;
        }
        else if (block instanceof IDreadBlock)
        {
            return true;
        }
        else if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (pushDir != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= worldIn.getHeight() - 1 && (pushDir != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
            {
                if (block == IafBlockRegistry.dread_piston || block == IafBlockRegistry.dread_sticky_piston)
                {
                    if (blockStateIn.getValue(EXTENDED))
                    {
                        return false;
                    }
                }
                else if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON)
                {
                    if (blockStateIn.getValue(BlockPistonBase.EXTENDED))
                    {
                        return false;
                    }
                }
                else {
                    if (blockStateIn.getBlockHardness(worldIn, pos) == -1.0F)
                    {
                        return false;
                    }

                    switch (blockStateIn.getPushReaction())
                    {
                        case BLOCK:
                            return false;
                        case DESTROY:
                            return destroyBlocks;
                        case PUSH_ONLY:
                            return pushDir == blockFace;
                        default:
                            break;
                    }
                }
                return !block.hasTileEntity(blockStateIn);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //necessary
    @Override
    protected boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockDreadPistonStructureHelper blockpistonstructurehelper = new BlockDreadPistonStructureHelper(worldIn, pos, direction, extending);

        if (!blockpistonstructurehelper.canMove())
        {
            return false;
        }
        else
        {
            List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
            List<IBlockState> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i)
            {
                BlockPos blockpos = list.get(i);
                list1.add(worldIn.getBlockState(blockpos).getActualState(worldIn, blockpos));
            }

            List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
            int k = list.size() + list2.size();
            IBlockState[] aiblockstate = new IBlockState[k];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = list2.size() - 1; j >= 0; --j)
            {
                BlockPos blockpos1 = list2.get(j);
                IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
                iblockstate.getBlock().dropBlockAsItemWithChance(worldIn, blockpos1, iblockstate, chance, 0);
                worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 4);
                --k;
                aiblockstate[k] = iblockstate;
            }

            for (int l = list.size() - 1; l >= 0; --l)
            {
                BlockPos blockpos3 = list.get(l);
                IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
                worldIn.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 2);
                blockpos3 = blockpos3.offset(enumfacing);
                worldIn.setBlockState(blockpos3, IafBlockRegistry.dread_piston_moving.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(blockpos3, BlockDreadPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
                --k;
                aiblockstate[k] = iblockstate2;
            }

            BlockPos blockpos2 = pos.offset(direction);

            if (extending)
            {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate3 = IafBlockRegistry.dread_piston_head.getDefaultState().withProperty(BlockDreadPistonExtension.FACING, direction).withProperty(BlockDreadPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate1 = IafBlockRegistry.dread_piston_moving.getDefaultState().withProperty(BlockDreadPistonMoving.FACING, direction).withProperty(BlockDreadPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(blockpos2, iblockstate1, 4);
                worldIn.setTileEntity(blockpos2, BlockDreadPistonMoving.createTilePiston(iblockstate3, direction, true, true));
            }

            for (int i1 = list2.size() - 1; i1 >= 0; --i1)
            {
                worldIn.notifyNeighborsOfStateChange(list2.get(i1), aiblockstate[k++].getBlock(), false);
            }

            for (int j1 = list.size() - 1; j1 >= 0; --j1)
            {
                worldIn.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock(), false);
            }

            if (extending)
            {
                worldIn.notifyNeighborsOfStateChange(blockpos2, IafBlockRegistry.dread_piston_head, false);
            }

            return true;
        }
    }

}