package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global (not per-dimension) record of the last known position and dimension of crystal-bound dragons,
 * so a summoning crystal can chunk load the dragon's last location when the dragon itself is unloaded.
 */
public class DragonPosWorldData extends WorldSavedData {

    private static final String IDENTIFIER = "iceandfire_dragonPositions";
    protected final Map<UUID, BlockPos> lastDragonPositions = new HashMap<>();
    protected final Map<UUID, Integer> lastDragonDimensions = new HashMap<>();
    private World world;
    private int tickCounter;

    public DragonPosWorldData(String name) {
        super(name);
    }

    public DragonPosWorldData(World world) {
        super(IDENTIFIER);
        this.world = world;
        this.markDirty();
    }

    public static DragonPosWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage == null) {
            return null;
        }
        DragonPosWorldData instance = (DragonPosWorldData) storage.getOrLoadData(DragonPosWorldData.class, IDENTIFIER);

        if (instance == null) {
            instance = new DragonPosWorldData(world);
            storage.setData(IDENTIFIER, instance);
        }
        return instance;
    }

    public void addDragon(UUID uuid, BlockPos pos, int dimension) {
        BlockPos old = lastDragonPositions.put(uuid, pos);
        Integer oldDim = lastDragonDimensions.put(uuid, dimension);
        if (!pos.equals(old) || oldDim == null || oldDim != dimension) {
            this.markDirty();
        }
    }

    public void removeDragon(UUID uuid) {
        if (lastDragonPositions.remove(uuid) != null) {
            this.markDirty();
        }
        lastDragonDimensions.remove(uuid);
    }

    public BlockPos getDragonPos(UUID uuid) {
        return lastDragonPositions.get(uuid);
    }

    /** @return the dimension the dragon was last seen in, or null if unknown. */
    public Integer getDragonDimension(UUID uuid) {
        return lastDragonDimensions.get(uuid);
    }

    public World getWorld() {
        return world;
    }

    public void debug() {
        IceAndFire.logger.warn(lastDragonPositions.toString());
    }


    public void tick() {
        ++this.tickCounter;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.tickCounter = nbt.getInteger("Tick");
        NBTTagList nbttaglist = nbt.getTagList("DragonMap", 10);
        this.lastDragonPositions.clear();
        this.lastDragonDimensions.clear();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            UUID uuid = nbttagcompound.getUniqueId("DragonUUID");
            BlockPos pos = new BlockPos(nbttagcompound.getInteger("DragonPosX"), nbttagcompound.getInteger("DragonPosY"), nbttagcompound.getInteger("DragonPosZ"));
            this.lastDragonPositions.put(uuid, pos);
            if (nbttagcompound.hasKey("DragonDim")) {
                this.lastDragonDimensions.put(uuid, nbttagcompound.getInteger("DragonDim"));
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Tick", this.tickCounter);
        NBTTagList nbttaglist = new NBTTagList();
        for (Map.Entry<UUID, BlockPos> pair : lastDragonPositions.entrySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setUniqueId("DragonUUID", pair.getKey());
            nbttagcompound.setInteger("DragonPosX", pair.getValue().getX());
            nbttagcompound.setInteger("DragonPosY", pair.getValue().getY());
            nbttagcompound.setInteger("DragonPosZ", pair.getValue().getZ());
            Integer dim = lastDragonDimensions.get(pair.getKey());
            if (dim != null) {
                nbttagcompound.setInteger("DragonDim", dim);
            }
            nbttaglist.appendTag(nbttagcompound);
        }
        compound.setTag("DragonMap", nbttaglist);
        return compound;
    }
}
