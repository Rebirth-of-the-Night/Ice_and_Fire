package com.github.alexthe666.iceandfire.world.gen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class VillageAvoidance {
    private VillageAvoidance() {
    }

    public static boolean intersects(World world, BlockPos first, BlockPos opposite) {
        int minX = Math.min(first.getX(), opposite.getX());
        int minZ = Math.min(first.getZ(), opposite.getZ());
        int maxX = Math.max(first.getX(), opposite.getX());
        int maxZ = Math.max(first.getZ(), opposite.getZ());
        if (intersectsKnownVillage(world, minX, minZ, maxX, maxZ)) {
            return true;
        }
        if (!world.getWorldInfo().isMapFeaturesEnabled()) {
            return false;
        }

        // Vanilla records village bounds during terrain generation, before placing roads and houses.
        // Prepare any missing chunks in the footprint before checking the saved bounds again.
        boolean preparedChunks = false;
        for (int chunkX = minX >> 4; chunkX <= (maxX >> 4); chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= (maxZ >> 4); chunkZ++) {
                if (world.getChunkProvider().getLoadedChunk(chunkX, chunkZ) == null) {
                    world.getChunk(chunkX, chunkZ);
                    preparedChunks = true;
                }
            }
        }
        return preparedChunks && intersectsKnownVillage(world, minX, minZ, maxX, maxZ);
    }

    private static boolean intersectsKnownVillage(World world, int minX, int minZ, int maxX, int maxZ) {
        MapGenStructureData data = (MapGenStructureData) world.getPerWorldStorage()
                .getOrLoadData(MapGenStructureData.class, "Village");
        if (data == null) {
            return false;
        }
        NBTTagCompound starts = data.getTagCompound();
        for (String key : starts.getKeySet()) {
            NBTTagCompound start = starts.getCompoundTag(key);
            if (!start.getBoolean("Valid")) {
                continue;
            }
            int[] bounds = start.getIntArray("BB");
            if (bounds.length == 6 && new StructureBoundingBox(bounds).intersectsWith(minX, minZ, maxX, maxZ)) {
                return true;
            }
        }
        return false;
    }
}
