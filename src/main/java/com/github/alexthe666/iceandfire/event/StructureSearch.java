package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import com.github.alexthe666.iceandfire.world.StructureLocatorData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public final class StructureSearch {
    private static final int SAVED_RADIUS_CHUNKS = 128;
    private static final int EXPLORATION_RADIUS_CHUNKS = 512;
    private static final int NEARBY_RADIUS_CHUNKS = 32;
    private static final int DISTANT_STEP_CHUNKS = 4;
    private static final int POPULATION_LIMIT = 256;
    private static final int POPULATIONS_PER_BAND = 16;
    private static final int CANDIDATES_PER_TICK = 128;
    private static final int SAVED_CANDIDATES_PER_TICK = 1024;
    private static final int CHUNK_READS_PER_TICK = 8;
    private static final Map<World, Search> SEARCHES = new HashMap<>();

    private StructureSearch() {
    }

    @Nullable
    public static LocatableStructure getPopulationTarget(World world, BlockPos pos) {
        Search search = SEARCHES.get(world);
        if (search == null || search.populationChunk == null) {
            return null;
        }
        ChunkPos chunk = search.populationChunk;
        return chunk.x == (pos.getX() >> 4) && chunk.z == (pos.getZ() >> 4) ? search.structure : null;
    }

    static List<BlockPos> getNearbySites(World world) {
        Search search = SEARCHES.get(world);
        return search == null ? Collections.emptyList() : search.nearbySites;
    }

    public static boolean start(EntityPlayerMP player, LocatableStructure structure, boolean undiscovered) {
        WorldServer world = player.getServerWorld();
        if (SEARCHES.containsKey(world)) {
            player.sendStatusMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.busy"), true);
            return false;
        }

        BlockPos origin = player.getPosition();
        Set<BlockPos> known = undiscovered ? StructureLocatorData.getKnown(world, structure) : null;
        if (!undiscovered) {
            BlockPos target = StructureLocatorData.findNearest(world, structure, origin);
            if (target != null && isExistingChunk(world, target)) {
                return teleport(player, target);
            }
        }
        boolean scanSaved = !undiscovered && LegacyStructureMarkers.supports(structure, world.provider.getDimension())
                && world.getChunkProvider().chunkLoader instanceof AnvilChunkLoader;
        boolean canExplore = WorldGenEvents.canExploreDimension(world, structure);
        if (!scanSaved && !canExplore) {
            player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.not_indexed"));
            return false;
        }

        SEARCHES.put(world, new Search(player.getUniqueID(), structure, origin, scanSaved, known));
        if (scanSaved) {
            player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.searching"));
        } else {
            notifyExploration(player);
        }
        return true;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) {
            return;
        }
        WorldServer world = (WorldServer) event.world;
        Search search = SEARCHES.get(world);
        if (search == null) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(search.player);
        if (player == null || player.isDead || !player.capabilities.isCreativeMode) {
            SEARCHES.remove(world);
            return;
        }

        BlockPos target = StructureLocatorData.findNearest(world, search.structure, search.origin, search.known);
        if (target == null || !isExistingChunk(world, target)) {
            if (search.scanningSaved) {
                search.scanSavedChunks(world);
                if (search.hasMoreSavedCandidates()) {
                    return;
                }
                target = search.nearest;
                if (target == null && WorldGenEvents.canExploreDimension(world, search.structure)) {
                    search.scanningSaved = false;
                    search.radius = 0;
                    search.offset = 0;
                    notifyExploration(player);
                    return;
                }
            } else if (WorldGenEvents.canExploreDimension(world, search.structure) && search.hasMoreExplorationCandidates()) {
                search.explore(world);
                return;
            } else {
                target = null;
            }
        }
        SEARCHES.remove(world);
        if (target == null) {
            player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.not_found"));
        } else {
            teleport(player, target);
        }
    }

    private static void notifyExploration(EntityPlayerMP player) {
        player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.exploring",
                EXPLORATION_RADIUS_CHUNKS * 16));
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() instanceof WorldServer) {
            Search search = SEARCHES.get(event.getWorld());
            if (search != null && search.populationChunk != null) {
                search.loadedChunks.add(event.getChunk());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof WorldServer) {
            SEARCHES.remove(event.getWorld());
        }
    }

    private static boolean teleport(EntityPlayerMP player, BlockPos target) {
        WorldServer world = player.getServerWorld();
        if (!isExistingChunk(world, target)) {
            return false;
        }
        BlockPos feet = findStandingPosition(world, target);
        if (feet == null) {
            feet = hasLandingClearance(world, target) ? target : world.getHeight(target);
            if (!hasLandingClearance(world, feet)) {
                player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.no_landing",
                        target.getX(), target.getY(), target.getZ()));
                return false;
            }
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }
        player.dismountRidingEntity();
        player.connection.setPlayerLocation(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D, player.rotationYaw, 0);
        player.fallDistance = 0;
        player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.arrived",
                target.getX(), target.getY(), target.getZ()));
        return true;
    }

    @Nullable
    private static BlockPos findStandingPosition(WorldServer world, BlockPos target) {
        BlockPos feet = findStandingPositionInColumn(world, target);
        if (feet != null) {
            return feet;
        }
        for (int radius = 1; radius <= 2; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.max(Math.abs(x), Math.abs(z)) == radius) {
                        feet = findStandingPositionInColumn(world, target.add(x, 0, z));
                        if (feet != null) {
                            return feet;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static BlockPos findStandingPositionInColumn(WorldServer world, BlockPos target) {
        if (!isExistingChunk(world, target)) {
            return null;
        }
        for (int distance = 0; distance <= 8; distance++) {
            BlockPos above = target.up(distance);
            if (canStandAt(world, above)) {
                return above;
            }
            BlockPos below = target.down(distance);
            if (distance > 0 && canStandAt(world, below)) {
                return below;
            }
        }
        return null;
    }

    private static boolean isExistingChunk(WorldServer world, BlockPos pos) {
        return world.getChunkProvider().isChunkGeneratedAt(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static boolean canStandAt(World world, BlockPos feet) {
        if (!hasLandingClearance(world, feet)) {
            return false;
        }
        BlockPos floor = feet.down();
        return world.getBlockState(floor).getBlock() != Blocks.MAGMA
                && world.isSideSolid(floor, EnumFacing.UP);
    }

    private static boolean hasLandingClearance(World world, BlockPos feet) {
        return feet.getY() > 0 && feet.getY() < world.getActualHeight() - 1
                && world.getWorldBorder().contains(feet)
                && world.isAirBlock(feet) && world.isAirBlock(feet.up());
    }

    private static final class Search {
        private final UUID player;
        @Nullable
        private final Set<BlockPos> known;
        private final LocatableStructure structure;
        private final BlockPos origin;
        private final List<Chunk> loadedChunks = new ArrayList<>();
        private final List<BlockPos> nearbySites = new ArrayList<>();
        private boolean scanningSaved;
        private int radius;
        private int offset;
        private int stride = 1;
        private int populations;
        private int populationBand;
        private int bandPopulations;
        private ChunkPos populationChunk;
        private BlockPos nearest;
        private double nearestDistance = Double.POSITIVE_INFINITY;

        private Search(UUID player, LocatableStructure structure, BlockPos origin, boolean scanningSaved,
                       @Nullable Set<BlockPos> known) {
            this.known = known;
            this.player = player;
            this.structure = structure;
            this.origin = origin;
            this.scanningSaved = scanningSaved;
        }

        private boolean hasMoreSavedCandidates() {
            // Every unvisited ring is at least this far from the origin along one axis.
            int minimumDistance = Math.max(0, radius * 16 - 15);
            return radius <= SAVED_RADIUS_CHUNKS && minimumDistance * minimumDistance <= nearestDistance;
        }

        private void scanSavedChunks(WorldServer world) {
            File saveDirectory = world.getChunkSaveLocation();
            int reads = 0;
            for (int checked = 0; checked < SAVED_CANDIDATES_PER_TICK && reads < CHUNK_READS_PER_TICK && hasMoreSavedCandidates(); checked++) {
                ChunkPos chunk = nextChunk();
                if (!LegacyStructureMarkers.isSaved(saveDirectory, chunk)) {
                    continue;
                }
                reads++;
                for (BlockPos marker : LegacyStructureMarkers.read(saveDirectory, chunk, structure)) {
                    double dx = marker.getX() - origin.getX();
                    double dz = marker.getZ() - origin.getZ();
                    double distance = dx * dx + dz * dz;
                    if (distance < nearestDistance && world.getWorldBorder().contains(marker)) {
                        nearest = marker;
                        nearestDistance = distance;
                    }
                }
            }
        }

        private boolean hasMoreExplorationCandidates() {
            return radius * stride <= EXPLORATION_RADIUS_CHUNKS && populations < POPULATION_LIMIT;
        }

        private void explore(WorldServer world) {
            ChunkProviderServer provider = world.getChunkProvider();
            int range = EXPLORATION_RADIUS_CHUNKS * 16;
            for (int checked = 0; checked < CANDIDATES_PER_TICK && hasMoreExplorationCandidates(); checked++) {
                if (stride == 1 && radius > NEARBY_RADIUS_CHUNKS) {
                    // Sample every fourth chunk farther out so a missing biome doesn't stall the server.
                    stride = DISTANT_STEP_CHUNKS;
                    radius = NEARBY_RADIUS_CHUNKS / stride + 1;
                }
                int band = radius * stride / NEARBY_RADIUS_CHUNKS;
                if (band != populationBand) {
                    populationBand = band;
                    bandPopulations = 0;
                }
                if (bandPopulations >= POPULATIONS_PER_BAND) {
                    radius = (band + 1) * NEARBY_RADIUS_CHUNKS / stride;
                    offset = 0;
                    continue;
                }
                ChunkPos chunk = nextChunk();
                BlockPos center = chunk.getBlock(8, 0, 8);
                double dx = center.getX() - origin.getX();
                double dz = center.getZ() - origin.getZ();
                if (dx * dx + dz * dz > range * range
                        || !world.getWorldBorder().contains(chunk.getBlock(0, 0, 0))
                        || !world.getWorldBorder().contains(chunk.getBlock(31, 0, 31))
                        || !WorldGenEvents.canExploreAt(world, structure, center)
                        || provider.isChunkGeneratedAt(chunk.x, chunk.z)) {
                    continue;
                }
                populate(world, provider, chunk);
                return;
            }
        }

        private void populate(WorldServer world, ChunkProviderServer provider, ChunkPos chunk) {
            populations++;
            bandPopulations++;
            boolean temple = structure == LocatableStructure.GORGON_TEMPLE;
            boolean roost = structure == LocatableStructure.FIRE_DRAGON_ROOST
                    || structure == LocatableStructure.ICE_DRAGON_ROOST
                    || structure == LocatableStructure.LIGHTNING_DRAGON_ROOST;
            if (temple || roost) {
                // Remember fresh centers before the first terrain probe can load their chunks.
                BlockPos center = chunk.getBlock(8, 0, 8);
                int range = EXPLORATION_RADIUS_CHUNKS * 16;
                int firstDistance = temple ? 16 : 24;
                for (int distance = firstDistance; distance <= 32; distance += 32 - firstDistance) {
                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        BlockPos site = center.offset(facing, distance);
                        if (origin.distanceSq(site.getX(), origin.getY(), site.getZ()) <= range * range
                                && world.getWorldBorder().contains(site)
                                && !provider.isChunkGeneratedAt(site.getX() >> 4, site.getZ() >> 4)) {
                            nearbySites.add(site);
                        }
                    }
                }
            }
            populationChunk = chunk;
            try {
                // Forge populates the northwest chunk once all four chunks are loaded.
                provider.provideChunk(chunk.x, chunk.z);
                provider.provideChunk(chunk.x + 1, chunk.z);
                provider.provideChunk(chunk.x, chunk.z + 1);
                provider.provideChunk(chunk.x + 1, chunk.z + 1);
            } finally {
                populationChunk = null;
                nearbySites.clear();
                for (Chunk loaded : loadedChunks) {
                    if (world.getPlayerChunkMap().getEntry(loaded.x, loaded.z) == null) {
                        provider.queueUnload(loaded);
                    }
                }
                loadedChunks.clear();
            }
        }

        private ChunkPos nextChunk() {
            int x = origin.getX() >> 4;
            int z = origin.getZ() >> 4;
            if (radius == 0) {
                radius = 1;
                return new ChunkPos(x, z);
            }

            int width = radius * 2;
            int side = offset / width;
            int step = offset % width;
            ChunkPos result;
            switch (side) {
                case 0: result = new ChunkPos(x + (step - radius) * stride, z - radius * stride); break;
                case 1: result = new ChunkPos(x + radius * stride, z + (step - radius) * stride); break;
                case 2: result = new ChunkPos(x + (radius - step) * stride, z + radius * stride); break;
                default: result = new ChunkPos(x - radius * stride, z + (radius - step) * stride); break;
            }
            if (++offset == width * 4) {
                offset = 0;
                radius++;
            }
            return result;
        }
    }
}
