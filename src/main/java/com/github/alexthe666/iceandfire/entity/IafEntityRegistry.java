package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.world.BiomeSpawnFilter;
import com.github.alexthe666.iceandfire.world.BiomeDreadLands;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IafEntityRegistry {

    private static final Set<Biome> defaultSpawnBiomes = new HashSet<>();

    public static void init() {
        EntityPropertiesHandler.INSTANCE.registerProperties(StoneEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(MiscEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(FrozenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(SirenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(GhostEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(ChickenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(ChainEntityProperties.class);

        // Retain the biomes eligible for the original pre-init spawn registration.
        Biome.REGISTRY.forEach(defaultSpawnBiomes::add);
    }

    public static void registerSpawns() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome == null) {
                continue;
            }
            boolean useDefaults = defaultSpawnBiomes.contains(biome);
            List<Biome.SpawnListEntry> creatures = biome.getSpawnableList(EnumCreatureType.CREATURE);
            List<Biome.SpawnListEntry> monsters = biome.getSpawnableList(EnumCreatureType.MONSTER);
            registerDreadSpawns(biome, monsters);

            if (IceAndFire.CONFIG.spawnHippogryphs && BiomeSpawnFilter.HIPPOGRYPH.allows(biome,
                    useDefaults && BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS))) {
                for (EnumHippogryphTypes type : EnumHippogryphTypes.values()) {
                    if (!type.developer) {
                        creatures.add(new Biome.SpawnListEntry(EntityHippogryph.class, IceAndFire.CONFIG.hippogryphSpawnRate, 1, 1));
                    }
                }
            }
            if (IceAndFire.CONFIG.spawnDeathWorm && BiomeSpawnFilter.DEATH_WORM.allows(biome,
                    useDefaults && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
                            && BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)
                            && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
                            && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA))) {
                creatures.add(new Biome.SpawnListEntry(EntityDeathWorm.class, IceAndFire.CONFIG.deathWormSpawnRate, 1, 3));
            }
            if (IceAndFire.CONFIG.spawnTrolls) {
                int matchingTypes = 0;
                for (EnumTroll type : EnumTroll.values()) {
                    if (BiomeDictionary.hasType(biome, type.spawnBiome)) {
                        matchingTypes++;
                    }
                }
                if (BiomeSpawnFilter.TROLL.allows(biome, useDefaults && matchingTypes > 0)) {
                    for (int i = 0; i < Math.max(1, matchingTypes); i++) {
                        monsters.add(new Biome.SpawnListEntry(EntityTroll.class, IceAndFire.CONFIG.trollSpawnRate, 1, 1));
                    }
                }
            }
            if (IceAndFire.CONFIG.spawnLiches && BiomeSpawnFilter.DREAD_LICH.allows(biome,
                    useDefaults && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY))) {
                monsters.add(new Biome.SpawnListEntry(EntityDreadLich.class, IceAndFire.CONFIG.lichSpawnRate, 1, 1));
            }
            if (IceAndFire.CONFIG.spawnCockatrices && BiomeSpawnFilter.COCKATRICE.allows(biome,
                    useDefaults && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SAVANNA)
                            && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPARSE))) {
                creatures.add(new Biome.SpawnListEntry(EntityCockatrice.class, IceAndFire.CONFIG.cockatriceSpawnRate, 1, 2));
            }
            if (IceAndFire.CONFIG.spawnAmphitheres && BiomeSpawnFilter.AMPHITHERE.allows(biome,
                    useDefaults && BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE))) {
                creatures.add(new Biome.SpawnListEntry(EntityAmphithere.class, IceAndFire.CONFIG.amphithereSpawnRate, 1, 3));
            }
        }
        defaultSpawnBiomes.clear();
    }

    private static void registerDreadSpawns(Biome biome, List<Biome.SpawnListEntry> monsters) {
        boolean dreadlands = biome instanceof BiomeDreadLands;
        addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_THRALL, EntityDreadThrall.class, 100, dreadlands);
        addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_GHOUL, EntityDreadGhoul.class, 70, dreadlands);
        addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_BEAST, EntityDreadBeast.class, 60, dreadlands);
        addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_SCUTTLER, EntityDreadScuttler.class, 70, dreadlands);
        addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_KNIGHT, EntityDreadKnight.class, 70, dreadlands);
        if (dreadlands) {
            addDreadSpawn(biome, monsters, BiomeSpawnFilter.DREAD_LICH, EntityDreadLich.class, 10, true);
        }
    }

    private static void addDreadSpawn(Biome biome, List<Biome.SpawnListEntry> monsters, BiomeSpawnFilter filter,
                                      Class<? extends EntityLiving> entity, int weight, boolean allowedByDefault) {
        if (filter.allows(biome, allowedByDefault)) {
            monsters.add(new Biome.SpawnListEntry(entity, weight, 1, 1));
        }
    }
}
