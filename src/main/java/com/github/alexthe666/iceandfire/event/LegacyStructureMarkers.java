package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class LegacyStructureMarkers {
    private LegacyStructureMarkers() {
    }

    static boolean supports(LocatableStructure structure, int dimension) {
        if (structure == LocatableStructure.DREAD_RUIN || structure == LocatableStructure.MAUSOLEUM) {
            return (structure == LocatableStructure.DREAD_RUIN) == (dimension == IceAndFire.CONFIG.dreadlandsDimensionId);
        }
        return lootTable(structure) != null;
    }

    @Nullable
    private static String lootTable(LocatableStructure structure) {
        switch (structure) {
            case MAUSOLEUM:
            case DREAD_RUIN:
                return "iceandfire:mausoleum_chest";
            case GRAVEYARD:
                return "iceandfire:graveyard_chest";
            case CYCLOPS_CAVE:
                return "iceandfire:cyclops_cave";
            case HYDRA_CAVE:
                return "iceandfire:hydra_cave";
            // Roosts share the female den loot tables.
            case FIRE_DRAGON_DEN:
                return "iceandfire:fire_dragon_male_cave";
            case ICE_DRAGON_DEN:
                return "iceandfire:ice_dragon_male_cave";
            case LIGHTNING_DRAGON_DEN:
                return "iceandfire:lightning_dragon_male_cave";
            default:
                return null;
        }
    }

    static boolean isSaved(File saveDirectory, ChunkPos chunk) {
        return RegionFileCache.chunkExists(saveDirectory, chunk.x, chunk.z);
    }

    static List<BlockPos> read(File saveDirectory, ChunkPos chunk, LocatableStructure structure) {
        RegionFile region = RegionFileCache.getRegionFileIfExists(saveDirectory, chunk.x, chunk.z);
        if (region == null) {
            return Collections.emptyList();
        }
        String lootTable = lootTable(structure);
        try (DataInputStream input = region.getChunkDataInputStream(chunk.x & 31, chunk.z & 31)) {
            if (input == null || lootTable == null) {
                return Collections.emptyList();
            }
            NBTTagCompound level = CompressedStreamTools.read(input).getCompoundTag("Level");
            NBTTagList tiles = level.getTagList("TileEntities", NBT.TAG_COMPOUND);
            List<BlockPos> markers = new ArrayList<>();
            for (int i = 0; i < tiles.tagCount(); i++) {
                NBTTagCompound tile = tiles.getCompoundTagAt(i);
                if (lootTable.equals(tile.getString("LootTable"))) {
                    BlockPos pos = new BlockPos(tile.getInteger("x"), tile.getInteger("y"), tile.getInteger("z"));
                    if (pos.getX() >> 4 == chunk.x && pos.getZ() >> 4 == chunk.z) {
                        markers.add(pos);
                    }
                }
            }
            return markers;
        } catch (IOException failure) {
            IceAndFire.logger.debug("Could not read saved structure markers in chunk {}", chunk, failure);
            return Collections.emptyList();
        }
    }
}
