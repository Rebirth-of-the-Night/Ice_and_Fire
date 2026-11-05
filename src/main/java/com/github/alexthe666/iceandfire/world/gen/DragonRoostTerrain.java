package com.github.alexthe666.iceandfire.world.gen;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

/** Terrain checks shared by all surface dragon roosts. */
final class DragonRoostTerrain {
    private DragonRoostTerrain() { }

    private static boolean isGround(IBlockState state) {
        Material material = state.getMaterial();
        return material == Material.GRASS || material == Material.GROUND
            || material == Material.ROCK || material == Material.SAND
            || material == Material.CLAY || material == Material.ICE
            || material == Material.PACKED_ICE || material == Material.CRAFTED_SNOW;
    }

    /** First space above terrain, ignoring trees and decorations. */
    static BlockPos surface(World world, BlockPos column) {
        BlockPos pos = world.getHeight(column).down();
        while (pos.getY() > 1) {
            IBlockState state = world.getBlockState(pos);
            if (isGround(state) || state.getMaterial().isLiquid()) break;
            pos = pos.down();
        }
        return pos.up();
    }

    private static int clearingRadius(int radius) {
        int outer = radius + 13;
        return (int) Math.ceil((outer * 2 + outer / 5) * 0.333F + 0.5F);
    }

    private static int floorRadius(int radius) {
        return (int) Math.ceil((radius * 2 + radius / 5) * 0.333F + 0.5F);
    }

    static final class Site {
        final BlockPos position;
        final int radius;

        Site(BlockPos position, int radius) {
            this.position = position;
            this.radius = radius;
        }
    }

    /** A successful spawn roll gets several placement options, without extra spawn rolls. */
    static Site findSite(World world, BlockPos origin, int radius, Predicate<BlockPos> allowed) {
        // Prefer the rolled size; use a smaller normal roost only if necessary.
        for (int size = radius; size >= 12; size--) {
            for (int ring = 0; ring <= 2; ring++) {
                for (int x = -ring; x <= ring; x++) {
                    for (int z = -ring; z <= ring; z++) {
                        if (Math.max(Math.abs(x), Math.abs(z)) != ring) continue;
                        BlockPos candidate = surface(world, origin.add(x * 8, 0, z * 8)).down();
                        if (allowed.test(candidate) && canGenerate(world, candidate, size)) {
                            return new Site(candidate, size);
                        }
                    }
                }
            }
        }
        return null;
    }

    /** Only the solid floor needs dry, reasonably close terrain. */
    static boolean canGenerate(World world, BlockPos center, int radius) {
        int floor = floorRadius(radius);
        for (int x = -floor; x <= floor; x++) {
            for (int z = -floor; z <= floor; z++) {
                if (x * x + z * z > floor * floor) continue;
                BlockPos ground = surface(world, center.add(x, 0, z)).down();
                if (!isGround(world.getBlockState(ground))
                    || Math.abs(ground.getY() - center.getY()) > 8) return false;
            }
        }
        return true;
    }

    /** Join the floor to the landscape instead of demanding an already flat site. */
    static void levelFloor(World world, BlockPos center, int radius) {
        int floor = floorRadius(radius);
        for (int x = -floor; x <= floor; x++) {
            for (int z = -floor; z <= floor; z++) {
                if (x * x + z * z > floor * floor) continue;
                BlockPos ground = surface(world, center.add(x, 0, z)).down();
                IBlockState fill = world.getBlockState(ground);
                boolean grass = fill.getMaterial() == Material.GRASS;
                IBlockState subsurface = grass ? Blocks.DIRT.getDefaultState() : fill;
                if (grass && ground.getY() < center.getY()) {
                    world.setBlockState(ground, subsurface, 2);
                }
                for (int y = ground.getY() + 1; y <= center.getY(); y++) {
                    IBlockState layer = y == center.getY() ? fill : subsurface;
                    world.setBlockState(new BlockPos(ground.getX(), y, ground.getZ()), layer, 2);
                }
                for (int y = center.getY() + 1; y <= ground.getY(); y++) {
                    world.setBlockToAir(new BlockPos(ground.getX(), y, ground.getZ()));
                }
            }
        }
    }

    static void clearTrees(World world, BlockPos center, int radius) {
        int outer = clearingRadius(radius);
        for (int x = -outer; x <= outer; x++) {
            for (int z = -outer; z <= outer; z++) {
                if (x * x + z * z > outer * outer) continue;
                BlockPos column = center.add(x, 0, z);
                int top = world.getHeight(column).getY();
                int bottom = surface(world, column).getY();
                for (int y = bottom; y < top; y++) {
                    BlockPos pos = new BlockPos(column.getX(), y, column.getZ());
                    Material material = world.getBlockState(pos).getMaterial();
                    if (world.getTileEntity(pos) == null && (material == Material.WOOD
                        || material == Material.LEAVES || material == Material.PLANTS
                        || material == Material.VINE)) world.setBlockToAir(pos);
                }
            }
        }
    }
}
