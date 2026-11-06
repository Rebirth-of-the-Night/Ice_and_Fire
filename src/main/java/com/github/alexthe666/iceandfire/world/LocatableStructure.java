package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.util.IceAndFireCoreUtils;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.util.Locale;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public enum LocatableStructure {
    GORGON_TEMPLE(c -> c.spawnGorgons, (biome, types) -> types.contains(Type.BEACH)),
    GRAVEYARD(c -> c.generateGraveyards, (biome, types) -> types.contains(Type.PLAINS)),
    SIREN_ISLAND(c -> c.generateSirenIslands, (biome, types) -> types.contains(Type.OCEAN) && !types.contains(Type.COLD)),
    CYCLOPS_CAVE(c -> c.generateCyclopsCaves, (biome, types) -> types.contains(Type.BEACH)),
    PIXIE_VILLAGE(c -> c.spawnPixies, (biome, types) -> allowsLegacyPixieBiome(biome)),
    FIRE_DRAGON_ROOST(c -> c.generateDragonRoosts, (biome, types) -> isFireDragonBiome(biome, types) && biome != Biomes.ICE_PLAINS),
    ICE_DRAGON_ROOST(c -> c.generateDragonRoosts, LocatableStructure::isColdAndSnowy),
    LIGHTNING_DRAGON_ROOST(c -> c.generateDragonRoosts, LocatableStructure::isLightningDragonBiome),
    FIRE_DRAGON_DEN(c -> c.generateDragonDens, (biome, types) -> isFireDragonBiome(biome, types) && !types.contains(Type.BEACH)),
    ICE_DRAGON_DEN(c -> c.generateDragonDens, (biome, types) -> isColdAndSnowy(biome, types) && !types.contains(Type.BEACH)),
    LIGHTNING_DRAGON_DEN(c -> c.generateDragonDens, LocatableStructure::isLightningDragonBiome),
    SNOW_VILLAGE(c -> c.generateSnowVillages, LocatableStructure::isColdAndSnowy),
    JUNGLE_MYRMEX_HIVE(c -> c.generateMyrmexColonies, (biome, types) -> types.contains(Type.JUNGLE)),
    DESERT_MYRMEX_HIVE(c -> c.generateMyrmexColonies, (biome, types) -> !types.contains(Type.JUNGLE)
            && types.contains(Type.HOT) && types.contains(Type.DRY) && types.contains(Type.SANDY)),
    MAUSOLEUM(c -> c.generateMausoleums, LocatableStructure::isColdAndSnowy),
    HYDRA_CAVE(c -> c.generateHydraCaves, (biome, types) -> types.contains(Type.SWAMP)),
    DREAD_RUIN(c -> c.enableDreadlands, (biome, types) -> true);

    private final Predicate<IceAndFireConfig> enabled;
    private final BiPredicate<Biome, Set<Type>> defaultBiome;
    private final BiomeSpawnFilter filter;

    LocatableStructure(Predicate<IceAndFireConfig> enabled, BiPredicate<Biome, Set<Type>> defaultBiome) {
        this.enabled = enabled;
        this.defaultBiome = defaultBiome;
        this.filter = BiomeSpawnFilter.valueOf(name());
    }

    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isEnabled() {
        return enabled.test(IceAndFire.CONFIG);
    }

    public boolean allows(Biome biome, Set<Type> types) {
        return filter.allows(biome, defaultBiome.test(biome, types));
    }

    private static boolean isColdAndSnowy(Biome biome, Set<Type> types) {
        return types.contains(Type.COLD) && types.contains(Type.SNOWY);
    }

    private static boolean isLightningDragonBiome(Biome biome, Set<Type> types) {
        return types.contains(Type.JUNGLE) || types.contains(Type.MESA) || types.contains(Type.SAVANNA);
    }

    private static boolean isFireDragonBiome(Biome biome, Set<Type> types) {
        return !biome.getEnableSnow() && biome.getDefaultTemperature() > 0.0
                && !types.contains(Type.COLD) && !types.contains(Type.SNOWY) && !types.contains(Type.WET)
                && !types.contains(Type.OCEAN) && !types.contains(Type.RIVER) && !types.contains(Type.SAVANNA);
    }

    private static boolean allowsLegacyPixieBiome(Biome biome) {
        ResourceLocation biomeId = biome.getRegistryName();
        return biomeId != null && IceAndFireCoreUtils.blackOrWhitelistCheck(IceAndFire.CONFIG.pixieVillageBiomeBlacklist,
                IceAndFire.CONFIG.pixieVillageBiomeBlacklistIsWhitelist, biomeId);
    }
}
