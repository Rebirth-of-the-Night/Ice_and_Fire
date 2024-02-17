package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class GhostEntityProperties extends EntityProperties<EntityLivingBase> {

    public int ghostID;

    @Override
    public int getTrackingTime() {
        return 20;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("GhostID", ghostID);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        this.ghostID = compound.getInteger("GhostID");
    }

    public EntityGhost getGhost(World world) {
        Entity entity = world.getEntityByID(ghostID);
        if (entity instanceof EntityGhost) {
            return (EntityGhost) entity;
        }
        return null;
    }

    @Override
    public void init() {
    }

    @Override
    public String getID() {
        return "Ice And Fire - Ghost Property Tracker";
    }

    @Override
    public Class<EntityLivingBase> getEntityClass() {
        return EntityLivingBase.class;
    }
}
