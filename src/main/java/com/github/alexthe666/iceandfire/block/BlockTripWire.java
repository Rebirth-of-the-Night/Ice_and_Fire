package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
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

public class BlockTripWire extends Block implements IDragonProof, IDreadBlock {
    public BlockTripWire() {
        super(Material.ROCK);
        this.setHardness(4F);
        this.setResistance(1000F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setTranslationKey("iceandfire." + "tripwire");
        this.setRegistryName(IceAndFire.MODID, "tripwire");
    }


    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    private void deleteNearbyWire(World worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.getDistance(startPos.getX(), startPos.getY(), startPos.getZ()) < 32) {
            if (worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.tripwire || worldIn.getBlockState(pos).getBlock() == this) {
                worldIn.destroyBlock(pos, false);
                for (EnumFacing facing : EnumFacing.values()) {
                    deleteNearbyWire(worldIn, pos.offset(facing), startPos);
                }
            }
            for (int i = -64; i < 64; i++)
                for (int k = -64; k < 64; k++)
                    if (worldIn.getBlockState(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + k)).getBlock() == IafBlockRegistry.dread_single_spawner_queen) {
                        worldIn.destroyBlock(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + k), false);
                    }
        }
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if(!(entityIn instanceof EntityPlayer))
            return;
        //List<EntityPlayer> players = worldIn.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1, pos.getZ()+1).expand(6, 3, 6).expand(-6, -3, -6));

        deleteNearbyWire(worldIn, pos, pos);

        worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDERDRAGON_AMBIENT, SoundCategory.BLOCKS, 1, 1, false);
        worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 2, false);

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
