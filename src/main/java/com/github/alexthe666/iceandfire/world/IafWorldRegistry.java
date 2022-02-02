package com.github.alexthe666.iceandfire.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

public class IafWorldRegistry {

    public static DimensionType DREADLANDS_DIM;
    public static Biome GLACIER_BIOME = new BiomeGlacier();
    public static Biome DREADLANDS_BIOME = new BiomeDreadLands();

    public static void init() {
       // DREADLANDS_DIM = DimensionType.register("Dreadlands", "_dreadlands", IceAndFire.CONFIG.dreadlandsDimensionId, WorldProviderDreadLands.class, false);
       // DimensionManager.registerDimension(IceAndFire.CONFIG.dreadlandsDimensionId, DREADLANDS_DIM);
    }
}
