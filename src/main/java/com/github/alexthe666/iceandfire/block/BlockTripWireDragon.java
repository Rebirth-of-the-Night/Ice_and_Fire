package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockTripWireDragon extends Block implements IDragonProof, IDreadBlock {
    public BlockTripWireDragon() {
        super(Material.ROCK);
        this.setHardness(4F);
        this.setResistance(1000F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setTranslationKey("iceandfire." + "tripwire_dragon");
        this.setRegistryName(IceAndFire.MODID, "tripwire_dragon");
    }

    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    private void deleteNearbyWire(World worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.getDistance(startPos.getX(), startPos.getY(), startPos.getZ()) < 32) {
            if (worldIn.getBlockState(pos).getBlock() == this) {
                worldIn.destroyBlock(pos, false);
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    deleteNearbyWire(worldIn, pos.offset(facing), startPos);
                }
            }
            for(int i=0;i<10;i++)
                if (worldIn.getBlockState(pos.add(0, i, 0)).getBlock() instanceof IDreadBlock) {
                    worldIn.destroyBlock(pos.add(0, i, 0), false);
                }
        }
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if(!(entityIn instanceof EntityDreadQueen) && !(entityIn instanceof EntityBlackFrostDragon) && !(entityIn instanceof EntityPlayer))
            return;

        deleteNearbyWire(worldIn, pos, pos);

        worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1, 2, false);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
