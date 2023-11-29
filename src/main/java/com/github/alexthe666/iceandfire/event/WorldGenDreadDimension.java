package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.world.dimension.WorldProviderDreadLands;
import com.github.alexthe666.iceandfire.world.gen.WorldGenCastle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import static com.github.alexthe666.iceandfire.event.WorldGenEvents.degradeSurface;
import static com.github.alexthe666.iceandfire.event.WorldGenEvents.getHeight;

/**
 * @author Pabilo8
 * @since 27.06.2022
 */
public class WorldGenDreadDimension implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        if (world.provider instanceof WorldProviderDreadLands) {
            int x = (chunkX * 16) + 8;
            int z = (chunkZ * 16) + 8;
            BlockPos height = getHeight(world, new BlockPos(x, 0, z));

            if (BiomeDictionary.hasType(world.getBiome(height), BiomeDictionary.Type.COLD) && BiomeDictionary.hasType(world.getBiome(height), BiomeDictionary.Type.SNOWY)) {
               // if (random.nextInt(1000) == 0) {
                    BlockPos surface = world.getHeight(new BlockPos(x, 0, z));
                    surface = degradeSurface(world, surface);
                    new WorldGenCastle().generate(world, random, surface);
                    //lastMausoleum = surface;
             //   }
            }
        }

    }
}
