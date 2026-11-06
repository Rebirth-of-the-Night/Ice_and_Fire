package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.world.BiomeSpawnFilter;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import com.github.alexthe666.iceandfire.world.StructureLocatorData;
import com.github.alexthe666.iceandfire.world.gen.*;
import com.github.alexthe666.iceandfire.world.gen.processor.GraveyardProcessor;
import com.github.alexthe666.iceandfire.world.village.MapGenPixieVillage;
import com.github.alexthe666.iceandfire.world.village.MapGenSnowVillage;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.*;

public class WorldGenEvents implements IWorldGenerator {

    public static final MapGenSnowVillage SNOW_VILLAGE = new MapGenSnowVillage();
    public static final MapGenPixieVillage PIXIE_VILLAGE = new MapGenPixieVillage();
    public static final WorldGenMyrmexHive JUNGLE_MYRMEX_HIVE = new WorldGenMyrmexHive(false, true);
    public static final WorldGenMyrmexHive DESERT_MYRMEX_HIVE = new WorldGenMyrmexHive(false, false);
    private static final WorldGenFireDragonCave FIRE_DRAGON_CAVE = new WorldGenFireDragonCave();
    private static final WorldGenFireDragonRoosts FIRE_DRAGON_ROOST = new WorldGenFireDragonRoosts();
    private static final WorldGenIceDragonCave ICE_DRAGON_CAVE = new WorldGenIceDragonCave();
    private static final WorldGenIceDragonRoosts ICE_DRAGON_ROOST = new WorldGenIceDragonRoosts();
    private static final WorldGenLightningDragonCave LIGHTNING_DRAGON_CAVE = new WorldGenLightningDragonCave();
    private static final WorldGenLightningDragonRoosts LIGHTNING_DRAGON_ROOST = new WorldGenLightningDragonRoosts();
    private static final WorldGenCyclopsCave CYCLOPS_CAVE = new WorldGenCyclopsCave();
    private static final WorldGenSirenIsland SIREN_ISLAND = new WorldGenSirenIsland();
    private static final WorldGenHydraCave HYDRA_CAVE = new WorldGenHydraCave();
    private static final WorldGenGorgonTemple GORGON_TEMPLE = new WorldGenGorgonTemple();
    private static final ResourceLocation GRAVEYARD = new ResourceLocation(IceAndFire.MODID, "graveyard");

    private BlockPos lastSnowVillage = null;
    private BlockPos lastPixieVillage = null;
    private BlockPos lastMyrmexHive = null;
    private BlockPos lastDragonRoost = null;
    private BlockPos lastDragonCave = null;
    private BlockPos lastCyclopsCave = null;
    private BlockPos lastSirenIsland = null;
    private BlockPos lastMausoleum = null;
    private BlockPos lastHydraCave = null;

    public static BlockPos getHeight(World world, BlockPos pos) {
        // World#getHeight reports zero for unloaded chunks, even when their terrain exists.
        int height = world.getChunk(pos).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
        return new BlockPos(pos.getX(), height, pos.getZ());
    }

    private static boolean canHeightSkipBlock(BlockPos pos, World world) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLiquid;
    }

    public static BlockPos degradeSurface(World world, BlockPos surface) {
        while ((!world.getBlockState(surface).isOpaqueCube() || canHeightSkipBlock(surface, world)) && surface.getY() > 1) {
            surface = surface.down();
        }
        return surface;
    }

    /** The graveyard needs a dry, nearly level site; on slopes it floats on one side and is buried on the other. */
    private static boolean isGraveyardSiteFlat(World world, BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int maxX = Math.max(a.getX(), b.getX());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxZ = Math.max(a.getZ(), b.getZ());
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int x = minX; x <= maxX; x += 2) {
            for (int z = minZ; z <= maxZ; z += 2) {
                if (!sampleGraveyardColumn(world, x, z, a.getY() + 4)) {
                    return false;
                }
                int y = degradeSurface(world, getHeight(world, new BlockPos(x, 0, z))).getY();
                low = Math.min(low, y);
                high = Math.max(high, y);
            }
        }
        return high - low <= 2;
    }

    private static boolean sampleGraveyardColumn(World world, int x, int z, int surfaceY) {
        BlockPos top = getHeight(world, new BlockPos(x, 0, z));
        if (world.getBlockState(top).getMaterial().isLiquid() || world.getBlockState(top.down()).getMaterial().isLiquid()) {
            return false;
        }
        return Math.abs(degradeSurface(world, top).getY() + 1 - surfaceY) <= 2;
    }

    /** Extends the dirt base of the template down to solid ground so no side is left hanging. */
    private static void fillBelowGraveyard(World world, BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int maxX = Math.max(a.getX(), b.getX());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxZ = Math.max(a.getZ(), b.getZ());
        IBlockState dirt = Blocks.DIRT.getDefaultState();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos pos = new BlockPos(x, a.getY() - 1, z);
                while (pos.getY() > 1 && !world.getBlockState(pos).isOpaqueCube()) {
                    world.setBlockState(pos, dirt, 2);
                    pos = pos.down();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.getWorldType() == WorldType.FLAT && !IceAndFire.CONFIG.spawnStructuresOnSuperflat) {
            return;
        }

        if (isDimensionBlacklisted(world.provider.getDimension(), false))
            return;

        boolean prevLogCascadingWorldGen = net.minecraftforge.common.ForgeModContainer.logCascadingWorldGeneration;
        double spawnCheck = IceAndFire.CONFIG.worldGenDistance * IceAndFire.CONFIG.worldGenDistance;
        if (!IceAndFire.CONFIG.logCascadingWorldGen) {
            net.minecraftforge.common.ForgeModContainer.logCascadingWorldGeneration = false;
        }
        int x = (chunkX * 16) + 8;
        int z = (chunkZ * 16) + 8;
        BlockPos height = getHeight(world, new BlockPos(x, 0, z));
        LocatableStructure searchTarget = StructureSearch.getPopulationTarget(world, height);
        Biome biome = world.getBiome(height);
        Set<Type> types = BiomeDictionary.getTypes(biome);

        boolean coldAndSnowy = types.contains(Type.COLD) && types.contains(Type.SNOWY);
        boolean lightningBiome = types.contains(Type.JUNGLE) || types.contains(Type.MESA) || types.contains(Type.SAVANNA);

        if (IceAndFire.CONFIG.spawnGorgons
                && (random.nextInt(IceAndFire.CONFIG.spawnGorgonsChance) == 0 || searchTarget == LocatableStructure.GORGON_TEMPLE)
                && LocatableStructure.GORGON_TEMPLE.allows(biome, types)) {
            BlockPos temple = GORGON_TEMPLE.generate(world, random, height) ? height : null;
            if (temple == null && searchTarget == LocatableStructure.GORGON_TEMPLE) {
                for (BlockPos column : StructureSearch.getNearbySites(world)) {
                    BlockPos site = getHeight(world, column);
                    if (!world.getBlockState(site.down()).getMaterial().isLiquid()
                            && BiomeSpawnFilter.GORGON_TEMPLE.allowsConfigured(world, site)
                            && GORGON_TEMPLE.generate(world, random, site)) {
                        temple = site;
                        break;
                    }
                }
            }
            if (temple != null) {
                StructureLocatorData.record(world, LocatableStructure.GORGON_TEMPLE, temple);
            }
        }
        if (IceAndFire.CONFIG.generateGraveyards
                && (random.nextInt(IceAndFire.CONFIG.generateGraveyardChance) == 0 || searchTarget == LocatableStructure.GRAVEYARD)
                && LocatableStructure.GRAVEYARD.allows(biome, types)
                && world.getBlockState(height.down()).isFullBlock() && world.isAirBlock(height.up())) {
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
            MinecraftServer server = world.getMinecraftServer();
            TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
            PlacementSettings settings = new PlacementSettings().setRotation(rotation).setMirror(mirror);
            Template template = templateManager.getTemplate(server, GRAVEYARD);
            // Centre the rotated/mirrored footprint on the chosen column instead of anchoring one corner there.
            BlockPos extent = Template.transformedBlockPos(settings, template.getSize().add(-1, 0, -1));
            BlockPos center = height.add(-extent.getX() / 2, -4, -extent.getZ() / 2);
            BlockPos opposite = center.add(extent);
            if (isGraveyardSiteFlat(world, center, opposite) && !VillageAvoidance.intersects(world, center, opposite)) {
                template.addBlocksToWorld(world, center, new GraveyardProcessor(), settings, 2);
                fillBelowGraveyard(world, center, opposite);
                StructureLocatorData.record(world, LocatableStructure.GRAVEYARD, center);
            }
        }
        if (IceAndFire.CONFIG.generateSirenIslands && isFarEnoughFromSpawn(world, height) && LocatableStructure.SIREN_ISLAND.allows(biome, types)
                && (random.nextInt(IceAndFire.CONFIG.generateSirenChance + 1) == 0 || searchTarget == LocatableStructure.SIREN_ISLAND)
                && (lastSirenIsland == null || lastSirenIsland.distanceSq(height) >= spawnCheck)) {
            SIREN_ISLAND.generate(world, random, height);
            StructureLocatorData.record(world, LocatableStructure.SIREN_ISLAND, height);
            lastSirenIsland = height;
        }

        if (IceAndFire.CONFIG.generateCyclopsCaves && isFarEnoughFromSpawn(world, height) && LocatableStructure.CYCLOPS_CAVE.allows(biome, types)
                && (random.nextInt(IceAndFire.CONFIG.spawnCyclopsCaveChance + 1) == 0 || searchTarget == LocatableStructure.CYCLOPS_CAVE)
                && world.getBlockState(height.down()).isOpaqueCube() && (lastCyclopsCave == null || lastCyclopsCave.distanceSq(height) >= spawnCheck)) {
            if (CYCLOPS_CAVE.generate(world, random, height)) {
                StructureLocatorData.record(world, LocatableStructure.CYCLOPS_CAVE, height);
                lastCyclopsCave = height;
            }
        }
        if (IceAndFire.CONFIG.generateWanderingCyclops && isFarEnoughFromSpawn(world, height) && BiomeSpawnFilter.WANDERING_CYCLOPS.allows(biome, types.contains(Type.PLAINS)) && (lastCyclopsCave == null || lastCyclopsCave.distanceSq(height) >= spawnCheck)) {
            if (random.nextInt(IceAndFire.CONFIG.spawnWanderingCyclopsChance + 1) == 0) {
                EntityCyclops cyclops = new EntityCyclops(world);
                cyclops.setPosition(x, height.getY() + 1, z);
                cyclops.setVariant(random.nextInt(3));
                if (!world.isRemote) {
                    world.spawnEntity(cyclops);
                }
                for (int i = 0; i < 3 + random.nextInt(3); i++) {
                    EntitySheep sheep = new EntitySheep(world);
                    sheep.setPosition(x, height.getY() + 1, z);
                    sheep.setFleeceColor(EntitySheep.getRandomSheepColor(random));
                    if (!world.isRemote) {
                        world.spawnEntity(sheep);
                    }
                }
                lastCyclopsCave = height;
            }
        }
        if (IceAndFire.CONFIG.spawnPixies && isFarEnoughFromSpawn(world, height) && (lastPixieVillage == null || lastPixieVillage.distanceSq(height) >= spawnCheck)) {
            if (LocatableStructure.PIXIE_VILLAGE.allows(biome, types)
                    && (random.nextInt(IceAndFire.CONFIG.spawnPixiesChance + 1) == 0 || searchTarget == LocatableStructure.PIXIE_VILLAGE)) {
                PIXIE_VILLAGE.generate(world, random, height);
                StructureLocatorData.record(world, LocatableStructure.PIXIE_VILLAGE, height);
                lastPixieVillage = height;
            }
        }
        if (IceAndFire.CONFIG.generateDragonRoosts && isFarEnoughFromSpawn(world, height) && !isDimensionBlacklisted(world.provider.getDimension(), true) && (lastDragonRoost == null || lastDragonRoost.distanceSq(height) >= spawnCheck)) {
            boolean isHills = types.contains(Type.HILLS) || types.contains(Type.MOUNTAIN) && !types.contains(Type.SNOWY);
            if (LocatableStructure.FIRE_DRAGON_ROOST.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonRoostChance : IceAndFire.CONFIG.generateDragonRoostChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.FIRE_DRAGON_ROOST) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    surface = degradeSurface(world, surface);
                    if (FIRE_DRAGON_ROOST.generate(world, random, surface,
                        candidate -> BiomeSpawnFilter.FIRE_DRAGON_ROOST.allowsConfigured(world, candidate)
                            && isFarEnoughFromSpawn(world, candidate)
                            && (lastDragonRoost == null || lastDragonRoost.distanceSq(candidate) >= spawnCheck),
                        searchTarget == LocatableStructure.FIRE_DRAGON_ROOST ? StructureSearch.getNearbySites(world) : Collections.emptyList())) {
                        lastDragonRoost = FIRE_DRAGON_ROOST.getLastGeneratedPosition();
                        StructureLocatorData.record(world, LocatableStructure.FIRE_DRAGON_ROOST, lastDragonRoost);
                    }
                }
            }
            if (LocatableStructure.ICE_DRAGON_ROOST.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonRoostChance : IceAndFire.CONFIG.generateDragonRoostChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.ICE_DRAGON_ROOST) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    surface = degradeSurface(world, surface);
                    if (ICE_DRAGON_ROOST.generate(world, random, surface,
                        candidate -> BiomeSpawnFilter.ICE_DRAGON_ROOST.allowsConfigured(world, candidate)
                            && isFarEnoughFromSpawn(world, candidate)
                            && (lastDragonRoost == null || lastDragonRoost.distanceSq(candidate) >= spawnCheck),
                        searchTarget == LocatableStructure.ICE_DRAGON_ROOST ? StructureSearch.getNearbySites(world) : Collections.emptyList())) {
                        lastDragonRoost = ICE_DRAGON_ROOST.getLastGeneratedPosition();
                        StructureLocatorData.record(world, LocatableStructure.ICE_DRAGON_ROOST, lastDragonRoost);
                    }
                }
            }
            if (LocatableStructure.LIGHTNING_DRAGON_ROOST.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonRoostChance : IceAndFire.CONFIG.generateDragonRoostChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.LIGHTNING_DRAGON_ROOST) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    surface = degradeSurface(world, surface);
                    if (LIGHTNING_DRAGON_ROOST.generate(world, random, surface,
                        candidate -> BiomeSpawnFilter.LIGHTNING_DRAGON_ROOST.allowsConfigured(world, candidate)
                            && isFarEnoughFromSpawn(world, candidate)
                            && (lastDragonRoost == null || lastDragonRoost.distanceSq(candidate) >= spawnCheck),
                        searchTarget == LocatableStructure.LIGHTNING_DRAGON_ROOST ? StructureSearch.getNearbySites(world) : Collections.emptyList())) {
                        lastDragonRoost = LIGHTNING_DRAGON_ROOST.getLastGeneratedPosition();
                        StructureLocatorData.record(world, LocatableStructure.LIGHTNING_DRAGON_ROOST, lastDragonRoost);
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateDragonSkeletons && !isDimensionBlacklisted(world.provider.getDimension(), true)) {
            if (BiomeSpawnFilter.FIRE_DRAGON_SKELETON.allows(biome, types.contains(Type.DRY) && types.contains(Type.SANDY)) && random.nextInt(IceAndFire.CONFIG.generateDragonSkeletonChance + 1) == 0) {
                EntityFireDragon firedragon = new EntityFireDragon(world);
                firedragon.setPosition(x, height.getY() + 1, z);
                int dragonage = 10 + random.nextInt(100);
                firedragon.setGender(random.nextBoolean());
                firedragon.growDragon(dragonage);
                firedragon.modelDeadProgress = 20;
                firedragon.setModelDead(true);
                firedragon.setDeathStage((dragonage / 5) / 2);
                firedragon.rotationYaw = random.nextInt(360);
                if (!world.isRemote) {
                    world.spawnEntity(firedragon);
                }
            }
            if (BiomeSpawnFilter.ICE_DRAGON_SKELETON.allows(biome, coldAndSnowy) && random.nextInt(IceAndFire.CONFIG.generateDragonSkeletonChance + 1) == 0) {
                EntityIceDragon icedragon = new EntityIceDragon(world);
                icedragon.setPosition(x, height.getY() + 1, z);
                int dragonage = 10 + random.nextInt(100);
                icedragon.setGender(random.nextBoolean());
                icedragon.growDragon(dragonage);
                icedragon.modelDeadProgress = 20;
                icedragon.setModelDead(true);
                icedragon.setDeathStage((dragonage / 5) / 2);
                icedragon.rotationYaw = random.nextInt(360);
                if (!world.isRemote) {
                    world.spawnEntity(icedragon);
                }
            }
            if (BiomeSpawnFilter.LIGHTNING_DRAGON_SKELETON.allows(biome, lightningBiome)) {
                if (random.nextInt(IceAndFire.CONFIG.generateDragonSkeletonChance + 1) == 0) {
                    EntityLightningDragon lightningdragon = new EntityLightningDragon(world);
                    lightningdragon.setPosition(x, height.getY() + 1, z);
                    int dragonage = 10 + random.nextInt(100);
                    lightningdragon.setGender(random.nextBoolean());
                    lightningdragon.growDragon(dragonage);
                    lightningdragon.modelDeadProgress = 20;
                    lightningdragon.setModelDead(true);
                    lightningdragon.setDeathStage((dragonage / 5) / 2);
                    lightningdragon.rotationYaw = random.nextInt(360);
                    if (!world.isRemote) {
                        world.spawnEntity(lightningdragon);
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.spawnHippocampus && BiomeSpawnFilter.HIPPOCAMPUS.allows(biome, types.contains(Type.OCEAN)) && random.nextInt(IceAndFire.CONFIG.hippocampusSpawnChance + 1) == 0) {
            for (int i = 0; i < random.nextInt(5); i++) {
                BlockPos pos = new BlockPos(x + random.nextInt(10) - 5, 20 + random.nextInt(40), z + random.nextInt(10) - 5);
                if (world.getBlockState(pos).getMaterial() == Material.WATER && BiomeSpawnFilter.HIPPOCAMPUS.allowsConfigured(world, pos)) {
                    EntityHippocampus campus = new EntityHippocampus(world);
                    campus.setVariant(random.nextInt(5));
                    campus.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0);
                    if (campus.isNotColliding()) {
                        world.spawnEntity(campus);
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.spawnSeaSerpents && BiomeSpawnFilter.SEA_SERPENT.allows(biome, types.contains(Type.OCEAN)) && random.nextInt(IceAndFire.CONFIG.seaSerpentSpawnChance + 1) == 0) {
            BlockPos pos = new BlockPos(x + random.nextInt(10) - 5, 20 + random.nextInt(40), z + random.nextInt(10) - 5);
            if (world.getBlockState(pos).getMaterial() == Material.WATER && BiomeSpawnFilter.SEA_SERPENT.allowsConfigured(world, pos)) {
                EntitySeaSerpent serpent = new EntitySeaSerpent(world);
                serpent.onWorldSpawn(random);
                serpent.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0);
                world.spawnEntity(serpent);
            }
        }
        if (IceAndFire.CONFIG.spawnStymphalianBirds && isFarEnoughFromSpawn(world, height) && BiomeSpawnFilter.STYMPHALIAN_BIRD.allows(biome, types.contains(Type.SWAMP)) && random.nextInt(IceAndFire.CONFIG.stymphalianBirdSpawnChance + 1) == 0) {
            for (int i = 0; i < 4 + random.nextInt(4); i++) {
                BlockPos pos = height.add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
                if (world.getBlockState(pos.down()).isOpaqueCube() && BiomeSpawnFilter.STYMPHALIAN_BIRD.allowsConfigured(world, pos)) {
                    EntityStymphalianBird bird = new EntityStymphalianBird(world);
                    bird.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0);
                    if (bird.isNotColliding()) {
                        world.spawnEntity(bird);
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateDragonDens && isFarEnoughFromSpawn(world, height) && !isDimensionBlacklisted(world.provider.getDimension(), true) && (lastDragonCave == null || lastDragonCave.distanceSq(height) >= spawnCheck)) {
            boolean isHills = types.contains(Type.HILLS) || types.contains(Type.MOUNTAIN);
            if (LocatableStructure.FIRE_DRAGON_DEN.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonDenChance : IceAndFire.CONFIG.generateDragonDenChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.FIRE_DRAGON_DEN) {
                    int newY = 20 + random.nextInt(20);
                    BlockPos pos = new BlockPos(x, newY, z);
                    if (!world.canBlockSeeSky(pos)) {
                        FIRE_DRAGON_CAVE.generate(world, random, pos);
                        StructureLocatorData.record(world, LocatableStructure.FIRE_DRAGON_DEN, pos);
                        lastDragonCave = pos;
                    }
                }
            }
            if (LocatableStructure.ICE_DRAGON_DEN.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonDenChance : IceAndFire.CONFIG.generateDragonDenChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.ICE_DRAGON_DEN) {
                    int newY = 20 + random.nextInt(20);
                    BlockPos pos = new BlockPos(x, newY, z);
                    ICE_DRAGON_CAVE.generate(world, random, pos);
                    StructureLocatorData.record(world, LocatableStructure.ICE_DRAGON_DEN, pos);
                    lastDragonCave = pos;
                }
            }
            if (LocatableStructure.LIGHTNING_DRAGON_DEN.allows(biome, types)) {
                if (random.nextInt((isHills ? IceAndFire.CONFIG.generateDragonDenChance : IceAndFire.CONFIG.generateDragonDenChance * 2) + 1) == 0
                        || searchTarget == LocatableStructure.LIGHTNING_DRAGON_DEN) {
                    int newY = 20 + random.nextInt(20);
                    BlockPos pos = new BlockPos(x, newY, z);
                    if (!world.canBlockSeeSky(pos)) {
                        LIGHTNING_DRAGON_CAVE.generate(world, random, pos);
                        StructureLocatorData.record(world, LocatableStructure.LIGHTNING_DRAGON_DEN, pos);
                        lastDragonCave = pos;
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateSilverOre) {
            for (int silverAmount = 0; silverAmount < 2; silverAmount++) {
                int oreHeight = random.nextInt(32);
                int xOre = (chunkX * 16) + random.nextInt(16);
                int zOre = (chunkZ * 16) + random.nextInt(16);
                new WorldGenMinable(IafBlockRegistry.silverOre.getDefaultState(), 4 + random.nextInt(4)).generate(world, random, new BlockPos(xOre, oreHeight, zOre));
            }
        }
        if (IceAndFire.CONFIG.generateCopperOre) {
            for (int copperAmount = 0; copperAmount < 5; copperAmount++) {
                int oreHeight = random.nextInt(128);
                int xOre = (chunkX * 16) + random.nextInt(16);
                int zOre = (chunkZ * 16) + random.nextInt(16);
                new WorldGenMinable(IafBlockRegistry.copperOre.getDefaultState(), 1 + random.nextInt(4)).generate(world, random, new BlockPos(xOre, oreHeight, zOre));
            }
        }
        if (IceAndFire.CONFIG.generateSapphireOre) {
            if (types.contains(Type.SNOWY)) {
                int count = 3 + random.nextInt(6);
                for (int sapphireAmount = 0; sapphireAmount < count; sapphireAmount++) {
                    int oreHeight = random.nextInt(28) + 4;
                    int xOre = (chunkX * 16) + random.nextInt(16);
                    int zOre = (chunkZ * 16) + random.nextInt(16);
                    BlockPos pos = new BlockPos(xOre, oreHeight, zOre);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock().isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE))) {
                        world.setBlockState(pos, IafBlockRegistry.sapphireOre.getDefaultState());
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateAmethystOre) {
            if (types.contains(Type.SAVANNA)) {
                int count = 3 + random.nextInt(6);
                for (int sapphireAmount = 0; sapphireAmount < count; sapphireAmount++) {
                    int oreHeight = random.nextInt(28) + 4;
                    int xOre = (chunkX * 16) + random.nextInt(16);
                    int zOre = (chunkZ * 16) + random.nextInt(16);
                    BlockPos pos = new BlockPos(xOre, oreHeight, zOre);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock().isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE))) {
                        world.setBlockState(pos, IafBlockRegistry.amethystOre.getDefaultState());
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateSnowVillages && LocatableStructure.SNOW_VILLAGE.allows(biome, types) && (lastSnowVillage == null || lastSnowVillage.distanceSq(height) >= spawnCheck)) {
            SNOW_VILLAGE.generate(world, random, height);
            lastSnowVillage = height;
        }
        boolean myrmexRoll = IceAndFire.CONFIG.generateMyrmexColonies && random.nextInt(IceAndFire.CONFIG.myrmexColonyGenChance) == 0;
        boolean searchingForHive = searchTarget == LocatableStructure.JUNGLE_MYRMEX_HIVE
                || searchTarget == LocatableStructure.DESERT_MYRMEX_HIVE;
        if (IceAndFire.CONFIG.generateMyrmexColonies && (myrmexRoll || searchingForHive)
                && isFarEnoughFromSpawn(world, height) && MyrmexWorldData.get(world).getNearestHive(height, 500) == null) {
            boolean jungle = LocatableStructure.JUNGLE_MYRMEX_HIVE.allows(biome, types);
            boolean desert = LocatableStructure.DESERT_MYRMEX_HIVE.allows(biome, types);
            LocatableStructure hiveType = jungle ? LocatableStructure.JUNGLE_MYRMEX_HIVE : LocatableStructure.DESERT_MYRMEX_HIVE;
            if ((jungle || desert) && (myrmexRoll || searchTarget == hiveType)
                    && (lastMyrmexHive == null || lastMyrmexHive.distanceSq(height) >= spawnCheck)) {
                BlockPos lowestHeight = new BlockPos(height.getX(), world.getChunksLowestHorizon(height.getX(), height.getZ()), height.getZ());
                int down = Math.max(15, lowestHeight.getY() - 20 + random.nextInt(10));
                WorldGenMyrmexHive hiveGenerator = jungle ? JUNGLE_MYRMEX_HIVE : DESERT_MYRMEX_HIVE;
                BlockPos center = new BlockPos(lowestHeight.getX(), down, lowestHeight.getZ());
                hiveGenerator.generate(world, random, center);
                StructureLocatorData.record(world, hiveType, center);
                lastMyrmexHive = height;
            }
        }
        if (!isDimensionBlacklisted(world.provider.getDimension(), true)) {
            if (types.contains(Type.COLD) && types.contains(Type.SNOWY)) {
                if (random.nextInt(15) == 0) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    if (IafBlockRegistry.frost_lily.canPlaceBlockAt(world, surface)) {
                        world.setBlockState(surface, IafBlockRegistry.frost_lily.getDefaultState());
                    }
                }
            }
            if (types.contains(Type.HOT) && (types.contains(Type.SANDY))) {
                if (random.nextInt(15) == 0) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    if (IafBlockRegistry.fire_lily.canPlaceBlockAt(world, surface)) {
                        world.setBlockState(surface, IafBlockRegistry.fire_lily.getDefaultState());
                    }
                }
            }
            if (types.contains(Type.NETHER)) {
                if (random.nextInt(15) == 0) {
                    BlockPos surface = getNetherHeight(world, new BlockPos(x, 0, z));
                    if (surface != null) {
                        world.setBlockState(surface.up(), IafBlockRegistry.fire_lily.getDefaultState());
                    }
                }
            }
            if (types.contains(Type.JUNGLE) || types.contains(Type.MESA) || types.contains(Type.SAVANNA)) {
                if (random.nextInt(15) == 0) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    if (IafBlockRegistry.lightning_lily.canPlaceBlockAt(world, surface)) {
                        world.setBlockState(surface, IafBlockRegistry.lightning_lily.getDefaultState());
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateMausoleums && (lastMausoleum == null || lastMausoleum.distanceSq(height) >= spawnCheck)) {
            if (LocatableStructure.MAUSOLEUM.allows(biome, types)) {
                if (random.nextInt(IceAndFire.CONFIG.generateMausoleumChance + 1) == 0 || searchTarget == LocatableStructure.MAUSOLEUM) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    surface = degradeSurface(world, surface);
                    if (new WorldGenMausoleum(EnumFacing.byHorizontalIndex(random.nextInt(4))).generate(world, random, surface)) {
                        lastMausoleum = surface;
                        StructureLocatorData.record(world, LocatableStructure.MAUSOLEUM, surface);
                    }
                }
            }
        }
        if (IceAndFire.CONFIG.generateHydraCaves && isFarEnoughFromSpawn(world, height) && LocatableStructure.HYDRA_CAVE.allows(biome, types)
                && (random.nextInt(IceAndFire.CONFIG.generateHydraChance + 1) == 0 || searchTarget == LocatableStructure.HYDRA_CAVE)
                && world.getBlockState(height.down()).isOpaqueCube() && (lastHydraCave == null || lastHydraCave.distanceSq(height) >= spawnCheck)) {
            if (HYDRA_CAVE.generate(world, random, height)) {
                StructureLocatorData.record(world, LocatableStructure.HYDRA_CAVE, height);
            }
            lastHydraCave = height;
        }
        if (!IceAndFire.CONFIG.logCascadingWorldGen) {
            net.minecraftforge.common.ForgeModContainer.logCascadingWorldGeneration = prevLogCascadingWorldGen;
        }
    }

    static boolean canExploreDimension(World world, LocatableStructure structure) {
        if (!structure.isEnabled()) {
            return false;
        }
        int dimension = world.provider.getDimension();
        if (structure == LocatableStructure.DREAD_RUIN) {
            return dimension == IceAndFire.CONFIG.dreadlandsDimensionId;
        }
        if (isDimensionBlacklisted(dimension, false)
                || world.getWorldType() == WorldType.FLAT && !IceAndFire.CONFIG.spawnStructuresOnSuperflat) {
            return false;
        }
        switch (structure) {
            case FIRE_DRAGON_ROOST:
            case ICE_DRAGON_ROOST:
            case LIGHTNING_DRAGON_ROOST:
            case FIRE_DRAGON_DEN:
            case ICE_DRAGON_DEN:
            case LIGHTNING_DRAGON_DEN:
                return !isDimensionBlacklisted(dimension, true);
            default:
                return true;
        }
    }

    static boolean canExploreAt(World world, LocatableStructure structure, BlockPos center) {
        switch (structure) {
            case GORGON_TEMPLE:
            case GRAVEYARD:
            case SNOW_VILLAGE:
            case MAUSOLEUM:
            case DREAD_RUIN:
                break;
            default:
                if (!isFarEnoughFromSpawn(world, center)) {
                    return false;
                }
        }
        Biome biome = world.getBiomeProvider().getBiome(center);
        Set<Type> types = BiomeDictionary.getTypes(biome);
        return structure.allows(biome, types)
                && (structure != LocatableStructure.DESERT_MYRMEX_HIVE || !LocatableStructure.JUNGLE_MYRMEX_HIVE.allows(biome, types));
    }

    private static boolean isDimensionBlacklisted(int id, boolean dragons) {
        if (id == IceAndFire.CONFIG.dreadlandsDimensionId) {
            return true;
        }
        boolean useBlackOrWhiteLists = IceAndFire.CONFIG.useDimensionBlackList;
        int[] blacklistedArray = dragons ? IceAndFire.CONFIG.dragonBlacklistedDimensions : IceAndFire.CONFIG.structureBlacklistedDimensions;
        int[] whitelistedArray = dragons ? IceAndFire.CONFIG.dragonWhitelistedDimensions : IceAndFire.CONFIG.structureWhitelistedDimensions;
        int[] array = useBlackOrWhiteLists ? blacklistedArray : whitelistedArray;
        List<Integer> dimList = new ArrayList<>();
        for (int dimension : array) {
            dimList.add(dimension);
        }

        if (dimList.contains(id)) {
            return useBlackOrWhiteLists;
        }

        return !useBlackOrWhiteLists;
    }

    private BlockPos getNetherHeight(World world, BlockPos pos) {
        for (int i = 0; i < 255; i++) {
            BlockPos ground = pos.up(i);
            if (world.getBlockState(ground).getBlock() == Blocks.NETHERRACK && world.isAirBlock(ground.up())) {
                return ground;
            }
        }
        return null;
    }

    private static boolean isFarEnoughFromSpawn(World world, BlockPos pos) {
        BlockPos spawnRelative = new BlockPos(world.getSpawnPoint().getX(), pos.getY(), world.getSpawnPoint().getZ());
        boolean spawnCheck = spawnRelative.distanceSq(pos) > IceAndFire.CONFIG.dangerousWorldGenDistanceLimit * IceAndFire.CONFIG.dangerousWorldGenDistanceLimit;
        return spawnCheck;
    }
}
