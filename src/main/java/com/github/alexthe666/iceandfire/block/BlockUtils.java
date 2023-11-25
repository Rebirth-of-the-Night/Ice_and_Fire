package com.github.alexthe666.iceandfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockUtils {

    public static boolean isDreadBlock(IBlockState state){
        Block block = state.getBlock();
        //TODO what about dreadwood planks and dread stone tiles
        return block instanceof IDreadBlock|| block == IafBlockRegistry.dread_stone_bricks_stairs;
    }

    public static boolean canSnowUpon(IBlockState state){
        return !isDreadBlock(state) && state.getBlock() != IafBlockRegistry.dragon_ice_spikes;
    }
}
