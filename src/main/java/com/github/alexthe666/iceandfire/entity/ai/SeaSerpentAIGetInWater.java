package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SeaSerpentAIGetInWater extends EntityAIBase {

    private static final int NEARBY_RADIUS = 24;
    private static final int SHORE_RADIUS = 64;
    private static final int SEARCH_HEIGHT = 16;
    private static final int SEARCH_INTERVAL = 20;

    private final EntitySeaSerpent serpent;
    private BlockPos waterTarget;
    private int searchDelay;
    private int wiggleTicks;

    public SeaSerpentAIGetInWater(EntitySeaSerpent serpent) {
        this.setMutexBits(3);
        this.serpent = serpent;
    }

    @Override
    public boolean shouldExecute() {
        return serpent.onGround && !serpent.isInWater();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !serpent.isInWater();
    }

    @Override
    public void startExecuting() {
        serpent.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        serpent.getNavigator().clearPath();
        waterTarget = null;
        searchDelay = 0;
        wiggleTicks = 0;
    }

    @Override
    public void updateTask() {
        if (!serpent.onGround) {
            return;
        }
        PathNavigate navigator = serpent.getNavigator();
        if (waterTarget != null && (!serpent.world.isBlockLoaded(waterTarget) || !isOpenWater(waterTarget))) {
            navigator.clearPath();
            waterTarget = null;
            searchDelay = 0;
        }
        if (navigator.noPath()) {
            if (searchDelay == 0) {
                BlockPos nearestWater = findWater(NEARBY_RADIUS);
                if (nearestWater == null) {
                    nearestWater = findWater(SHORE_RADIUS);
                }
                // Let the wriggle take over if a path to the same destination already failed.
                if (nearestWater != null && !nearestWater.equals(waterTarget)) {
                    navigator.tryMoveToXYZ(nearestWater.getX() + 0.5D, nearestWater.getY(), nearestWater.getZ() + 0.5D, 1.0D);
                }
                waterTarget = nearestWater;
                searchDelay = SEARCH_INTERVAL;
            }
            searchDelay--;
            if (waterTarget != null && navigator.noPath()) {
                wriggleTowardWater();
            }
        }
    }

    private void wriggleTowardWater() {
        double dx = waterTarget.getX() + 0.5D - serpent.posX;
        double dz = waterTarget.getZ() + 0.5D - serpent.posZ;
        float swing = MathHelper.sin(wiggleTicks++ * 0.2F);
        double heading = MathHelper.atan2(dz, dx) + swing * (serpent.collidedHorizontally ? 0.9D : 0.35D);
        serpent.rotationYaw = (float) Math.toDegrees(heading) - 90.0F;
        serpent.motionX = Math.cos(heading) * 0.18D;
        serpent.motionZ = Math.sin(heading) * 0.18D;
        if (serpent.collidedHorizontally) {
            serpent.getJumpHelper().setJumping();
        }
    }

    @Nullable
    private BlockPos findWater(int radius) {
        World world = serpent.world;
        BlockPos origin = new BlockPos(serpent);
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        int minY = Math.max(0, origin.getY() - SEARCH_HEIGHT);
        int maxY = Math.min(world.getActualHeight() - 1, origin.getY() + SEARCH_HEIGHT);
        for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
            for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                double dx = x + 0.5D - serpent.posX;
                double dz = z + 0.5D - serpent.posZ;
                double horizontalDistance = dx * dx + dz * dz;
                if (horizontalDistance > radius * radius || horizontalDistance >= nearestDistance
                        || !world.isBlockLoaded(candidate.setPos(x, origin.getY(), z))) {
                    continue;
                }
                int bottom = minY;
                int top = maxY;
                if (radius == SHORE_RADIUS) {
                    // Use the heightmap for the wider search to avoid scanning entire columns.
                    bottom = top = world.getHeight(x, z) - 1;
                }
                for (int y = Math.max(0, bottom); y <= top; y++) {
                    double dy = y - serpent.posY;
                    double distance = horizontalDistance + dy * dy;
                    if (distance < nearestDistance && canEnterWater(candidate.setPos(x, y, z))) {
                        nearest = candidate.toImmutable();
                        nearestDistance = distance;
                    }
                }
            }
        }
        return nearest;
    }

    private boolean canEnterWater(BlockPos pos) {
        if (!isOpenWater(pos)) {
            return false;
        }
        // A large serpent can straddle the shoreline even with its center over water.
        double x = pos.getX() + 0.5D - serpent.posX;
        double y = pos.getY() + 0.5D - serpent.posY;
        double z = pos.getZ() + 0.5D - serpent.posZ;
        AxisAlignedBB entryBox = serpent.getEntityBoundingBox().offset(x, y, z);
        return !serpent.world.collidesWithAnyBlock(entryBox);
    }

    private boolean isOpenWater(BlockPos pos) {
        World world = serpent.world;
        if (!EntitySeaSerpent.isWaterBlock(world, pos)) {
            return false;
        }
        BlockPos above = pos.up();
        return !EntitySeaSerpent.isWaterBlock(world, above)
                && world.getBlockState(above).getCollisionBoundingBox(world, above) == Block.NULL_AABB;
    }
}
