package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.dimension.WorldProviderDreadLands;
import com.github.alexthe666.iceandfire.world.gen.WorldGenCastle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class WorldGenDreadDimension implements IWorldGenerator {
    // Cache for generated castles to prevent duplicate generation
    private static final ConcurrentHashMap<Long, Boolean> GENERATED_CASTLES = new ConcurrentHashMap<>();

    // Constants for castle positioning and generation
    private static final BlockPos CASTLE_ORIGIN = new BlockPos(-63, 0, -165);
    private static final BlockPos CASTLE_GENERATION_POS = new BlockPos(-126, 0, -330);
    private static final int MIN_GENERATION_HEIGHT = 40;
    private static final int MAX_GENERATION_HEIGHT = 120;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Early exit if not in DreadLands dimension
        if (!(world.provider instanceof WorldProviderDreadLands)) {
            return;
        }

        ChunkPos castleChunkPos = new ChunkPos(CASTLE_ORIGIN);
        ChunkPos currentChunkPos = new ChunkPos(chunkX, chunkZ);

        // Check if this is the target chunk for castle generation
        if (!castleChunkPos.equals(currentChunkPos)) {
            return;
        }

        // Generate castle only once per position using chunk coordinates as key
        long posKey = ChunkPos.asLong(chunkX, chunkZ);
        if (GENERATED_CASTLES.putIfAbsent(posKey, true) != null) {
            return;
        }

        try {
            generateCastle(world, random);
        } catch (Exception e) {
            GENERATED_CASTLES.remove(posKey);
            IceAndFire.logger.error("Failed to generate castle at " + posKey, e);
        }
    }

    private void generateCastle(World world, Random random) {
        // Calculate appropriate height for castle generation
        int groundHeight = world.getHeight(CASTLE_ORIGIN.getX(), CASTLE_ORIGIN.getZ());
        int generationHeight = Math.min(Math.max(groundHeight, MIN_GENERATION_HEIGHT),
                MAX_GENERATION_HEIGHT);

        // Create generation position with calculated height
        BlockPos generationPos = new BlockPos(
                CASTLE_GENERATION_POS.getX(),
                generationHeight,
                CASTLE_GENERATION_POS.getZ()
        );

        // Generate the castle structure
        new WorldGenCastle().generate(world, random, generationPos);
    }

    /**
     * Clears the generation cache - useful for world reloads or dimension resets
     */
    public static void clearGenerationCache() {
        GENERATED_CASTLES.clear();
    }
}