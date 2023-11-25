package com.github.alexthe666.iceandfire.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HomePosition {
    private int x;
    private int y;
    private int z;
    private BlockPos pos;
    private Integer dimension;

    public HomePosition(NBTTagCompound compound) {
        read(compound);
    }

    public HomePosition(NBTTagCompound compound, World world) {
        read(compound, world);
    }

    public HomePosition(BlockPos pos, World world) {
        this(pos.getX(), pos.getY(), pos.getZ(), world);
    }

    public HomePosition(int x, int y, int z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        pos = new BlockPos(x, y, z);
        this.dimension = DragonUtils.getDimensionID(world);
    }

    public BlockPos getPosition() {
        return pos;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void write(NBTTagCompound compound) {
        compound.setInteger("HomeAreaX", this.x);
        compound.setInteger("HomeAreaY", this.y);
        compound.setInteger("HomeAreaZ", this.z);
        if (dimension != null)
            compound.setInteger("HomeDimension", this.dimension);
    }

    public void read(NBTTagCompound compound, World world) {
        read(compound);
        if (this.dimension == null)
            this.dimension = DragonUtils.getDimensionID(world);
    }

    public void read(NBTTagCompound compound) {
        if (compound.hasKey("HomeAreaX"))
            this.x = compound.getInteger("HomeAreaX");
        if (compound.hasKey("HomeAreaY"))
            this.y = compound.getInteger("HomeAreaY");
        if (compound.hasKey("HomeAreaZ"))
            this.z = compound.getInteger("HomeAreaZ");
        pos = new BlockPos(x, y, z);
        if (compound.hasKey("HomeDimension"))
            this.dimension = compound.getInteger("HomeDimension");
    }
}
