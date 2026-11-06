package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.world.dimension.ChunkProviderDreadLands;
import com.github.alexthe666.iceandfire.world.gen.WorldGenCastle;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenDreadDimension implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (chunkGenerator instanceof ChunkProviderDreadLands) {
            WorldGenCastle.generateSlice(world, (ChunkProviderDreadLands) chunkGenerator, chunkX, chunkZ);
        }
    }
}