package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class StructureLocatorData extends WorldSavedData {
    private static final String ID = "iceandfire_structure_locations";
    private final Map<LocatableStructure, Set<BlockPos>> locations = new EnumMap<>(LocatableStructure.class);

    public StructureLocatorData(String name) {
        super(name);
    }

    @Nullable
    private static StructureLocatorData get(World world, boolean create) {
        MapStorage storage = world.getPerWorldStorage();
        StructureLocatorData data = (StructureLocatorData) storage.getOrLoadData(StructureLocatorData.class, ID);
        if (data == null && create) {
            data = new StructureLocatorData(ID);
            storage.setData(ID, data);
        }
        return data;
    }

    public static void record(World world, LocatableStructure structure, BlockPos pos) {
        if (!IceAndFire.CONFIG.enableStructureLocator) {
            return;
        }
        StructureLocatorData data = get(world, true);
        if (data.locations.computeIfAbsent(structure, ignored -> new LinkedHashSet<>()).add(pos.toImmutable())) {
            data.markDirty();
        }
    }

    public static Set<BlockPos> getKnown(World world, LocatableStructure structure) {
        StructureLocatorData data = get(world, false);
        Set<BlockPos> positions = data == null ? null : data.locations.get(structure);
        return positions == null ? new HashSet<>() : new HashSet<>(positions);
    }

    @Nullable
    public static BlockPos findNearest(World world, LocatableStructure structure, BlockPos origin) {
        return findNearest(world, structure, origin, null);
    }

    @Nullable
    public static BlockPos findNearest(World world, LocatableStructure structure, BlockPos origin,
                                       @Nullable Set<BlockPos> excluded) {
        StructureLocatorData data = get(world, false);
        Set<BlockPos> positions = data == null ? null : data.locations.get(structure);
        if (positions == null) {
            return null;
        }
        BlockPos nearest = null;
        double distance = Double.POSITIVE_INFINITY;
        for (BlockPos pos : positions) {
            if (excluded != null && excluded.contains(pos)) {
                continue;
            }
            double dx = (double) pos.getX() - origin.getX();
            double dz = (double) pos.getZ() - origin.getZ();
            double candidate = dx * dx + dz * dz;
            if (candidate < distance && world.getWorldBorder().contains(pos)) {
                nearest = pos;
                distance = candidate;
            }
        }
        return nearest;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        locations.clear();
        for (LocatableStructure structure : LocatableStructure.values()) {
            NBTTagList list = nbt.getTagList(structure.getId(), NBT.TAG_LONG);
            if (list.tagCount() == 0) {
                continue;
            }
            Set<BlockPos> positions = new LinkedHashSet<>();
            for (int i = 0; i < list.tagCount(); i++) {
                positions.add(BlockPos.fromLong(((NBTTagLong) list.get(i)).getLong()));
            }
            locations.put(structure, positions);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        for (Map.Entry<LocatableStructure, Set<BlockPos>> entry : locations.entrySet()) {
            NBTTagList list = new NBTTagList();
            for (BlockPos pos : entry.getValue()) {
                list.appendTag(new NBTTagLong(pos.toLong()));
            }
            nbt.setTag(entry.getKey().getId(), list);
        }
        return nbt;
    }
}
