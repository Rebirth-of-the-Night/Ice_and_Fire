package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum BiomeSpawnFilter {
    GORGON_TEMPLE("Structures - Gorgon Temples"),
    GRAVEYARD("Structures - Graveyards"),
    SIREN_ISLAND("Structures - Siren Islands"),
    CYCLOPS_CAVE("Structures - Cyclops Caves"),
    PIXIE_VILLAGE("Structures - Pixie Villages"),
    FIRE_DRAGON_ROOST("Structures - Fire Dragon Roosts"),
    ICE_DRAGON_ROOST("Structures - Ice Dragon Roosts"),
    LIGHTNING_DRAGON_ROOST("Structures - Lightning Dragon Roosts"),
    FIRE_DRAGON_DEN("Structures - Fire Dragon Dens"),
    ICE_DRAGON_DEN("Structures - Ice Dragon Dens"),
    LIGHTNING_DRAGON_DEN("Structures - Lightning Dragon Dens"),
    SNOW_VILLAGE("Structures - Snow Villages"),
    JUNGLE_MYRMEX_HIVE("Structures - Jungle Myrmex Hives"),
    DESERT_MYRMEX_HIVE("Structures - Desert Myrmex Hives"),
    MAUSOLEUM("Structures - Mausoleums"),
    HYDRA_CAVE("Structures - Hydra Caves"),
    ANIMAL_FARM("Structures - Village Animal Farms"),
    SCRIBER_HOUSE("Structures - Scriber Houses"),
    DREAD_CASTLE("Structures - Dread Castle"),
    DREAD_RUIN("Structures - Dread Ruins"),
    WANDERING_CYCLOPS("Mobs - Wandering Cyclopes"),
    FIRE_DRAGON_SKELETON("Mobs - Fire Dragon Skeletons"),
    ICE_DRAGON_SKELETON("Mobs - Ice Dragon Skeletons"),
    LIGHTNING_DRAGON_SKELETON("Mobs - Lightning Dragon Skeletons"),
    HIPPOCAMPUS("Mobs - Hippocampi"),
    SEA_SERPENT("Mobs - Sea Serpents"),
    STYMPHALIAN_BIRD("Mobs - Stymphalian Birds"),
    HIPPOGRYPH("Mobs - Hippogryphs"),
    DEATH_WORM("Mobs - Death Worms"),
    TROLL("Mobs - Trolls"),
    DREAD_LICH("Mobs - Dread Liches"),
    COCKATRICE("Mobs - Cockatrices"),
    AMPHITHERE("Mobs - Amphitheres"),
    DREAD_THRALL("Mobs - Dread Thralls"),
    DREAD_GHOUL("Mobs - Dread Ghouls"),
    DREAD_BEAST("Mobs - Dread Beasts"),
    DREAD_SCUTTLER("Mobs - Dread Scuttlers"),
    DREAD_KNIGHT("Mobs - Dread Knights");

    private final String configKey;
    // A null whitelist uses the original biome rules.
    private volatile Set<ResourceLocation> whitelist;
    private volatile Set<ResourceLocation> blacklist = Collections.emptySet();

    BiomeSpawnFilter(String configKey) {
        this.configKey = configKey;
    }

    public static void load(Configuration config) {
        for (BiomeSpawnFilter filter : values()) {
            String whitelistKey = filter.configKey + " Biome Whitelist";
            String blacklistKey = filter.configKey + " Biome Blacklist";
            String[] allowed = config.get("all", whitelistKey, new String[0],
                    "A non-empty list replaces the default biome rules. Use biome IDs such as minecraft:plains.")
                    .setRequiresMcRestart(true).getStringList();
            String[] denied = config.get("all", blacklistKey, new String[0],
                    "Biome IDs to exclude, even if they appear in the whitelist.")
                    .setRequiresMcRestart(true).getStringList();
            filter.whitelist = allowed.length == 0 ? null : readBiomes(allowed, whitelistKey);
            filter.blacklist = readBiomes(denied, blacklistKey);
        }
    }

    private static Set<ResourceLocation> readBiomes(String[] entries, String key) {
        Set<ResourceLocation> biomes = new HashSet<>();
        for (String entry : entries) {
            String id = entry.trim();
            if (!id.matches("(?i)(?:[a-z0-9_.-]+:)?[a-z0-9_./-]+")) {
                IceAndFire.logger.warn("Invalid biome ID '{}' in '{}'", entry, key);
                continue;
            }
            biomes.add(new ResourceLocation(id));
        }
        return biomes;
    }

    public boolean allows(Biome biome, boolean allowedByDefault) {
        ResourceLocation id = biome.getRegistryName();
        Set<ResourceLocation> allowed = whitelist;
        return !blacklist.contains(id) && (allowed == null ? allowedByDefault : allowed.contains(id));
    }

    public boolean allowsConfigured(World world, BlockPos pos) {
        return (whitelist == null && blacklist.isEmpty()) || allows(world.getBiome(pos), true);
    }
}
