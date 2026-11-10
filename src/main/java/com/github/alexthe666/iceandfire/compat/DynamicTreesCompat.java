package com.github.alexthe666.iceandfire.compat;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockFruit;
import com.ferreusveritas.dynamictrees.blocks.BlockFruitCocoa;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockElementalBranch;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.message.MessageTreeLeafDissolve;
import com.github.alexthe666.iceandfire.util.LeafBurnEffects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Dynamic Trees support. Only load this class when the dynamictrees mod is present.
 */
public class DynamicTreesCompat {

    private static final int MAX_TREE_BLOCKS = 2048;
    private static final int MAX_LEAF_DISTANCE = 4;
    private static final int MAX_LEAF_BLOCKS = 8192;

    /**
     * If the block at pos is a Dynamic Trees branch, turns the whole connected tree into a charred (or frozen) copy of
     * itself: every branch keeps its radius so the shape stays intact, and the leaves are removed.
     *
     * @param block the charred, frozen or crackled branch block to turn the tree into
     * @return true if the position was a tree branch and has been handled
     */
    /**
     * Fells a Dynamic Trees tree the way an axe or saw would, so a dragon that runs into it topples it instead of
     * being stopped by its tough branches.
     *
     * @return true if the block was a branch and the tree is now falling
     */
    public static boolean fellTree(World world, BlockPos pos, IBlockState state, EntityLivingBase feller) {
        if (!(state.getBlock() instanceof BlockBranch)) {
            return false;
        }
        ((BlockBranch) state.getBlock()).futureBreak(state, world, pos, feller);
        return true;
    }

    public static boolean transformTree(World world, BlockPos start, BlockElementalBranch block) {
        if (!TreeHelper.isBranch(world.getBlockState(start))) {
            return false;
        }
        Map<BlockPos, Integer> branches = new HashMap<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        branches.put(start, radius(world.getBlockState(start)));
        queue.add(start);
        while (!queue.isEmpty() && branches.size() < MAX_TREE_BLOCKS) {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos next = current.add(dx, dy, dz);
                        if (!branches.containsKey(next)) {
                            IBlockState nextState = world.getBlockState(next);
                            if (TreeHelper.isBranch(nextState)) {
                                branches.put(next, radius(nextState));
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        }

        // Leaves are always within a few blocks of a branch
        Set<BlockPos> leaves = new HashSet<>();
        ArrayDeque<BlockPos> leafQueue = new ArrayDeque<>();
        Map<BlockPos, Integer> leafDistance = new HashMap<>();
        for (BlockPos branch : branches.keySet()) {
            leafDistance.put(branch, 0);
            leafQueue.add(branch);
        }
        while (!leafQueue.isEmpty() && leaves.size() < MAX_LEAF_BLOCKS) {
            BlockPos current = leafQueue.poll();
            int distance = leafDistance.get(current);
            if (distance >= MAX_LEAF_DISTANCE) {
                continue;
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos next = current.offset(facing);
                if (!leafDistance.containsKey(next) && TreeHelper.isLeaves(world.getBlockState(next))) {
                    leafDistance.put(next, distance + 1);
                    leaves.add(next);
                    leafQueue.add(next);
                }
            }
        }

        // Fruit (apples, cocoa, ...) hangs off branches and leaves; snow layers rest on top of them
        Set<BlockPos> fruits = new HashSet<>();
        for (BlockPos pos : branches.keySet()) {
            collectFruit(world, pos, fruits);
        }
        for (BlockPos pos : leaves) {
            collectFruit(world, pos, fruits);
        }

        int effect = block == IafBlockRegistry.charredBranch ? MessageTreeLeafDissolve.ELEMENT_FIRE
                : block == IafBlockRegistry.frozenBranch ? MessageTreeLeafDissolve.ELEMENT_ICE
                : block == IafBlockRegistry.crackledBranch ? MessageTreeLeafDissolve.ELEMENT_LIGHTNING : -1;
        if (effect >= 0) {
            for (BlockPos leaf : leaves) {
                LeafBurnEffects.queue(world, leaf, visualLeafState(world.getBlockState(leaf)), leafDistance.get(leaf), effect);
            }
        }
        // flag 2: no neighbour updates, so Dynamic Trees doesn't react to half-converted trees
        for (BlockPos fruit : fruits) {
            world.setBlockState(fruit, Blocks.AIR.getDefaultState(), 2);
        }
        for (BlockPos leaf : leaves) {
            world.setBlockState(leaf, Blocks.AIR.getDefaultState(), 2);
        }
        for (Map.Entry<BlockPos, Integer> entry : branches.entrySet()) {
            world.setBlockState(entry.getKey(), block.withRadius(entry.getValue()), 2);
        }
        return true;
    }

    /**
     * Dynamic Trees leaves are drawn as their "primitive" leaf block (e.g. vanilla oak or spruce leaves), so the
     * effect uses that block's texture and colour, which always resolve on the client.
     */
    public static IBlockState visualLeafState(IBlockState state) {
        if (TreeHelper.isLeaves(state)) {
            IBlockState primitive = TreeHelper.getLeaves(state).getProperties(state).getPrimitiveLeaves();
            if (primitive != null && primitive.getBlock() != Blocks.AIR) {
                return primitive;
            }
        }
        return state;
    }

    private static void collectFruit(World world, BlockPos pos, Set<BlockPos> fruits) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos next = pos.offset(facing);
            Block block = world.getBlockState(next).getBlock();
            if (block instanceof BlockFruit || block instanceof BlockFruitCocoa || (facing == EnumFacing.UP && block == Blocks.SNOW_LAYER)) {
                fruits.add(next);
            }
        }
    }

    private static int radius(IBlockState state) {
        return TreeHelper.getBranch(state).getRadius(state);
    }
}
