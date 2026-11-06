package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadRuinProcessor;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorldGenMausoleum extends WorldGenerator {

    private static final ResourceLocation STRUCTURE = new ResourceLocation(IceAndFire.MODID, "dread_mausoleum");
    private static final ResourceLocation STRUCTURE_OLD = new ResourceLocation(IceAndFire.MODID, "dread_mausoleum_forge");
    private static final int PATH_LENGTH = 10;
    private static final int MAX_FOUNDATION_DEPTH = 4;
    private static final int MAX_BURIAL_DEPTH = 9;
    private static final int SNOW_MOUND_CHANCE = 3;
    private static final int SNOW_MOUND_MARGIN = 12;

    private final EnumFacing preferredFacing;

    public WorldGenMausoleum(EnumFacing facing) {
        super(false);
        this.preferredFacing = facing;
    }

    public static boolean isPartOfRuin(IBlockState state) {
        return DragonUtils.isDreadBlock(state);
    }

    public static Rotation getRotationFromFacing(EnumFacing facing) {
        switch (facing) {
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        position = position.add(rand.nextInt(8) - 4, 0, rand.nextInt(8) - 4);
        Template template = world.getSaveHandler().getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
                IceAndFire.CONFIG.enableDreadlands ? STRUCTURE : STRUCTURE_OLD);
        if (template == null) {
            return false;
        }

        BlockPos size = template.getSize();
        Map<BlockPos, BlockPos> terrain = new HashMap<>();
        Site best = null;
        EnumFacing facing = preferredFacing;
        for (int i = 0; i < 4; i++, facing = facing.rotateY()) {
            PlacementSettings settings = new PlacementSettings().setRotation(getRotationFromFacing(facing));
            BlockPos origin = position.subtract(Template.transformedBlockPos(settings,
                    new BlockPos(size.getX() / 2, 0, size.getZ() / 2)));
            Site candidate = findSite(world, terrain, size, origin, settings, facing);
            if (candidate != null && (best == null || candidate.cost < best.cost)) {
                best = candidate;
            }
        }
        if (best == null) {
            return false;
        }

        Site site = best;
        DreadRuinProcessor processor = new DreadRuinProcessor(position, site.settings, null);
        template.addBlocksToWorld(world, site.origin, (level, target, block) -> {
            // The front row contains the skull above open ground. Let the approach shape the ground below it.
            if (block.pos.getZ() == 0 && block.pos.getY() <= 3 && block.blockState.getBlock() == Blocks.AIR) {
                return null;
            }
            return processor.processBlock(level, target, block);
        }, site.settings, 2);
        supportFloor(world, terrain, site, size);

        Random decoration = new Random(world.getSeed() ^ site.origin.toLong());
        boolean mound = decoration.nextInt(SNOW_MOUND_CHANCE) == 0
                && site.origin.getY() + size.getY() + 8 < world.getHeight();
        Biome biome = world.getBiome(site.column(size.getX() / 2, 0));
        boolean forest = BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.CONIFEROUS);
        addSnowCover(world, terrain, site, size, decoration, mound, forest);
        buildApproach(world, terrain, site, size.getX() / 2, decoration, forest);
        weatherGate(world, site, size.getX() / 2, decoration);
        return true;
    }

    private Site findSite(World world, Map<BlockPos, BlockPos> terrain, BlockPos size, BlockPos origin,
                          PlacementSettings settings, EnumFacing facing) {
        Site layout = new Site(origin, settings, facing, null, 0);
        int center = size.getX() / 2;
        int frontY = ground(world, terrain, layout.column(center, 0)).getY();
        int backY = ground(world, terrain, layout.column(center, size.getZ() - 1)).getY();
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int x = 0; x < size.getX(); x++) {
            // The foundation starts at Z=1 in both templates.
            for (int z = 1; z < size.getZ(); z++) {
                BlockPos surface = ground(world, terrain, layout.column(x, z));
                if (!isDryGround(world.getBlockState(surface))) {
                    return null;
                }
                low = Math.min(low, surface.getY());
                high = Math.max(high, surface.getY());
            }
        }

        Site best = null;
        for (int y = Math.max(frontY - 1, high - MAX_BURIAL_DEPTH);
             y <= Math.min(frontY + 2, low + MAX_FOUNDATION_DEPTH); y++) {
            if (y <= 1 || y + size.getY() >= world.getHeight()) {
                continue;
            }
            int[] path = gradeApproach(world, terrain, layout, center, y);
            if (path == null) {
                continue;
            }
            int cost = Math.abs(y - frontY) * 3 + Math.max(0, y - low) * 2
                    + Math.max(0, path[PATH_LENGTH] - y) * 4 - MathHelper.clamp(backY - frontY, -6, 6);
            if (best == null || cost < best.cost) {
                best = new Site(new BlockPos(origin.getX(), y, origin.getZ()), settings, facing, path, cost);
            }
        }
        return best;
    }

    private int[] gradeApproach(World world, Map<BlockPos, BlockPos> terrain, Site layout, int center, int floorY) {
        int[] grade = new int[PATH_LENGTH + 1];
        grade[0] = floorY;
        int endY = ground(world, terrain, layout.column(center, 1 - PATH_LENGTH)).getY();
        for (int row = 1; row <= PATH_LENGTH; row++) {
            int z = 1 - row;
            int previousY = grade[row - 1];
            int remaining = PATH_LENGTH - row;
            int naturalY = ground(world, terrain, layout.column(center, z)).getY();
            int low = Math.max(previousY - 1, endY - remaining);
            int high = Math.min(previousY + 1, endY + remaining);
            if (low > high) {
                return null;
            }
            int y = row == 1 ? floorY : MathHelper.clamp(naturalY, low, high);
            if (y < low || y > high) {
                return null;
            }
            grade[row] = y;
            int halfWidth = pathHalfWidth(row);
            for (int x = center - halfWidth; x <= center + halfWidth; x++) {
                BlockPos surface = ground(world, terrain, layout.column(x, z));
                if (!isDryGround(world.getBlockState(surface)) || Math.abs(surface.getY() - y) > 2) {
                    return null;
                }
                // Keep standing trees in the woods and choose a route through a gap.
                for (int checkY = surface.getY() + 1; checkY <= y + 3; checkY++) {
                    if (world.getBlockState(new BlockPos(surface.getX(), checkY, surface.getZ())).getMaterial() == Material.WOOD) {
                        return null;
                    }
                }
            }
        }
        return grade;
    }

    private int pathHalfWidth(int row) {
        return row >= 8 ? 0 : row >= 3 && row <= 5 ? 2 : 1;
    }

    private BlockPos ground(World world, Map<BlockPos, BlockPos> terrain, BlockPos column) {
        return terrain.computeIfAbsent(new BlockPos(column.getX(), 0, column.getZ()), key -> {
            BlockPos surface = world.getHeight(key).down();
            while (surface.getY() > 1) {
                Material material = world.getBlockState(surface).getMaterial();
                if (material.isLiquid() || !(material.isReplaceable() || material == Material.LEAVES || material == Material.WOOD)) {
                    break;
                }
                surface = surface.down();
            }
            return surface;
        });
    }

    private boolean isDryGround(IBlockState state) {
        Material material = state.getMaterial();
        return state.isFullCube() && material != Material.ICE && material != Material.WOOD
                && material != Material.LEAVES && !material.isLiquid() && !isPartOfRuin(state);
    }

    private void supportFloor(World world, Map<BlockPos, BlockPos> terrain, Site site, BlockPos size) {
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 1; z < size.getZ(); z++) {
                BlockPos column = site.column(x, z);
                BlockPos surface = ground(world, terrain, column);
                if (!world.getBlockState(column).isFullCube()) {
                    continue;
                }
                for (BlockPos fill = column.down(); fill.getY() > surface.getY(); fill = fill.down()) {
                    world.setBlockState(fill, Blocks.STONE.getDefaultState(), 2);
                }
            }
        }
    }

    private void addSnowCover(World world, Map<BlockPos, BlockPos> terrain, Site site, BlockPos size,
                              Random random, boolean mound, boolean forest) {
        int centerX = size.getX() / 2;
        int centerZ = size.getZ() / 2;
        int baseY = site.origin.getY();
        int margin = mound ? SNOW_MOUND_MARGIN : 5;
        NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin(random, 1);
        IBlockState snow = Blocks.SNOW.getDefaultState();
        IBlockState ice = Blocks.PACKED_ICE.getDefaultState();

        for (int x = -margin; x < size.getX() + margin; x++) {
            for (int z = -margin; z < size.getZ() + margin; z++) {
                int across = Math.abs(x - centerX);
                // Keep the skull, the gate and the forecourt clear, including on buried variants.
                if ((z <= 2 && across <= 4) || (z <= 0 && across <= 7)) {
                    continue;
                }
                int outsideX = Math.max(0, Math.max(-x, x - size.getX() + 1));
                int outsideZ = Math.max(0, Math.max(1 - z, z - size.getZ() + 1));
                double distance = Math.hypot(outsideX, outsideZ);
                if (distance >= margin) {
                    continue;
                }

                BlockPos column = site.column(x, z);
                BlockPos snowPos = world.getPrecipitationHeight(column);
                IBlockState support = world.getBlockState(snowPos.down());
                if (!world.getBiome(snowPos).getEnableSnow()
                        || !(isDryGround(support) || isPartOfRuin(support))) {
                    continue;
                }
                // Preserve tree cover and water outside the footprint, even where the canopy hides them.
                if (distance > 0 && !isDryGround(world.getBlockState(ground(world, terrain, column)))) {
                    continue;
                }
                if (distance == 0 && !isPartOfRuin(support)) {
                    continue;
                }

                double variation = noise.getValue(x * 0.18, z * 0.18);
                double taper = 1 - distance / margin;
                if (z <= 2) {
                    int clearWidth = z <= 0 ? 7 : 4;
                    taper *= MathHelper.clamp((across - clearWidth + variation) / 5.0, 0, 1);
                }
                taper = taper * taper * (3 - 2 * taper);
                int surfaceY = snowPos.getY();
                int topY = surfaceY;
                if (mound) {
                    double crown = Math.max(0, 1 - Math.hypot((x - centerX) / (double) centerX,
                            (z - centerZ) / (double) centerZ));
                    double crestY = baseY + size.getY() + 3 + 4 * crown + variation;
                    topY += (int) Math.floor((crestY - surfaceY) * taper);
                } else if (distance > 0) {
                    double drift = Math.max(baseY + 1 - surfaceY, forest ? 1.6 : 2.8) + variation * 1.4;
                    topY = (int) Math.floor(surfaceY + taper * Math.max(0, drift));
                }
                int iceY = baseY + 4 + (int) Math.floor(variation * 3);
                boolean rubble = !mound && distance > 0 && distance < 3 && variation < 0 && random.nextInt(7) == 0;
                if (rubble && world.getBlockState(snowPos).getMaterial().isReplaceable()) {
                    world.setBlockState(snowPos, rubble(random), 2);
                    snowPos = snowPos.up();
                }
                while (snowPos.getY() < topY && world.getBlockState(snowPos).getMaterial().isReplaceable()) {
                    int y = snowPos.getY();
                    world.setBlockState(snowPos, mound && y >= iceY && y < iceY + 4 && y < topY - 1 ? ice : snow, 2);
                    snowPos = snowPos.up();
                }
                if ((distance == 0 || variation + taper > 0.75) && world.canSnowAt(snowPos, true)) {
                    int layers = MathHelper.clamp(1 + (int) Math.floor((variation + taper) * 2), 1, 5);
                    world.setBlockState(snowPos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layers), 2);
                }
            }
        }
    }

    private void buildApproach(World world, Map<BlockPos, BlockPos> terrain, Site site, int center,
                               Random random, boolean forest) {
        for (int row = 1; row <= PATH_LENGTH; row++) {
            int y = site.grade[row];
            int halfWidth = pathHalfWidth(row);
            EnumFacing uphill = y > site.grade[row - 1] ? site.facing : site.facing.getOpposite();
            boolean step = y > site.grade[row - 1] || (row < PATH_LENGTH && y > site.grade[row + 1]);
            for (int offset = -halfWidth; offset <= halfWidth; offset++) {
                BlockPos column = site.column(center + offset, 1 - row);
                BlockPos surface = ground(world, terrain, column);
                BlockPos tread = new BlockPos(column.getX(), y, column.getZ());
                for (BlockPos fill = surface.up(); fill.getY() < y; fill = fill.up()) {
                    world.setBlockState(fill, Blocks.COBBLESTONE.getDefaultState(), 2);
                }
                if (step) {
                    world.setBlockState(tread, Blocks.STONE_BRICK_STAIRS.getDefaultState()
                            .withProperty(BlockStairs.FACING, uphill), 2);
                } else if (surface.getY() != y || row < PATH_LENGTH && random.nextInt(row <= 5 ? 5 : 2) != 0) {
                    world.setBlockState(tread, rubble(random), 2);
                }
                for (int headroom = 1; headroom <= 3; headroom++) {
                    world.setBlockState(tread.up(headroom), Blocks.AIR.getDefaultState(), 2);
                }
            }
        }

        int side = random.nextBoolean() ? 1 : -1;
        for (int i = 0; i < 3; i++, side = -side) {
            BlockPos marker = findDecorationSpot(world, site.column(center + side * (4 + random.nextInt(2)), -2 - i * 2));
            if (marker != null) {
                world.setBlockState(marker, rubble(random), 2);
                world.setBlockState(marker.up(), Blocks.STONE_BRICK_STAIRS.getDefaultState()
                        .withProperty(BlockStairs.FACING, i == 1 ? site.facing.rotateY() : site.facing), 2);
            }
        }

        if (!forest) {
            for (int bank = -1; bank <= 1; bank += 2) {
                BlockPos column = site.column(center + bank * (6 + random.nextInt(2)), -3 - random.nextInt(5));
                BlockPos rock = findDecorationSpot(world, column);
                if (rock != null) {
                    world.setBlockState(rock, Blocks.STONE.getDefaultState(), 2);
                    world.setBlockState(rock.up(), Blocks.COBBLESTONE.getDefaultState(), 2);
                    EnumFacing outward = bank < 0 ? site.facing.rotateYCCW() : site.facing.rotateY();
                    BlockPos shoulder = findDecorationSpot(world, column.offset(outward));
                    if (shoulder != null && Math.abs(shoulder.getY() - rock.getY()) <= 1) {
                        world.setBlockState(shoulder, Blocks.STONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    private BlockPos findDecorationSpot(World world, BlockPos column) {
        BlockPos pos = world.getPrecipitationHeight(column);
        return isDryGround(world.getBlockState(pos.down())) && world.getBlockState(pos).getMaterial().isReplaceable()
                && world.getBlockState(pos.up()).getMaterial().isReplaceable() ? pos : null;
    }

    private IBlockState rubble(Random random) {
        switch (random.nextInt(4)) {
            case 0:
                return Blocks.COBBLESTONE.getDefaultState();
            case 1:
                return Blocks.MOSSY_COBBLESTONE.getDefaultState();
            default:
                return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
        }
    }

    private void weatherGate(World world, Site site, int center, Random random) {
        for (int side = -2; side <= 2; side += 4) {
            for (int y = 1; y <= 5; y++) {
                BlockPos pos = site.column(center + side, 2).up(y);
                if (world.getBlockState(pos).getBlock() == IafBlockRegistry.dread_stone_bricks
                        && (y <= 2 || random.nextInt(3) == 0)) {
                    world.setBlockState(pos, IafBlockRegistry.dread_stone_bricks_cracked.getDefaultState(), 2);
                }
            }
        }
    }

    private static class Site {
        private final BlockPos origin;
        private final PlacementSettings settings;
        private final EnumFacing facing;
        private final int[] grade;
        private final int cost;

        private Site(BlockPos origin, PlacementSettings settings, EnumFacing facing, int[] grade, int cost) {
            this.origin = origin;
            this.settings = settings;
            this.facing = facing;
            this.grade = grade;
            this.cost = cost;
        }

        private BlockPos column(int x, int z) {
            return origin.add(Template.transformedBlockPos(settings, new BlockPos(x, 0, z)));
        }
    }
}
