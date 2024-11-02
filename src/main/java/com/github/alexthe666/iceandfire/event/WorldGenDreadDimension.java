package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.world.dimension.WorldProviderDreadLands;
import com.github.alexthe666.iceandfire.world.gen.WorldGenCastle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * @author Pabilo8
 * @since 27.06.2022
 */
public class WorldGenDreadDimension implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        //BlockPos ANU_CASTLE_POS = new BlockPos(-63, world.getHeight(-63, -165) / 2, -165);
//
        //if (world.getChunk(chunkX, chunkZ) == world.getChunk(ANU_CASTLE_POS) && world.provider instanceof WorldProviderDreadLands) {
        //    int counter = 0;
        //    if (++counter == 1) {
        //        new WorldGenCastle().generate(world, random, new BlockPos(-126, world.getHeight(-63, -165), -330));
        //    }
        //}


    }
}
