package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.UUID;

public class MyrmexWorldData extends WorldSavedData {

    private static final String IDENTIFIER = "iceandfire_myrmex";
    private final List<MyrmexHive> hiveList = Lists.newArrayList();
    private World world;
    private int tickCounter;

    public MyrmexWorldData(String name) {
        super(name);
    }

    public MyrmexWorldData(World world) {
        super(IDENTIFIER);
        this.world = world;
        this.markDirty();
    }

    public static MyrmexWorldData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        MyrmexWorldData instance = (MyrmexWorldData) storage.getOrLoadData(MyrmexWorldData.class, IDENTIFIER);

        if (instance == null) {
            instance = new MyrmexWorldData(world);
            storage.setData(IDENTIFIER, instance);
        }
        instance.markDirty();
        return instance;
    }

    public static void addHive(World world, MyrmexHive hive) {
        get(world).hiveList.add(hive);
    }

    public void setWorldsForAll(World worldIn) {
        this.world = worldIn;
        for (MyrmexHive village : this.hiveList) {
            village.setWorld(worldIn);
        }
    }

    public void tick() {
        ++this.tickCounter;
        for (MyrmexHive hive : this.hiveList) {
            hive.tick(this.tickCounter, world);
        }
        //this.removeAnnihilatedHives();

    }

    public List<MyrmexHive> getHivelist() {
        return this.hiveList;
    }

    public MyrmexHive getNearestHive(BlockPos doorBlock, int radius) {
        MyrmexHive village = null;
        double d0 = 3.4028234663852886E38D;

        for (MyrmexHive village1 : this.hiveList) {
            double d1 = village1.getCenter().distanceSq(doorBlock);

            if (d1 < d0) {
                float f = (float) (radius + village1.getVillageRadius());

                if (d1 <= (double) (f * f)) {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    public void debug() {
        for (MyrmexHive hive : this.hiveList) {
            IceAndFire.logger.debug(hive);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.tickCounter = nbt.getInteger("Tick");
        NBTTagList nbttaglist = nbt.getTagList("Hives", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            MyrmexHive village = new MyrmexHive();
            village.readVillageDataFromNBT(nbttagcompound);
            this.hiveList.add(village);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Tick", this.tickCounter);
        NBTTagList nbttaglist = new NBTTagList();

        for (MyrmexHive village : this.hiveList) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            village.writeVillageDataToNBT(nbttagcompound);
            nbttaglist.appendTag(nbttagcompound);
        }

        compound.setTag("Hives", nbttaglist);
        return compound;
    }

    public MyrmexHive getHiveFromUUID(UUID id) {
        for (MyrmexHive hive : this.hiveList) {
            if (hive.hiveUUID != null && hive.hiveUUID.equals(id)) {
                return hive;
            }
        }
        return null;
    }
}
