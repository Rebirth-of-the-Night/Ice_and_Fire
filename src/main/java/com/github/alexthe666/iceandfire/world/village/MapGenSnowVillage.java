package com.github.alexthe666.iceandfire.world.village;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.event.StructureSearch;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import com.github.alexthe666.iceandfire.world.StructureLocatorData;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class MapGenSnowVillage extends WorldGenerator {
    private static final int VILLAGE_RADIUS = 32;
    public static List<Biome> VILLAGE_SPAWN_BIOMES = Arrays.asList(Biomes.ICE_MOUNTAINS, Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS, IafWorldRegistry.GLACIER_BIOME, Biomes.COLD_TAIGA_HILLS, Biomes.FROZEN_OCEAN, Biomes.COLD_TAIGA);
    private int size;
    private int distance;

    public MapGenSnowVillage() {
        this.distance = 9;
    }

    public MapGenSnowVillage(Map<String, String> map) {
        this();
        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("size")) {
                this.size = MathHelper.getInt(entry.getValue(), this.size, 0);
            } else if (entry.getKey().equals("distance")) {
                this.distance = MathHelper.getInt(entry.getValue(), this.distance, 9);
            }
        }
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        if (StructureSearch.getPopulationTarget(world, position) == LocatableStructure.SNOW_VILLAGE
                || rand.nextInt(IceAndFire.CONFIG.generateSnowVillageChance) == 0) {
            int chunkX = position.getX() >> 4;
            int chunkZ = position.getZ() >> 4;
            StructureStart start = getStructureStart(world, chunkX, chunkZ, rand);
            if (!start.isSizeableStructure()) {
                return false;
            }
            start.generateStructure(world, rand, getPlacementBounds(chunkX, chunkZ));
            StructureLocatorData.record(world, LocatableStructure.SNOW_VILLAGE, position);
            return true;
        }
        return false;
    }

    public String getStructureName() {
        return "SnowVillage";
    }

    protected boolean canSpawnStructureAtCoords(World world, int chunkX, int chunkZ) {
        if (chunkX < 0) {
            chunkX -= this.distance - 1;
        }
        if (chunkZ < 0) {
            chunkZ -= this.distance - 1;
        }

        int k = chunkX / this.distance;
        int l = chunkZ / this.distance;
        Random random = world.setRandomSeed(k, l, 10387312);
        k = k * this.distance;
        l = l * this.distance;
        k = k + random.nextInt(this.distance - 8);
        l = l + random.nextInt(this.distance - 8);
        return true;
    }

    protected StructureStart getStructureStart(World world, int chunkX, int chunkZ, Random random) {
        return new MapGenSnowVillage.Start(world, random, chunkX, chunkZ, this.size);
    }

    private static StructureBoundingBox getPlacementBounds(int chunkX, int chunkZ) {
        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;
        return new StructureBoundingBox(x - VILLAGE_RADIUS, z - VILLAGE_RADIUS, x + VILLAGE_RADIUS, z + VILLAGE_RADIUS);
    }

    public static class Start extends StructureStart {
        private boolean valid;

        public Start() {
        }

        public Start(World worldIn, Random rand, int x, int z, int size) {
            super(x, z);
            List<SnowVillagePieces.PieceWeight> list = SnowVillagePieces.getStructureVillageWeightedPieceList(rand, size);
            SnowVillagePieces.Start structurevillagepieces$start = new SnowVillagePieces.Start(worldIn, getPlacementBounds(x, z), rand, (x << 4) + 2, (z << 4) + 2, list, size);
            if (!structurevillagepieces$start.canPlace(structurevillagepieces$start.getBoundingBox(), false)) {
                return;
            }
            this.components.add(structurevillagepieces$start);
            structurevillagepieces$start.buildComponent(structurevillagepieces$start, this.components, rand);
            List<StructureComponent> list1 = structurevillagepieces$start.pendingRoads;
            List<StructureComponent> list2 = structurevillagepieces$start.pendingHouses;

            while (!list1.isEmpty() || !list2.isEmpty()) {
                if (list1.isEmpty()) {
                    int i = rand.nextInt(list2.size());
                    StructureComponent structurecomponent = list2.remove(i);
                    structurecomponent.buildComponent(structurevillagepieces$start, this.components, rand);
                } else {
                    int j = rand.nextInt(list1.size());
                    StructureComponent structurecomponent2 = list1.remove(j);
                    structurecomponent2.buildComponent(structurevillagepieces$start, this.components, rand);
                }
            }

            this.updateBoundingBox();
            this.valid = this.components.stream().anyMatch(SnowVillagePieces.WoodHut.class::isInstance);
        }

        public boolean isSizeableStructure() {
            return this.valid;
        }

        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            tagCompound.setBoolean("Valid", this.valid);
        }

        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            this.valid = tagCompound.getBoolean("Valid");
        }
    }
}
