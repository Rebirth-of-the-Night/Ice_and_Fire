package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import java.util.Random;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Set;

public class WorldGenGorgonTemple extends WorldGenerator {
    private static final ResourceLocation TEMPLATE = new ResourceLocation(IceAndFire.MODID, "gorgon_temple");
    // The template has a buried chamber at 0..6, sand at 7..9, and a temple floor at 10.
    private static final int GROUND_LEVEL = 9;
    private static final int BLEND_RADIUS = 12;

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        PlacementSettings settings = new PlacementSettings()
                .setRotation(Rotation.values()[random.nextInt(Rotation.values().length)])
                .setMirror(Mirror.values()[random.nextInt(Mirror.values().length)]);
        Template template = world.getSaveHandler().getStructureTemplateManager()
                .getTemplate(world.getMinecraftServer(), TEMPLATE);
        BlockPos size = template.getSize();
        if (size.getX() <= 0 || size.getY() <= GROUND_LEVEL || size.getZ() <= 0) {
            return false;
        }

        // Center the actual transformed footprint on the candidate, for every rotation and mirror.
        BlockPos opposite = Template.transformedBlockPos(settings, size.add(-1, 0, -1));
        int minX = Math.min(0, opposite.getX());
        int minZ = Math.min(0, opposite.getZ());
        int width = Math.abs(opposite.getX()) + 1;
        int depth = Math.abs(opposite.getZ()) + 1;
        BlockPos corner = new BlockPos(position.getX() - width / 2, 0, position.getZ() - depth / 2);
        ArrayList<Integer> dryHeights = new ArrayList<>();
        ArrayList<Integer> allHeights = new ArrayList<>();
        IBlockState[][] surfaceStates = new IBlockState[width][depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos column = corner.add(x, 0, z);
                BlockPos top = ground(world, column, false);
                BlockPos ground = ground(world, column, true);
                surfaceStates[x][z] = surface(world.getBlockState(ground));
                allHeights.add(ground.getY());
                if (!world.getBlockState(top).getMaterial().isLiquid()) dryHeights.add(ground.getY());
            }
        }
        // A median follows the bulk of the land rather than raising the whole ruin to a hilltop.
        ArrayList<Integer> heights = dryHeights.isEmpty() ? allHeights : dryHeights;
        Collections.sort(heights);
        int groundY = heights.get((heights.size() - 1) / 2);
        BlockPos origin = corner.add(-minX, groundY - GROUND_LEVEL, -minZ);
        if (origin.getY() < 2 || origin.getY() + size.getY() > world.getHeight()) {
            return false;
        }
        // Terrain, trees, water and small caves are adapted below. Preserve protected blocks.
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos base = corner.add(x, origin.getY(), z);
                int top = Math.max(origin.getY() + size.getY() - 1, world.getHeight(base).getY());
                for (int y = base.getY(); y <= top; y++) {
                    BlockPos target = new BlockPos(base.getX(), y, base.getZ());
                    IBlockState state = world.getBlockState(target);
                    if (protectedBlock(world, target, state)) {
                        return false;
                    }
                }
            }
        }

        Map<BlockPos, IBlockState> apron = planApron(world, origin, settings, groundY);
        clearTrees(world, corner, width, depth, origin.getY(), size.getY(), apron);
        // Remove any hillside overhang above the template's roof, within the building footprint.
        for (int x = 5; x <= 27; x++) for (int z = 2; z <= 29; z++) {
            BlockPos column = Template.transformedBlockPos(settings, new BlockPos(x, 0, z)).add(origin);
            int top = world.getHeight(column).getY();
            for (int y = origin.getY() + size.getY(); y <= top; y++) {
                world.setBlockState(new BlockPos(column.getX(), y, column.getZ()), Blocks.AIR.getDefaultState(), 2);
            }
        }
        // Extend the buried floor down to solid terrain where it crosses a cave or a bank.
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos below = corner.add(x, origin.getY() - 1, z);
                for (int n = 0; n < 32 && below.getY() > 1; n++, below = below.down()) {
                    IBlockState state = world.getBlockState(below);
                    if (state.isFullCube() || protectedBlock(world, below, state)) break;
                    world.setBlockState(below, Blocks.STONE.getDefaultState(), 2);
                }
            }
        }
        template.addBlocksToWorld(world, origin, (worldIn, target, info) -> {
            // Exterior air must not excavate a square clearing around the ruin.
            boolean outside = info.pos.getX() < 5 || info.pos.getX() > 27
                    || info.pos.getZ() < 2 || info.pos.getZ() > 29;
            if (info.pos.getY() > GROUND_LEVEL && outside && info.blockState.getBlock() == Blocks.AIR) {
                return null;
            }
            // Keep the beach's original surface instead of stamping a 32x32 sand platform.
            if (info.blockState.getBlock() == Blocks.SAND) {
                if (outside || worldIn.getBlockState(target).isFullCube()) {
                    return null;
                }
                IBlockState surface = surfaceStates[target.getX() - corner.getX()][target.getZ() - corner.getZ()];
                return new Template.BlockInfo(info.pos, filler(surface), info.tileentityData);
            }
            return info;
        }, settings, 2);
        apron.forEach((pos, state) -> world.setBlockState(pos, state, 2));
        return true;
    }

    private static IBlockState filler(IBlockState surface) {
        return surface.getMaterial() == Material.GRASS ? Blocks.DIRT.getDefaultState() : surface;
    }

    private static IBlockState surface(IBlockState state) {
        Material material = state.getMaterial();
        return material == Material.GRASS || material == Material.GROUND || material == Material.SAND
                || material == Material.ROCK ? state : Blocks.GRASS.getDefaultState();
    }

    private static boolean protectedBlock(World world, BlockPos pos, IBlockState state) {
        return world.getTileEntity(pos) != null || state.getBlockHardness(world, pos) < 0;
    }

    private static boolean treeBlock(World world, BlockPos pos, IBlockState state) {
        return state.getBlock().isWood(world, pos) || state.getBlock().isLeaves(state, world, pos);
    }

    private static BlockPos ground(World world, BlockPos column, boolean skipLiquid) {
        BlockPos pos = world.getHeight(column).down();
        while (pos.getY() > 1) {
            IBlockState state = world.getBlockState(pos);
            if (!skipLiquid && state.getMaterial().isLiquid()) break;
            if (!treeBlock(world, pos, state) && !state.getMaterial().isReplaceable()
                    && state.getBlock() != Blocks.SNOW_LAYER
                    && !(skipLiquid && state.getMaterial().isLiquid())) break;
            pos = pos.down();
        }
        return pos;
    }

    private static void clearTrees(World world, BlockPos corner, int width, int depth, int bottom, int height,
                                   Map<BlockPos, IBlockState> apron) {
        Set<BlockPos> wood = new HashSet<>();
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        Set<BlockPos> columns = new HashSet<>();
        for (int x = 0; x < width; x++) for (int z = 0; z < depth; z++) columns.add(corner.add(x, 0, z));
        for (BlockPos pos : apron.keySet()) columns.add(new BlockPos(pos.getX(), 0, pos.getZ()));
        for (BlockPos column : columns) {
            int top = Math.min(world.getHeight() - 1, Math.max(bottom + height, world.getHeight(column).getY()));
            for (int y = bottom; y <= top; y++) {
                BlockPos pos = new BlockPos(column.getX(), y, column.getZ());
                if (world.getBlockState(pos).getBlock().isWood(world, pos) && wood.add(pos)) pending.add(pos);
            }
        }
        // Follow connected branches beyond the wall so cut trees don't leave floating crowns.
        while (!pending.isEmpty() && wood.size() < 4096) {
            BlockPos pos = pending.removeFirst();
            for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
                BlockPos next = pos.add(x, y, z);
                if (next.getX() < corner.getX() - BLEND_RADIUS - 8 || next.getX() >= corner.getX() + width + BLEND_RADIUS + 8
                        || next.getZ() < corner.getZ() - BLEND_RADIUS - 8 || next.getZ() >= corner.getZ() + depth + BLEND_RADIUS + 8
                        || next.getY() < bottom || next.getY() >= world.getHeight()) continue;
                IBlockState state = world.getBlockState(next);
                if (state.getBlock().isWood(world, next) && !protectedBlock(world, next, state)
                        && wood.add(next)) pending.add(next);
            }
        }
        Set<BlockPos> leaves = new HashSet<>();
        for (BlockPos pos : wood) {
            if (protectedBlock(world, pos, world.getBlockState(pos))) continue;
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            for (int x = -4; x <= 4; x++) for (int y = -4; y <= 4; y++) for (int z = -4; z <= 4; z++) {
                if (Math.abs(x) + Math.abs(y) + Math.abs(z) > 4) continue;
                BlockPos leaf = pos.add(x, y, z);
                IBlockState state = world.getBlockState(leaf);
                if (state.getBlock().isLeaves(state, world, leaf)) leaves.add(leaf);
            }
        }
        for (BlockPos leaf : leaves) {
            boolean supported = false;
            for (int x = -4; x <= 4 && !supported; x++) for (int y = -4; y <= 4 && !supported; y++) for (int z = -4; z <= 4; z++) {
                if (Math.abs(x) + Math.abs(y) + Math.abs(z) > 4) continue;
                BlockPos support = leaf.add(x, y, z);
                if (world.getBlockState(support).getBlock().isWood(world, support)) { supported = true; break; }
            }
            if (!supported && !protectedBlock(world, leaf, world.getBlockState(leaf))) {
                world.setBlockState(leaf, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }

    private static Map<BlockPos, IBlockState> planApron(World world, BlockPos origin,
                                                      PlacementSettings settings, int groundY) {
        Map<BlockPos, IBlockState> result = new LinkedHashMap<>();
        // The stone floor occupies local x=5..27, z=2..29. Feather soil beyond it,
        // following the existing surface, with broad variations instead of a rectangular rim.
        for (int x = 5 - BLEND_RADIUS; x <= 27 + BLEND_RADIUS; x++) {
            for (int z = 2 - BLEND_RADIUS; z <= 29 + BLEND_RADIUS; z++) {
                int dx = Math.max(5 - x, Math.max(0, x - 27));
                int dz = Math.max(2 - z, Math.max(0, z - 29));
                if (dx == 0 && dz == 0) continue;
                double distance = Math.sqrt(dx * dx + dz * dz);
                BlockPos column = Template.transformedBlockPos(settings, new BlockPos(x, 0, z)).add(origin);
                double variation = Math.sin(column.getX() * 0.37 + column.getZ() * 0.19)
                        + 0.5 * Math.sin(column.getZ() * 0.51 - column.getX() * 0.13);
                BlockPos ground = ground(world, column, true);
                IBlockState surface = surface(world.getBlockState(ground));
                int relief = Math.abs(groundY - ground.getY());
                if (distance > Math.min(BLEND_RADIUS, 4.5 + relief + variation)) continue;
                int falloff = (int) Math.floor(Math.max(0, distance - 1) * 0.7 + 0.25 * (variation + 1.5));
                boolean cut = ground.getY() > groundY;
                int top = cut ? Math.min(ground.getY(), groundY + falloff) : Math.max(ground.getY(), groundY - falloff);
                if (top == ground.getY()) continue;
                boolean clear = true;
                for (int y = Math.min(ground.getY(), top); y <= Math.max(ground.getY(), top); y++) {
                    BlockPos target = new BlockPos(column.getX(), y, column.getZ());
                    IBlockState state = world.getBlockState(target);
                    if (protectedBlock(world, target, state)) {
                        clear = false;
                        break;
                    }
                }
                if (!clear) continue;
                if (cut) {
                    for (int y = top + 1; y <= ground.getY(); y++) {
                        result.put(new BlockPos(column.getX(), y, column.getZ()), Blocks.AIR.getDefaultState());
                    }
                    result.put(new BlockPos(column.getX(), top, column.getZ()), surface);
                } else {
                    for (int y = ground.getY() + 1; y <= top; y++) {
                        result.put(new BlockPos(column.getX(), y, column.getZ()), y == top ? surface : filler(surface));
                    }
                }
            }
        }
        // The basement fills the full template rectangle, beyond the smaller above-ground floor.
        // Grading the surrounding land must never fill its chamber or replace its outer walls.
        BlockPos opposite = Template.transformedBlockPos(settings, new BlockPos(31, 0, 31)).add(origin);
        result.keySet().removeIf(pos -> pos.getY() <= origin.getY() + 6
                && pos.getX() >= Math.min(origin.getX(), opposite.getX())
                && pos.getX() <= Math.max(origin.getX(), opposite.getX())
                && pos.getZ() >= Math.min(origin.getZ(), opposite.getZ())
                && pos.getZ() <= Math.max(origin.getZ(), opposite.getZ()));
        return result;
    }
}
