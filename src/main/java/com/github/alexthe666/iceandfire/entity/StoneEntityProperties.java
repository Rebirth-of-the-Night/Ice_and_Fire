package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public class StoneEntityProperties extends EntityProperties<EntityLiving> {
    public boolean isStone;
    public int breakLvl;
    private boolean poseFrozen;
    private float bodyYaw;
    private float headYaw;
    private float headPitch;

    public static void applyPetrifiedName(EntityLiving statue, String originalName) {
        String prefix = "Petrified ";
        if (!originalName.startsWith(prefix)) {
            statue.setCustomNameTag(prefix + originalName);
        }
    }

    public void freezePose(EntityLiving statue) {
        if (!poseFrozen) {
            bodyYaw = statue.renderYawOffset;
            headYaw = statue.rotationYawHead;
            headPitch = statue.rotationPitch;
            poseFrozen = true;
        }
        statue.renderYawOffset = statue.prevRenderYawOffset = bodyYaw;
        statue.rotationYawHead = statue.prevRotationYawHead = headYaw;
        statue.rotationPitch = statue.prevRotationPitch = headPitch;
    }

    public void clearFrozenPose() {
        poseFrozen = false;
    }

    @Override
    public int getTrackingTime() {
        return 20;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean("TurnedToStone", isStone);
        compound.setInteger("StoneBreakLvl", breakLvl);
        if (isStone && poseFrozen) {
            NBTTagCompound pose = new NBTTagCompound();
            pose.setFloat("BodyYaw", bodyYaw);
            pose.setFloat("HeadYaw", headYaw);
            pose.setFloat("HeadPitch", headPitch);
            compound.setTag("StonePose", pose);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        this.isStone = compound.getBoolean("TurnedToStone");
        this.breakLvl = compound.getInteger("StoneBreakLvl");
        poseFrozen = isStone && compound.hasKey("StonePose", 10);
        if (poseFrozen) {
            NBTTagCompound pose = compound.getCompoundTag("StonePose");
            bodyYaw = pose.getFloat("BodyYaw");
            headYaw = pose.getFloat("HeadYaw");
            headPitch = pose.getFloat("HeadPitch");
        }
    }

    @Override
    public void init() {
        isStone = false;
        breakLvl = 0;
    }

    @Override
    public String getID() {
        return "Ice And Fire - Stone Property Tracker";
    }

    @Override
    public Class<EntityLiving> getEntityClass() {
        return EntityLiving.class;
    }
}
