package com.github.alexthe666.iceandfire.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.Vec3d;

public class IafDragonFlightManager {
    private final EntityDragonBase dragon;
    private Vec3d target;

    public IafDragonFlightManager(EntityDragonBase dragon) {
        this.dragon = dragon;
    }

    public void update() {
        EntityLivingBase attackTarget = dragon.getAttackTarget();
        if (attackTarget != null && !attackTarget.isDead) {

                float distY = 5 + dragon.getDragonStage() * 2;
                int randomDist = 20;
                if (dragon.getDistance(attackTarget.posX, dragon.posY, attackTarget.posZ) < 4 || dragon.getDistance(attackTarget.posX, dragon.posY, attackTarget.posZ) > 30) {
                    setTarget(new Vec3d(attackTarget.posX + dragon.getRNG().nextInt(randomDist) - randomDist / 2, attackTarget.posY + distY, attackTarget.posZ + dragon.getRNG().nextInt(randomDist) - randomDist / 2));
                }
                dragon.stimulateFire(attackTarget.posX, attackTarget.posY, attackTarget.posZ, 3);
        }

    }

    public void setTarget(Vec3d target) {
//        if (target == null) {
//            this.target = null;
//            return;
//        }
//        double y = target.y;
//        boolean b = dragon.world.getBlockState(new BlockPos(target.x, y, target.z)).getMaterial().isLiquid();
//        while (dragon.world.getBlockState(new BlockPos(target.x, y, target.z)).getMaterial().isLiquid()) {
//            y += 1.0;
//        }
//        this.target = new Vec3d(target.x, y + (b ? 10 : 0), target.z);
        this.target = target;
    }

    protected static class PlayerFlightMoveHelper<T extends EntityCreature & IFlyingMount> extends EntityMoveHelper {

        private final T dragon;

        public PlayerFlightMoveHelper(T dragon) {
            super(dragon);
            this.dragon = dragon;
        }

        @Override
        public void onUpdateMoveHelper() {
            double flySpeed = speed * speedMod();
            Vec3d dragonVec = dragon.getPositionVector();
            Vec3d moveVec = new Vec3d(posX, posY, posZ);
            Vec3d normalized = moveVec.subtract(dragonVec).normalize();
            double dist = dragonVec.distanceTo(moveVec);
            dragon.motionX = normalized.x * flySpeed;
            dragon.motionY = normalized.y * flySpeed;
            dragon.motionZ = normalized.z * flySpeed;
            if (dist > 2.5E-7) {
                float yaw = (float) Math.toDegrees(Math.PI * 2 - Math.atan2(normalized.x, normalized.y));
                dragon.rotationYaw = limitAngle(dragon.rotationYaw, yaw, 5);
                entity.setAIMoveSpeed((float)(speed));
            }
            dragon.move(MoverType.SELF, dragon.motionX, dragon.motionY, dragon.motionZ);
        }

        public double speedMod(){
            return dragon instanceof EntityAmphithere ? 0.75D : 0.5D;
        }
    }
}
