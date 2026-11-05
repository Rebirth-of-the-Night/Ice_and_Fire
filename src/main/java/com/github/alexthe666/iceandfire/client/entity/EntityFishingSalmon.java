package com.github.alexthe666.iceandfire.client.entity;

import com.github.alexthe666.iceandfire.event.FishingRippleEvents;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Client visual for a server fishing target. Uses the opaque entity pass so water blends over it. */
@SideOnly(Side.CLIENT)
public final class EntityFishingSalmon extends Entity {
    private static final double DEPTH = 0.25D;
    private static final int TIMEOUT_TICKS = 12;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float targetYaw;
    private int steps;
    private long lastUpdate;

    private EntityFishingSalmon(WorldClient world, double x, double y, double z) {
        super(world);
        setSize(0.9F, 0.3F);
        setPosition(x, y, z);
        lastTickPosX = x;
        lastTickPosY = y;
        lastTickPosZ = z;
        targetX = x;
        targetY = y;
        targetZ = z;
        lastUpdate = world.getTotalWorldTime();
    }

    public static void updateTarget(WorldClient world, int id, double x, double surfaceY, double z, boolean removed) {
        int visualId = Integer.MIN_VALUE + id;
        Entity existing = world.getEntityByID(visualId);
        if (existing instanceof EntityFishingSalmon) {
            if (removed) {
                world.removeEntityFromWorld(visualId);
            } else {
                ((EntityFishingSalmon) existing).updatePosition(x, surfaceY - DEPTH, z);
            }
        } else if (!removed && existing == null && world.isBlockLoaded(new BlockPos(x, surfaceY, z))) {
            world.addEntityToWorld(visualId, new EntityFishingSalmon(world, x, surfaceY - DEPTH, z));
        }
    }

    private void updatePosition(double x, double y, double z) {
        if (x != targetX || z != targetZ) {
            targetYaw = (float) Math.toDegrees(Math.atan2(x - targetX, z - targetZ));
        }
        targetX = x;
        targetY = y;
        targetZ = z;
        steps = FishingRippleEvents.SYNC_INTERVAL;
        lastUpdate = world.getTotalWorldTime();
    }

    @Override
    public void onUpdate() {
        if (world.getTotalWorldTime() - lastUpdate > TIMEOUT_TICKS) {
            ((WorldClient) world).removeEntityFromWorld(getEntityId());
            return;
        }
        if (steps > 0) {
            setPosition(posX + (targetX - posX) / steps, posY + (targetY - posY) / steps,
                    posZ + (targetZ - posZ) / steps);
            rotationYaw += MathHelper.wrapDegrees(targetYaw - rotationYaw) / steps;
            steps--;
        }
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}
}
