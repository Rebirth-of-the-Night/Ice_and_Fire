package com.github.alexthe666.iceandfire.block.keletu;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCrackedDreadstone extends Block implements IDragonProof {
    public BlockCrackedDreadstone() {
        super(Material.ROCK);
        this.setHardness(4F);
        this.setResistance(1000F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setTranslationKey("iceandfire." + "cracked_dreadstone");
        this.setRegistryName(IceAndFire.MODID, "cracked_dreadstone");
    }

    private void deleteNearbyBlock(World worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.getDistance(startPos.getX(), startPos.getY(), startPos.getZ()) < 32) {
            if (worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.cracked_dreadstone || worldIn.getBlockState(pos).getBlock() == this) {
                worldIn.destroyBlock(pos, false);
                for (EnumFacing facing : EnumFacing.values()) {
                    deleteNearbyBlock(worldIn, pos.offset(facing), startPos);
                }
            }
        }
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if(!(entityIn instanceof EntityDreadQueen) && !(entityIn instanceof EntityBlackFrostDragon))
            return;

        deleteNearbyBlock(worldIn, pos, pos);
    }
}
