package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import com.github.alexthe666.iceandfire.util.EntityUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class AIBlackFrostPassiveCircle<T extends EntityBlackFrostDragon> extends EntityAIBase {
    private final EntityBlackFrostDragon entity;
    private @Nullable Vec3d planeVectorPath = getNewPlaneVector();
    private final float circleRadius;

    public AIBlackFrostPassiveCircle(T entity, float circleRadius) {
        this.entity = entity;
        this.circleRadius = circleRadius;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return entity.getSpawnPointPos() != null && !this.entity.onGround && this.entity.isPhraseOne();
    }

    @Override
    public void updateTask() {
        if(entity.getSpawnPointPos() != null &&/* !this.entity.lockLook && */!this.entity.onGround) {
                Vec3d target = new Vec3d(entity.getSpawnPointPos());
                Vec3d nextPointToFollow = getNextPoint(target);
                Vec3d direction = nextPointToFollow.subtract(entity.getPositionVector()).normalize();
                double speed = entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue();
                if(this.entity.isPhraseOne()) {
                    EntityUtil.addEntityVelocity(entity, direction.scale(0.15 * speed));
                } else {
                    EntityUtil.addEntityVelocity(entity, direction.scale(0.2 * speed));
                }
                double distSq = this.entity.getDistanceSq(target.x, target.y, target.z);
                double distanceFrom = distSq * distSq;

                Vec3d entityPos = this.entity.getPositionEyes(1);
                Vec3d forwardVec = EntityUtil.direction(entityPos, nextPointToFollow);
                this.entity.setPitch(forwardVec);

                if(distanceFrom <= 30) {
                    BlockPos targetI = this.entity.getSpawnPointPos();
                    double d0 = (this.entity.posX - targetI.getX()) * 0.015;
                    double d1 = (this.entity.posY - targetI.getY()) * 0.009;
                    double d2 = (this.entity.posZ - targetI.getZ()) * 0.015;
                    this.entity.addVelocity(d0, d1, d2);
                }

                if (!hasClearPath(nextPointToFollow) || lineBlocked(nextPointToFollow, target)) {
                    planeVectorPath = getNewPlaneVector();
                }

                EntityUtil.facePosition(nextPointToFollow, entity, 10, 10);
                entity.getLookHelper().setLookPosition(nextPointToFollow.x, nextPointToFollow.y, nextPointToFollow.z, 3, 3);

        }

        super.updateTask();
    }


    private Vec3d getNewPlaneVector() {
        return EntityUtil.Y_AXIS.add(EntityUtil.randVec().scale(1.0)).normalize();
    }

    private boolean hasClearPath(Vec3d nextPointToFollow) {
        return EntityUtil.getBoundingBoxCorners(entity.getEntityBoundingBox()).stream().noneMatch(vec3d -> lineBlocked(vec3d, nextPointToFollow));
    }

    private boolean lineBlocked(Vec3d start, Vec3d nextPointToFollow) {
        RayTraceResult rayTraceResult = entity.world.rayTraceBlocks(start, nextPointToFollow, false, true, false);
        return rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK;
    }

    private Vec3d getNextPoint(Vec3d center) {
        Vec3d circlePointInWorld = entity.getPositionVector().add(EntityUtil.randVec());
        for(int i = 0; i < circleRadius; i++) {
            for(int sign : new int[]{1, -1}) {
                Vec3d entityVelocity = EntityUtil.getEntityVelocity(entity);
                Vec3d entityDirection = entity.getPositionVector().subtract(center);
                Vec3d projectedEntityDirection = EntityUtil.planeProject(entityDirection, planeVectorPath).normalize().scale(circleRadius + (i * sign));
                Vec3d nextPointOnCircle = EntityUtil.rotateVector2(projectedEntityDirection, planeVectorPath, 15 * entityVelocity.length());
                circlePointInWorld = nextPointOnCircle.add(center);
                if (hasClearPath(circlePointInWorld)) {
                    return circlePointInWorld;
                } else {
                    planeVectorPath = getNewPlaneVector();
                }
            }
        }
        return circlePointInWorld;
    }
}