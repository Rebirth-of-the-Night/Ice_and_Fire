package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.event.WorldGenEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
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
    private static final int MAX_TERRAIN_ADJUSTMENT = 6;
    // The widest vanilla acacia leaves sit five steps from their trunk.
    private static final int LEAF_SUPPORT_DISTANCE = 5;
    private static final BlockPos[] CHAMBER_STATUES = {
            new BlockPos(7, 1, 12), new BlockPos(25, 1, 12), new BlockPos(16, 1, 23)
    };

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
        // A steep site needs more excavation than the apron can blend into the hillside.
        for (int height : allHeights) {
            if (Math.abs(height - groundY) > MAX_TERRAIN_ADJUSTMENT) return false;
        }
        BlockPos origin = corner.add(-minX, groundY - GROUND_LEVEL, -minZ);
        if (origin.getY() < 2 || origin.getY() + size.getY() > world.getHeight()) {
            return false;
        }
        // Terrain, trees, water and small caves are adapted below. Preserve protected blocks.
        Set<BlockPos> leavesBeforePlacement = new HashSet<>();
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
                    if (state.getBlock().isLeaves(state, world, target)) leavesBeforePlacement.add(target);
                }
            }
        }

        Map<BlockPos, IBlockState> apron = planApron(world, origin, settings, groundY);
        int padding = BLEND_RADIUS + 8;
        StructureBoundingBox treeBounds = new StructureBoundingBox(
                corner.getX() - padding, 0, corner.getZ() - padding,
                corner.getX() + width + padding - 1, world.getHeight() - 1, corner.getZ() + depth + padding - 1);
        Set<BlockPos> leafCandidates = clearTrees(world, corner, width, depth, origin.getY(), size.getY(), apron, treeBounds);
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
        apron.forEach((pos, state) -> {
            IBlockState previous = world.getBlockState(pos);
            if (previous.getBlock().isLeaves(previous, world, pos)) leavesBeforePlacement.add(pos);
            world.setBlockState(pos, state, 2);
        });
        // Grading skips neighbor updates. Check snow, plants, and both halves of clipped double plants.
        for (BlockPos pos : apron.keySet()) {
            removeUnsupportedDecoration(world, pos.down());
            removeUnsupportedDecoration(world, pos.up());
            removeUnsupportedDecoration(world, pos.up(2));
        }
        // Placement and grading can also sever canopies from trees outside the clearing.
        for (BlockPos pos : leavesBeforePlacement) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock().isLeaves(state, world, pos)) continue;
            for (EnumFacing side : EnumFacing.values()) leafCandidates.add(pos.offset(side));
        }
        clearUnsupportedLeaves(world, leafCandidates, treeBounds);
        placeChamberStatues(world, origin, settings);
        placeOutlyingRuins(world, random, origin, settings);
        return true;
    }

    private static IBlockState ruinBrick(Random random) {
        int roll = random.nextInt(4);
        return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT,
                roll == 0 ? BlockStoneBrick.EnumType.MOSSY
                        : roll == 1 ? BlockStoneBrick.EnumType.CRACKED : BlockStoneBrick.EnumType.DEFAULT);
    }

    private static IBlockState rubbleBlock(Random random) {
        switch (random.nextInt(6)) {
            case 0: return Blocks.COBBLESTONE.getDefaultState();
            case 1: return Blocks.MOSSY_COBBLESTONE.getDefaultState();
            case 2: return Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK);
            case 3: return Blocks.COBBLESTONE_WALL.getDefaultState();
            default: return ruinBrick(random);
        }
    }

    /** Returns the free spot on top of the terrain at a local template coordinate, or null if unsuitable. */
    private static BlockPos surfaceSpot(World world, BlockPos origin, PlacementSettings settings, int x, int z) {
        BlockPos column = Template.transformedBlockPos(settings, new BlockPos(x, 0, z)).add(origin);
        BlockPos ground = ground(world, column, false);
        IBlockState state = world.getBlockState(ground);
        if (state.getMaterial().isLiquid() || !state.isFullCube() || protectedBlock(world, ground, state)) return null;
        BlockPos spot = ground.up();
        IBlockState above = world.getBlockState(spot);
        if (!world.isAirBlock(spot) && !above.getMaterial().isReplaceable() && !(above.getBlock() instanceof BlockBush)) {
            return null;
        }
        return spot;
    }

    private static void setRuin(World world, BlockPos pos, IBlockState state) {
        IBlockState existing = world.getBlockState(pos);
        if (world.isAirBlock(pos) || existing.getMaterial().isReplaceable() || existing.getBlock() instanceof BlockBush) {
            world.setBlockState(pos, state, 2);
        }
    }

    /** Scatters broken wall stubs, lone pillars and stone rubble around the temple on the surrounding terrain. */
    private static void placeOutlyingRuins(World world, Random random, BlockPos origin, PlacementSettings settings) {
        // Ring of candidate positions: outside the stone floor (x 5..27, z 2..29), inside the graded apron.
        int ruins = 2 + random.nextInt(3);
        for (int i = 0; i < ruins * 4 && ruins > 0; i++) {
            int x = 5 - 11 + random.nextInt(23 + 22);
            int z = 2 - 11 + random.nextInt(28 + 22);
            int dx = Math.max(5 - x, Math.max(0, x - 27));
            int dz = Math.max(2 - z, Math.max(0, z - 29));
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < 3 || distance > 9) continue;
            BlockPos base = surfaceSpot(world, origin, settings, x, z);
            if (base == null) continue;
            ruins--;
            if (random.nextBoolean()) {
                // Broken wall stub, running along one local axis and crumbling toward its ends.
                boolean alongX = random.nextBoolean();
                int length = 2 + random.nextInt(4);
                int peak = 1 + random.nextInt(3);
                for (int n = 0; n < length; n++) {
                    BlockPos spot = surfaceSpot(world, origin, settings, x + (alongX ? n : 0), z + (alongX ? 0 : n));
                    if (spot == null || Math.abs(spot.getY() - base.getY()) > 1) continue;
                    int height = Math.max(1, peak - (n == 0 || n == length - 1 ? 1 : 0) - random.nextInt(2));
                    for (int y = 0; y < height; y++) setRuin(world, spot.up(y), ruinBrick(random));
                }
            } else {
                // Lone broken pillar.
                int height = 2 + random.nextInt(3);
                for (int y = 0; y < height; y++) {
                    setRuin(world, base.up(y), y == height - 1 && random.nextBoolean()
                            ? Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED)
                            : Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));
                }
            }
            // Fallen blocks around the ruin.
            for (int n = 0; n < 6; n++) {
                BlockPos spot = surfaceSpot(world, origin, settings, x - 3 + random.nextInt(7), z - 3 + random.nextInt(7));
                if (spot != null) setRuin(world, spot, rubbleBlock(random));
            }
        }
        // Loose rubble strewn across the grounds and the temple approaches.
        for (int i = 0; i < 70; i++) {
            int x = 5 - 9 + random.nextInt(23 + 18);
            int z = 2 - 9 + random.nextInt(28 + 18);
            int dx = Math.max(5 - x, Math.max(0, x - 27));
            int dz = Math.max(2 - z, Math.max(0, z - 29));
            if (dx == 0 && dz == 0) continue;
            if (dx * dx + dz * dz > 81) continue;
            BlockPos spot = surfaceSpot(world, origin, settings, x, z);
            if (spot == null) continue;
            setRuin(world, spot, rubbleBlock(random));
            if (random.nextInt(4) == 0) {
                // Small piles of two or three blocks.
                BlockPos next = spot.offset(EnumFacing.HORIZONTALS[random.nextInt(4)]);
                BlockPos pileSpot = ground(world, next, false).up();
                IBlockState pileGround = world.getBlockState(pileSpot.down());
                if (world.isAirBlock(pileSpot) && pileGround.isFullCube() && !pileGround.getMaterial().isLiquid()
                        && !protectedBlock(world, pileSpot.down(), pileGround)) {
                    setRuin(world, pileSpot, rubbleBlock(random));
                }
            }
        }
    }

    private static void placeChamberStatues(World world, BlockPos origin, PlacementSettings settings) {
        BlockPos center = Template.transformedBlockPos(settings, new BlockPos(16, 1, 14)).add(origin);
        for (BlockPos local : CHAMBER_STATUES) {
            BlockPos pos = Template.transformedBlockPos(settings, local).add(origin);
            if (!world.getBlockState(pos.down()).isFullCube() || !world.isAirBlock(pos) || !world.isAirBlock(pos.up())) {
                continue;
            }
            float yaw = (float) Math.toDegrees(MathHelper.atan2(center.getZ() - pos.getZ(), center.getX() - pos.getX())) - 90.0F;
            EntityStoneStatue statue = new EntityStoneStatue(world);
            statue.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, yaw, 0.0F);
            world.spawnEntity(statue);
        }
    }

    private static void removeUnsupportedDecoration(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean unsupported = block == Blocks.SNOW_LAYER && !block.canPlaceBlockAt(world, pos)
                || block instanceof BlockBush && !((BlockBush) block).canBlockStay(world, pos, state);
        if (unsupported && !protectedBlock(world, pos, state)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        }
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
        BlockPos pos = WorldGenEvents.getHeight(world, column).down();
        while (pos.getY() > 1) {
            IBlockState state = world.getBlockState(pos);
            if (!skipLiquid && state.getMaterial().isLiquid()) break;
            if (!treeBlock(world, pos, state) && !state.getMaterial().isReplaceable()
                    && !(state.getBlock() instanceof BlockBush)
                    && state.getBlock() != Blocks.SNOW_LAYER
                    && !(skipLiquid && state.getMaterial().isLiquid())) break;
            pos = pos.down();
        }
        return pos;
    }

    private static Set<BlockPos> clearTrees(World world, BlockPos corner, int width, int depth, int bottom, int height,
                                            Map<BlockPos, IBlockState> apron, StructureBoundingBox bounds) {
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
                if (next.getY() < bottom || !bounds.isVecInside(next)) continue;
                IBlockState state = world.getBlockState(next);
                if (state.getBlock().isWood(world, next) && !protectedBlock(world, next, state)
                        && wood.add(next)) pending.add(next);
            }
        }
        Set<BlockPos> leaves = new HashSet<>();
        for (BlockPos pos : wood) {
            if (protectedBlock(world, pos, world.getBlockState(pos))) continue;
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            removeUnsupportedDecoration(world, pos.up());
            for (int x = -4; x <= 4; x++) for (int y = -4; y <= 4; y++) for (int z = -4; z <= 4; z++) {
                BlockPos leaf = pos.add(x, y, z);
                if (!bounds.isVecInside(leaf)) continue;
                IBlockState state = world.getBlockState(leaf);
                if (state.getBlock().isLeaves(state, world, leaf)) leaves.add(leaf);
            }
        }
        return leaves;
    }

    private static void clearUnsupportedLeaves(World world, Set<BlockPos> candidates, StructureBoundingBox bounds) {
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> supported = new HashSet<>();
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        ArrayDeque<BlockPos> supportFrontier = new ArrayDeque<>();
        ArrayList<BlockPos> canopy = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (!bounds.isVecInside(candidate) || visited.contains(candidate)) continue;
            IBlockState state = world.getBlockState(candidate);
            if (!state.getBlock().isLeaves(state, world, candidate)) continue;
            visited.add(candidate);
            pending.add(candidate);
            canopy.clear();
            supported.clear();
            supportFrontier.clear();
            while (!pending.isEmpty()) {
                BlockPos leaf = pending.removeFirst();
                canopy.add(leaf);
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos next = leaf.offset(side);
                    if (!bounds.isVecInside(next)) {
                        // The canopy may connect to a trunk beyond the search area.
                        if (supported.add(leaf)) supportFrontier.add(leaf);
                        continue;
                    }
                    if (visited.contains(next)) continue;
                    IBlockState neighbor = world.getBlockState(next);
                    if (neighbor.getBlock().canSustainLeaves(neighbor, world, next)) {
                        if (supported.add(leaf)) supportFrontier.add(leaf);
                    } else if (neighbor.getBlock().isLeaves(neighbor, world, next)) {
                        visited.add(next);
                        pending.add(next);
                    }
                }
            }
            // Trace support through the leaves themselves, starting beside surviving trunks.
            for (int distance = 1; distance < LEAF_SUPPORT_DISTANCE && !supportFrontier.isEmpty(); distance++) {
                for (int count = supportFrontier.size(); count > 0; count--) {
                    BlockPos leaf = supportFrontier.removeFirst();
                    for (EnumFacing side : EnumFacing.values()) {
                        BlockPos next = leaf.offset(side);
                        if (visited.contains(next) && supported.add(next)) supportFrontier.add(next);
                    }
                }
            }
            for (BlockPos leaf : canopy) {
                if (supported.contains(leaf) || (!supported.isEmpty() && !candidates.contains(leaf))) continue;
                IBlockState leafState = world.getBlockState(leaf);
                if (leafState.getBlock() instanceof BlockLeaves && !leafState.getValue(BlockLeaves.DECAYABLE)) continue;
                if (!protectedBlock(world, leaf, leafState)) {
                    world.setBlockState(leaf, Blocks.AIR.getDefaultState(), 2);
                    // Snow resting on the canopy would otherwise be left floating.
                    removeUnsupportedDecoration(world, leaf.up());
                }
            }
        }
    }

    private static Map<BlockPos, IBlockState> planApron(World world, BlockPos origin,
                                                      PlacementSettings settings, int groundY) {
        Map<BlockPos, IBlockState> result = new LinkedHashMap<>();
        // The stone floor occupies local x=5..27, z=2..29. Taper the cut or fill
        // to zero at the outer edge so the apron meets the original surface.
        for (int x = 5 - BLEND_RADIUS; x <= 27 + BLEND_RADIUS; x++) {
            for (int z = 2 - BLEND_RADIUS; z <= 29 + BLEND_RADIUS; z++) {
                int dx = Math.max(5 - x, Math.max(0, x - 27));
                int dz = Math.max(2 - z, Math.max(0, z - 29));
                if (dx == 0 && dz == 0) continue;
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance >= BLEND_RADIUS) continue;
                double blend = (BLEND_RADIUS - distance) / (BLEND_RADIUS - 1);
                BlockPos column = Template.transformedBlockPos(settings, new BlockPos(x, 0, z)).add(origin);
                BlockPos ground = ground(world, column, true);
                int delta = groundY - ground.getY();
                int adjustment = (int) Math.round(Math.min(Math.abs(delta), MAX_TERRAIN_ADJUSTMENT) * blend);
                if (adjustment == 0) continue;
                boolean cut = delta < 0;
                int top = ground.getY() + (cut ? -adjustment : adjustment);
                IBlockState surface = surface(world.getBlockState(ground));
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
