package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import net.minecraft.block.BlockBone;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class WorldGenCyclopsCave extends WorldGenerator {

    public static final ResourceLocation CYCLOPS_CHEST = LootTableList.register(new ResourceLocation("iceandfire", "cyclops_cave"));

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int radiusX = 16 + rand.nextInt(2);
        int radiusZ = 15 + rand.nextInt(3);
        int height = 18 + rand.nextInt(2);
        double phase = rand.nextDouble() * Math.PI * 2;
        long shapeSeed = rand.nextLong();
        EnumFacing entrance = EnumFacing.HORIZONTALS[rand.nextInt(4)];
        // Snapshot the original terrain before placing anything. The outer apron
        // follows this surface rather than stopping at the chamber's floor height.
        int reach = 44;
        int span = reach * 2 + 1;
        int[][] ground = new int[span][span];
        int[][] support = new int[span][span];
        IBlockState[][] cover = new IBlockState[span][span];
        List<BlockPos> plantedSurfaces = new ArrayList<>();
        boolean[][] water = new boolean[span][span];
        boolean[][] snow = new boolean[span][span];
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                BlockPos surface = worldIn.getHeight(position.add(x, 0, z)).down();
                Material material = worldIn.getBlockState(surface).getMaterial();
                snow[x + reach][z + reach] = material == Material.SNOW || material == Material.CRAFTED_SNOW;
                while (surface.getY() > 1 && (material == Material.LEAVES || material == Material.WOOD
                        || material.isReplaceable())) {
                    surface = surface.down();
                    material = worldIn.getBlockState(surface).getMaterial();
                }
                ground[x + reach][z + reach] = surface.getY() - position.getY();
                Biome biome = worldIn.getBiome(surface);
                IBlockState originalCover = worldIn.getBlockState(surface);
                // Exposed stone and sand belong to the site; grassy soil follows
                // the biome's own surface (including podzol, mycelium and modded soils).
                cover[x + reach][z + reach] = (material == Material.GRASS || material == Material.GROUND)
                        && biome.topBlock != null && biome.topBlock.isFullCube()
                        ? biome.topBlock : originalCover;
                water[x + reach][z + reach] = material.isLiquid() || material == Material.ICE;
                if (material == Material.ICE || material.isLiquid() || material == Material.SNOW
                        || material == Material.CRAFTED_SNOW) {
                    cover[x + reach][z + reach] = biome.topBlock != null && biome.topBlock.isFullCube()
                            && biome.topBlock.getMaterial() != Material.ICE
                            && !biome.topBlock.getMaterial().isLiquid()
                            ? biome.topBlock : Blocks.GRASS.getDefaultState();
                }
                BlockPos foundation = surface;
                if (water[x + reach][z + reach]) {
                    // Shallow water can be bridged by a continuous rock foundation.
                    while (foundation.getY() > 1 && foundation.getY() >= position.getY() - 12
                            && (worldIn.getBlockState(foundation).getMaterial().isLiquid()
                            || worldIn.getBlockState(foundation).getMaterial() == Material.ICE)) {
                        foundation = foundation.down();
                    }
                }
                support[x + reach][z + reach] = worldIn.isBlockFullCube(foundation)
                        ? foundation.getY() - position.getY() : -1000;
            }
        }
        // Keep the complete shell supported while allowing shallow shore overlap
        // and ordinary hillside variation. Never mask pieces out of the wall.
        int shellColumns = 0;
        int wetShellColumns = 0;
        int[][] shoreDistance = new int[span][span];
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                double radial = caveRadial(x, z, radiusX, radiusZ, phase);
                int original = ground[x + reach][z + reach];
                if (radial < 1.65) {
                    shellColumns++;
                    if (water[x + reach][z + reach]) {
                        wetShellColumns++;
                    }
                    if (support[x + reach][z + reach] < -8 || original > 10) {
                        return false;
                    }
                }
                shoreDistance[x + reach][z + reach] = water[x + reach][z + reach] ? 0 : span;
            }
        }
        if (wetShellColumns * 100 > shellColumns * 35) {
            return false;
        }
        // Distance to wet ground feathers the apron before it reaches the shore.
        for (int x = 0; x < span; x++) {
            for (int z = 0; z < span; z++) {
                if (x > 0) shoreDistance[x][z] = Math.min(shoreDistance[x][z], shoreDistance[x - 1][z] + 1);
                if (z > 0) shoreDistance[x][z] = Math.min(shoreDistance[x][z], shoreDistance[x][z - 1] + 1);
            }
        }
        for (int x = span - 1; x >= 0; x--) {
            for (int z = span - 1; z >= 0; z--) {
                if (x + 1 < span) shoreDistance[x][z] = Math.min(shoreDistance[x][z], shoreDistance[x + 1][z] + 1);
                if (z + 1 < span) shoreDistance[x][z] = Math.min(shoreDistance[x][z], shoreDistance[x][z + 1] + 1);
            }
        }
        boolean[][] connected = new boolean[span][span];
        java.util.ArrayDeque<BlockPos> pending = new java.util.ArrayDeque<>();
        pending.add(new BlockPos(reach, 0, reach));
        connected[reach][reach] = true;
        while (!pending.isEmpty()) {
            BlockPos current = pending.removeFirst();
            for (EnumFacing direction : EnumFacing.HORIZONTALS) {
                int x = current.getX() + direction.getXOffset();
                int z = current.getZ() + direction.getZOffset();
                if (x >= 0 && z >= 0 && x < span && z < span && !connected[x][z]
                        && (!water[x][z] || caveRadial(x - reach, z - reach, radiusX, radiusZ, phase) < 1.65)) {
                    connected[x][z] = true;
                    pending.addLast(new BlockPos(x, 0, z));
                }
            }
        }
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                double across = x * entrance.getZOffset() - z * entrance.getXOffset();
                double forward = x * entrance.getXOffset() + z * entrance.getZOffset();
                // A broad, off-center hill has a shorter entrance face and long
                // shoulders behind it. Low-frequency variation breaks contour rings.
                double longitudinalRadius = forward > 0 ? 25 : 33;
                double warpedAcross = across + 4 * terrainNoise(x / 18.0, z / 18.0, shapeSeed);
                double warpedForward = forward + 4 * terrainNoise(x / 18.0, z / 18.0, shapeSeed + 1);
                double angle = Math.atan2(forward, across);
                double outline = 1 + 0.13 * Math.sin(angle * 3 + phase)
                        + 0.07 * Math.sin(angle * 5 - phase);
                double distance = Math.sqrt(warpedAcross * warpedAcross / (32 * 32)
                        + warpedForward * warpedForward / (longitudinalRadius * longitudinalRadius)) / outline;
                double radial = caveRadial(x, z, radiusX, radiusZ, phase);
                if ((distance >= 1 && radial >= 1.65) || !connected[x + reach][z + reach]) {
                    continue;
                }
                distance = Math.min(distance, 0.999);
                int original = ground[x + reach][z + reach];
                boolean chamber = radial < 1;
                if (water[x + reach][z + reach] && radial >= 1.65) {
                    continue;
                }
                double taper = Math.min(1, (1 - distance) / 0.3);
                taper = taper * taper * (3 - 2 * taper);
                double shore = Math.min(1, shoreDistance[x + reach][z + reach] / 8.0);
                taper *= shore * shore * (3 - 2 * shore);
                double ridge = 2.8 * Math.sin(x * 0.16 + phase)
                        + 2.1 * Math.sin(z * 0.19 - x * 0.07 - phase)
                        + 0.8 * patch(x, 3, z, phase + 1)
                        + 5.0 * terrainNoise(x / 11.0, z / 11.0, shapeSeed + 2)
                        + 1.8 * terrainNoise(x / 4.5, z / 4.5, shapeSeed + 3);
                double hill = height * (1 - distance * distance) + ridge * (1 - distance);
                int roof = (int) Math.floor(original + Math.max(0, hill - original) * taper);
                if (radial < 1.65) {
                    double collar = Math.min(1, (1.65 - radial) / 0.65);
                    int requiredRoof = (int) Math.ceil(collar
                            * (13 + (height - 13) * Math.sqrt(Math.max(0, 1 - radial))));
                    roof = Math.max(roof, requiredRoof);
                }
                if (roof <= original && !chamber) {
                    continue;
                }
                boolean approach = forward > 0 && Math.abs(across) <= 7;
                int bottom = radial < 1.65 || approach
                        ? Math.min(-3, Math.max(original - 8, support[x + reach][z + reach])) : original + 1;
                int floorHeight = Math.hypot(x, z) > 7
                        && !isEntranceLane(position.add(x, 0, z), position, entrance, 4)
                        && patch(x, 0, z, phase + 1) > 0.45 ? 1 : 0;
                // Level pads at the side walls support pens without flattening
                // the room or obstructing the central pursuit route.
                if (Math.abs(across) >= 6 && Math.abs(across) <= 10 && forward >= -5 && forward <= -1) {
                    floorHeight = 1;
                }
                int highestPlaced = Integer.MIN_VALUE;
                for (int y = bottom; y <= roof; y++) {
                    BlockPos target = position.add(x, y, z);
                    if (target.getY() < 1 || target.getY() >= worldIn.getHeight() - 1
                            || worldIn.getTileEntity(target) != null
                            || worldIn.getBlockState(target).getBlockHardness(worldIn, target) < 0) {
                        continue;
                    }
                    double inner = radial + (double) Math.max(0, y - 10) * Math.max(0, y - 10)
                            / ((height - 13) * (height - 13));
                    double archCenter = 0.9 * Math.sin(forward * 0.21 + phase)
                            + 0.65 * Math.sin(y * 0.32 - phase);
                    double archWidth = (across < archCenter ? 6.5 : 6.0)
                            + 0.9 * Math.sin(forward * 0.36 + y * 0.42 + phase);
                    boolean doorway = forward > 0 && ((Math.abs(across) <= 3 && y <= 10)
                            || (across - archCenter) * (across - archCenter) / (archWidth * archWidth)
                            + (double) Math.max(0, y - 10) * Math.max(0, y - 10) / 36 < 1);
                    // Shallow wall recesses vary vertically, without narrowing
                    // the guaranteed full-height chamber.
                    double wallRecess = y <= 10 ? Math.max(0, patch(x, y, z, phase + 3)) * 0.045 : 0;
                    if (y > floorHeight && (inner < 1 + wallRecess || doorway)) {
                        worldIn.setBlockState(target, Blocks.AIR.getDefaultState(), 2);
                    } else if (y == floorHeight && (chamber || approach)) {
                        double floor = patch(x, 0, z, phase);
                        worldIn.setBlockState(target, (floor > 0.9 ? Blocks.MOSSY_COBBLESTONE : floor > 0.35 ? Blocks.DIRT
                                : floor > -0.35 ? Blocks.STONE : floor > -0.95 ? Blocks.GRAVEL : Blocks.COBBLESTONE).getDefaultState(), 2);
                    } else if (y >= 0 || !worldIn.isBlockFullCube(target)) {
                        double rock = patch(x, y, z, phase);
                        worldIn.setBlockState(target, (rock > 1.15 && y < 5 ? Blocks.MOSSY_COBBLESTONE
                                : rock < -0.8 ? Blocks.COBBLESTONE : Blocks.STONE).getDefaultState(), 2);
                        highestPlaced = y;
                    }
                }
                // Soil covers the whole supported slope, rather than isolated caps.
                // The last few blocks inherit the beach's sand where appropriate.
                if (highestPlaced == roof) {
                    BlockPos top = position.add(x, roof, z);
                    if (worldIn.isBlockFullCube(top.down()) && worldIn.isBlockFullCube(top.down(2))) {
                        IBlockState surfaceCover = cover[x + reach][z + reach];
                        Material surfaceMaterial = surfaceCover.getMaterial();
                        IBlockState filler = surfaceMaterial == Material.ROCK ? surfaceCover
                                : surfaceMaterial == Material.SAND ? Blocks.SANDSTONE.getDefaultState()
                                : worldIn.getBiome(top).fillerBlock;
                        if (filler == null || !filler.isFullCube()) {
                            filler = Blocks.DIRT.getDefaultState();
                        }
                        for (int depth = 0; depth < 3; depth++) {
                            BlockPos soil = top.down(depth);
                            if (worldIn.getTileEntity(soil) == null
                                    && worldIn.getBlockState(soil).getBlockHardness(worldIn, soil) >= 0) {
                                worldIn.setBlockState(soil, depth == 0 ? surfaceCover : filler, 2);
                            }
                        }
                    }
                    plantedSurfaces.add(top);
                    // Remove vegetation overtaken by the new slope, including
                    // leftover canopy above buried trunks.
                    int oldTop = worldIn.getHeight(top).getY();
                    for (int y = top.getY() + 1; y < oldTop; y++) {
                        BlockPos vegetation = new BlockPos(top.getX(), y, top.getZ());
                        Material material = worldIn.getBlockState(vegetation).getMaterial();
                        if (worldIn.getTileEntity(vegetation) == null
                                && (material == Material.LEAVES || material == Material.WOOD
                                || material == Material.PLANTS || material == Material.VINE)) {
                            worldIn.setBlockState(vegetation, Blocks.AIR.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        // Plant only after all terrain columns are finished, so later columns
        // cannot bury new foliage. Bare rock and sand remain bare.
        for (BlockPos top : plantedSurfaces) {
            if (!worldIn.isAirBlock(top.up()) || worldIn.getBlockState(top).getMaterial() != Material.GRASS) {
                continue;
            }
            Biome biome = worldIn.getBiome(top);
            boolean snowy = snow[top.getX() - position.getX() + reach][top.getZ() - position.getZ() + reach]
                    || biome.getEnableSnow() || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
                    || biome.getTemperature(top.up()) < 0.15F;
            if (snowy) {
                continue;
            }
            if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)
                    && rand.nextInt(180) == 0) {
                biome.getRandomTreeFeature(rand).generate(worldIn, rand, top.up());
            } else if (biome.decorator.grassPerChunk > 0 && rand.nextInt(4) == 0) {
                IBlockState grass = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE,
                        BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST) && rand.nextBoolean()
                                ? BlockTallGrass.EnumType.FERN : BlockTallGrass.EnumType.GRASS);
                if (Blocks.TALLGRASS.canPlaceBlockAt(worldIn, top.up())) {
                    worldIn.setBlockState(top.up(), grass, 2);
                }
            } else if (biome.decorator.flowersPerChunk > 0 && rand.nextInt(65) == 0
                    && Blocks.YELLOW_FLOWER.canPlaceBlockAt(worldIn, top.up())) {
                worldIn.setBlockState(top.up(), Blocks.YELLOW_FLOWER.getDefaultState(), 2);
            }
        }
        // Apply snow after foliage and terrain, including exposed stone roofs.
        for (BlockPos top : plantedSurfaces) {
            Biome biome = worldIn.getBiome(top);
            boolean snowy = snow[top.getX() - position.getX() + reach][top.getZ() - position.getZ() + reach]
                    || biome.getEnableSnow() || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)
                    || biome.getTemperature(top.up()) < 0.15F;
            if (snowy && worldIn.isAirBlock(top.up()) && worldIn.canSeeSky(top.up())
                    && Blocks.SNOW_LAYER.canPlaceBlockAt(worldIn, top.up())) {
                worldIn.setBlockState(top.up(), Blocks.SNOW_LAYER.getDefaultState(), 2);
            }
        }
        // Place livestock before incidental decorations can consume its space.
        EnumFacing penSide = entrance.rotateY();
        int firstSide = rand.nextBoolean() ? 1 : -1;
        for (int pen = 0; pen < 2; pen++) {
            if (pen == 1 && rand.nextBoolean()) {
                break;
            }
            int side = pen == 0 ? firstSide : -firstSide;
            BlockPos cornerA = position.offset(penSide, side * 6).offset(entrance, -5);
            BlockPos cornerB = position.offset(penSide, side * 10).offset(entrance, -1);
            BlockPos penOrigin = new BlockPos(Math.min(cornerA.getX(), cornerB.getX()),
                    position.getY() + 2, Math.min(cornerA.getZ(), cornerB.getZ()));
            genSheepPen(worldIn, penOrigin, rand, position, 13, entrance);
        }
        if (rand.nextBoolean()) {
            genSleepingBed(worldIn, rand, position, entrance);
        }
        {
            int j = 10;
            int k = 0;
            int l = 10;
            float f = 10;
            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
                if (blockpos.distanceSq(position) <= (double) (f * f) && blockpos.distanceSq(position) >= 49
                        && blockpos.getY() == position.getY() && worldIn.isAirBlock(blockpos.up())
                        && !isEntranceLane(blockpos, position, entrance, 8)) {
                    if (rand.nextInt(130) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        this.genSkeleton(worldIn, blockpos.up(), rand, position, f, entrance);
                    }

                    if (rand.nextInt(80) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), IafBlockRegistry.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, 8), 3);
                        worldIn.setBlockState(blockpos.up().north(), IafBlockRegistry.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().south(), IafBlockRegistry.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().west(), IafBlockRegistry.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().east(), IafBlockRegistry.goldPile.getDefaultState().withProperty(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up(2), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]), 2);
                        if (worldIn.getBlockState(blockpos.up(2)).getBlock() instanceof BlockChest) {
                            TileEntity tileentity1 = worldIn.getTileEntity(blockpos.up(2));
                            if (tileentity1 instanceof TileEntityChest && !tileentity1.isInvalid()) {
                                ((TileEntityChest) tileentity1).setLootTable(CYCLOPS_CHEST, rand.nextLong());
                            }
                        }

                    }


                    if (rand.nextInt(50) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        int torchHeight = rand.nextInt(2) + 1;
                        for (int fence = 0; fence < torchHeight; fence++) {
                            worldIn.setBlockState(blockpos.up(1 + fence), Blocks.OAK_FENCE.getDefaultState());
                        }
                        worldIn.setBlockState(blockpos.up(1 + torchHeight), Blocks.TORCH.getDefaultState());
                    }
                }
            }
        }
        EntityCyclops cyclops = new EntityCyclops(worldIn);
        cyclops.setVariant(rand.nextInt(3));
        cyclops.setPositionAndRotation(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
        worldIn.spawnEntity(cyclops);
        return true;
    }

    private static double caveRadial(int x, int z, int radiusX, int radiusZ, double phase) {
        double lobes = 1.06 + 0.035 * patch(x, 2, z, phase);
        return ((double) x * x / ((radiusX - 3) * (radiusX - 3))
                + (double) z * z / ((radiusZ - 3) * (radiusZ - 3))) / (lobes * lobes);
    }

    // Smooth value noise gives each cave independent ridges and gullies.
    private static double terrainNoise(double x, double z, long seed) {
        int ix = (int) Math.floor(x);
        int iz = (int) Math.floor(z);
        double u = x - ix;
        double v = z - iz;
        u = u * u * (3 - 2 * u);
        v = v * v * (3 - 2 * v);
        double a = noiseCorner(ix, iz, seed);
        double b = noiseCorner(ix + 1, iz, seed);
        double c = noiseCorner(ix, iz + 1, seed);
        double d = noiseCorner(ix + 1, iz + 1, seed);
        return (a + (b - a) * u) * (1 - v) + (c + (d - c) * u) * v;
    }

    private static double noiseCorner(int x, int z, long seed) {
        long hash = seed ^ (x * 341873128712L) ^ (z * 132897987541L);
        hash = (hash ^ (hash >>> 30)) * 0xbf58476d1ce4e5b9L;
        hash = (hash ^ (hash >>> 27)) * 0x94d049bb133111ebL;
        hash ^= hash >>> 31;
        return (hash >>> 11) * 0x1.0p-53 * 2 - 1;
    }

    private void genSleepingBed(World world, Random random, BlockPos origin, EnumFacing entrance) {
        // A giant's low straw mattress, oriented into the rear of the chamber.
        // Validate everything first so a rejected site leaves no partial bed.
        EnumFacing sideways = entrance.rotateY();
        for (int length = 4; length <= 11; length++) {
            for (int width = -2; width <= 2; width++) {
                BlockPos base = origin.offset(entrance.getOpposite(), length).offset(sideways, width).up();
                if (!world.isBlockFullCube(base.down()) || world.getTileEntity(base) != null) {
                    return;
                }
                IBlockState state = world.getBlockState(base);
                if (!world.isAirBlock(base) && state.getBlock() != Blocks.STONE
                        && state.getBlock() != Blocks.DIRT && state.getBlock() != Blocks.GRAVEL
                        && state.getBlock() != Blocks.COBBLESTONE && state.getBlock() != Blocks.MOSSY_COBBLESTONE) {
                    return;
                }
                for (int y = 1; y <= 9; y++) {
                    if (!world.isAirBlock(base.up(y))) {
                        return;
                    }
                }
            }
        }
        EnumDyeColor blanket = random.nextBoolean() ? EnumDyeColor.BROWN : EnumDyeColor.GRAY;
        for (int length = 4; length <= 11; length++) {
            for (int width = -2; width <= 2; width++) {
                // Trim the corners and leave loose straw at the foot and sides.
                if ((length == 4 || length == 11) && Math.abs(width) == 2) {
                    continue;
                }
                BlockPos base = origin.offset(entrance.getOpposite(), length).offset(sideways, width).up();
                world.setBlockState(base, Blocks.HAY_BLOCK.getDefaultState(), 2);
                if (Math.abs(width) < 2 && length >= 5) {
                    world.setBlockState(base.up(), Blocks.CARPET.getDefaultState()
                            .withProperty(BlockCarpet.COLOR, length >= 10 ? EnumDyeColor.WHITE : blanket), 2);
                }
            }
        }
    }

    private static boolean isEntranceLane(BlockPos target, BlockPos origin, EnumFacing entrance, int halfWidth) {
        int x = target.getX() - origin.getX();
        int z = target.getZ() - origin.getZ();
        int forward = x * entrance.getXOffset() + z * entrance.getZOffset();
        int across = x * entrance.getZOffset() - z * entrance.getXOffset();
        return forward >= -2 && Math.abs(across) <= halfWidth;
    }

    private static double patch(int x, int y, int z, double phase) {
        return Math.sin(x * 0.29 + z * 0.13 + phase)
                + 0.55 * Math.cos(z * 0.37 - y * 0.21 - phase)
                + 0.25 * Math.sin(x * 0.61 - z * 0.43 + y * 0.31);
    }

    private boolean genSheepPen(World worldIn, BlockPos blockpos, Random rand, BlockPos origin, float radius, EnumFacing entrance) {
        int width = 4;
        // Validate the entire enclosure before placing fences or spawning sheep.
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= width; z++) {
                BlockPos target = blockpos.add(x, 0, z);
                if (origin.distanceSq(target) > radius * radius
                        || target.distanceSq(origin.up()) < 36 || isEntranceLane(target, origin, entrance, 4)
                        || !worldIn.isAirBlock(target) || !worldIn.isAirBlock(target.up())
                        || !worldIn.isBlockFullCube(target.down())) {
                    return false;
                }
            }
        }
        for (int x = 0; x <= width; x++) {
            for (int z = 0; z <= width; z++) {
                if (x == 0 || z == 0 || x == width || z == width) {
                    worldIn.setBlockState(blockpos.add(x, 0, z), Blocks.OAK_FENCE.getDefaultState(), 2);
                } else if (rand.nextInt(5) == 0) {
                    worldIn.setBlockState(blockpos.add(x, -1, z), Blocks.HAY_BLOCK.getDefaultState(), 2);
                }
            }
        }
        int sheepCount = 2 + rand.nextInt(3);
        for (int sheep = 0; sheep < sheepCount; sheep++) {
            EntitySheep entitySheep = new EntitySheep(worldIn);
            entitySheep.setPosition(blockpos.getX() + 1.5 + sheep % 2,
                    blockpos.getY(), blockpos.getZ() + 1.5 + sheep / 2);
            entitySheep.setFleeceColor(rand.nextInt(4) == 0 ? EnumDyeColor.YELLOW : EnumDyeColor.WHITE);
            if (!worldIn.isRemote) {
                worldIn.spawnEntity(entitySheep);
            }
        }
        return true;
    }

    private boolean isTouchingAir(World worldIn, BlockPos pos) {
        boolean isTouchingAir = worldIn.isAirBlock(pos) && worldIn.isAirBlock(pos.up())
                && worldIn.isBlockFullCube(pos.down());
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            if (!worldIn.isAirBlock(pos.offset(direction))) {
                isTouchingAir = false;
            }
        }
        return isTouchingAir;
    }

    private void genSkeleton(World worldIn, BlockPos blockpos, Random rand, BlockPos origin, float radius, EnumFacing entrance) {
        EnumFacing direction = EnumFacing.HORIZONTALS[rand.nextInt(4)];
        int length = 5 + rand.nextInt(2) * 2;
        for (int spine = 0; spine < length; spine++) {
            for (int side = -2; side <= 2; side++) {
                BlockPos target = blockpos.offset(direction, spine).offset(direction.rotateY(), side);
                if (target.distanceSq(origin.up()) < 49 || isEntranceLane(target, origin, entrance, 8) || origin.distanceSq(target) > radius * radius
                        || !worldIn.isBlockFullCube(target.down())) {
                    return;
                }
                for (int y = 0; y <= 3; y++) {
                    if (!worldIn.isAirBlock(target.up(y))) {
                        return;
                    }
                }
            }
        }
        EnumFacing.Axis oppositeAxis = direction.getAxis() == EnumFacing.Axis.X ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
        int maxRibHeight = rand.nextInt(2);
        for (int spine = 0; spine < length; spine++) {
            BlockPos segment = blockpos.offset(direction, spine);
            if (origin.distanceSq(segment) <= (double) (radius * radius)) {
                worldIn.setBlockState(segment, Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, direction.getAxis()));
            }
            if (spine % 2 != 0) {
                BlockPos rightRib = segment.offset(direction.rotateYCCW());
                BlockPos leftRib = segment.offset(direction.rotateY());
                if (origin.distanceSq(rightRib) <= (double) (radius * radius)) {
                    worldIn.setBlockState(rightRib, Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, oppositeAxis));
                }
                if (origin.distanceSq(leftRib) <= (double) (radius * radius)) {
                    worldIn.setBlockState(leftRib, Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, oppositeAxis));
                }
                for (int ribHeight = 1; ribHeight < maxRibHeight + 2; ribHeight++) {
                    if (origin.distanceSq(rightRib.up(ribHeight).offset(direction.rotateYCCW())) <= (double) (radius * radius)) {
                        worldIn.setBlockState(rightRib.up(ribHeight).offset(direction.rotateYCCW()), Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, EnumFacing.Axis.Y));
                    }
                    if (origin.distanceSq(leftRib.up(ribHeight).offset(direction.rotateY())) <= (double) (radius * radius)) {
                        worldIn.setBlockState(leftRib.up(ribHeight).offset(direction.rotateY()), Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, EnumFacing.Axis.Y));
                    }
                }
                if (origin.distanceSq(rightRib.up(maxRibHeight + 2)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(rightRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, oppositeAxis));
                }
                if (origin.distanceSq(leftRib.up(maxRibHeight + 2)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(leftRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().withProperty(BlockBone.AXIS, oppositeAxis));
                }
            }

        }
    }
}
