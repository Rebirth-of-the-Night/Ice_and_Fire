package com.github.alexthe666.iceandfire.entity.tile.keletu;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.block.keletu.BlockDreadPistonBase;
import com.github.alexthe666.iceandfire.block.keletu.BlockDreadPistonExtension;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityDreadPiston extends TileEntityPiston {
    public TileEntityDreadPiston(IBlockState pistonStateIn, EnumFacing pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn)
    {
        super(pistonStateIn, pistonFacingIn, extendingIn, shouldHeadBeRenderedIn);
    }

    //necessary
    @Override
    protected IBlockState getCollisionRelatedBlockState()
    {
        return (!this.isExtending() && this.shouldPistonHeadBeRendered()) ? IafBlockRegistry.dread_piston_head.getDefaultState().withProperty(BlockDreadPistonExtension.TYPE, this.pistonState.getBlock() == IafBlockRegistry.dread_sticky_piston ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(BlockDreadPistonExtension.FACING, this.pistonState.getValue(BlockDreadPistonBase.FACING)) : this.pistonState;
    }

    @Override
    protected void moveCollidedEntities(float p_184322_1_)
    {
        EnumFacing enumfacing = this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
        double d0 = p_184322_1_ - this.progress;
        List<AxisAlignedBB> list = Lists.newArrayList();
        this.getCollisionRelatedBlockState().addCollisionBoxToList(this.world, BlockPos.ORIGIN, new AxisAlignedBB(BlockPos.ORIGIN), list, null, true);

        if (!list.isEmpty())
        {
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
            List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity(null, this.getMovementArea(axisalignedbb, enumfacing, d0).union(axisalignedbb));

            if (!list1.isEmpty())
            {
                Block pistonBlock = this.pistonState.getBlock();
                boolean doSlimeBounce = pistonBlock.isStickyBlock(this.pistonState);

                for (int i = 0; i < list1.size(); ++i)
                {
                    Entity entity = list1.get(i);

                    if (entity.getPushReaction() != EnumPushReaction.IGNORE)
                    {
                        if (doSlimeBounce)
                        {
                            switch (enumfacing.getAxis())
                            {
                                case X:
                                    entity.motionX = enumfacing.getXOffset();
                                    break;
                                case Y:
                                    entity.motionY = enumfacing.getYOffset();
                                    break;
                                case Z:
                                    entity.motionZ = enumfacing.getZOffset();
                            }
                        }

                        double d1 = 0.0D;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            AxisAlignedBB axisalignedbb1 = this.getMovementArea(this.moveByPositionAndProgress(list.get(j)), enumfacing, d0);
                            AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();

                            if (axisalignedbb1.intersects(axisalignedbb2))
                            {
                                d1 = Math.max(d1, this.getMovement(axisalignedbb1, enumfacing, axisalignedbb2));

                                if (d1 >= d0)
                                {
                                    break;
                                }
                            }
                        }

                        if (d1 > 0.0D)
                        {
                            d1 = Math.min(d1, d0) + 0.01D;
                            pushEntity(enumfacing, entity, d1, enumfacing);

                            if (!this.extending && this.shouldHeadBeRendered)
                            {
                                this.fixEntityWithinPistonBase(entity, enumfacing, d0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void fixEntityWithinPistonBase(Entity p_190605_1_, EnumFacing p_190605_2_, double p_190605_3_)
    {
        AxisAlignedBB axisalignedbb = p_190605_1_.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = Block.FULL_BLOCK_AABB.offset(this.pos);

        if (axisalignedbb.intersects(axisalignedbb1))
        {
            EnumFacing enumfacing = p_190605_2_.getOpposite();
            double d0 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb) + 0.01D;
            double d1 = this.getMovement(axisalignedbb1, enumfacing, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;

            if (Math.abs(d0 - d1) < 0.01D)
            {
                d0 = Math.min(d0, p_190605_3_) + 0.01D;
                pushEntity(p_190605_2_, p_190605_1_, d0, enumfacing);
            }
        }
    }

    @Override
    public void clearPistonTileEntity()
    {
        if (this.lastProgress < 1.0F && this.world != null)
        {
            this.progress = 1.0F;
            this.lastProgress = this.progress;
            this.world.removeTileEntity(this.pos);
            this.invalidate();

            if (this.world.getBlockState(this.pos).getBlock() == IafBlockRegistry.dread_piston_moving)
            {
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
            }
        }
    }

    @Override
    public void update()
    {
        this.lastProgress = this.progress;

        if (this.lastProgress >= 1.0F)
        {
            this.world.removeTileEntity(this.pos);
            this.invalidate();

            if (this.world.getBlockState(this.pos).getBlock() == IafBlockRegistry.dread_piston_moving)
            {
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
            }
        }
        else
        {
            float f = this.progress + 0.5F;
            this.moveCollidedEntities(f);
            this.progress = f;

            if (this.progress >= 1.0F)
            {
                this.progress = 1.0F;
            }
        }
    }

    @Override
    public void addCollissionAABBs(World p_190609_1_, BlockPos p_190609_2_, AxisAlignedBB p_190609_3_, List<AxisAlignedBB> p_190609_4_, @Nullable Entity p_190609_5_)
    {
        if (!this.extending && this.shouldHeadBeRendered)
        {
            this.pistonState.withProperty(BlockDreadPistonBase.EXTENDED, Boolean.TRUE).addCollisionBoxToList(p_190609_1_, p_190609_2_, p_190609_3_, p_190609_4_, p_190609_5_, false);
        }

        EnumFacing enumfacing = MOVING_ENTITY.get();

        if ((double)this.progress >= 1.0D || enumfacing != (this.extending ? this.pistonFacing : this.pistonFacing.getOpposite()))
        {
            int i = p_190609_4_.size();
            IBlockState iblockstate;

            if (this.shouldPistonHeadBeRendered())
            {
                iblockstate = IafBlockRegistry.dread_piston_head.getDefaultState().withProperty(BlockDreadPistonExtension.FACING, this.pistonFacing).withProperty(BlockDreadPistonExtension.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
            }
            else
            {
                iblockstate = this.pistonState;
            }

            float f = this.getExtendedProgress(this.progress);
            double d0 = (float)this.pistonFacing.getXOffset() * f;
            double d1 = (float)this.pistonFacing.getYOffset() * f;
            double d2 = (float)this.pistonFacing.getZOffset() * f;
            iblockstate.addCollisionBoxToList(p_190609_1_, p_190609_2_, p_190609_3_.offset(-d0, -d1, -d2), p_190609_4_, p_190609_5_, true);

            for (int j = i; j < p_190609_4_.size(); ++j)
            {
                p_190609_4_.set(j, p_190609_4_.get(j).offset(d0, d1, d2));
            }
        }
    }

    protected static void pushEntity(EnumFacing direction1, Entity entity, double distance, EnumFacing direction2) {
        MOVING_ENTITY.set(direction1);
        entity.move(MoverType.PISTON, distance * (double)direction2.getXOffset(), distance * (double)direction2.getYOffset(), distance * (double)direction2.getZOffset());
        MOVING_ENTITY.set(null);
    }
}