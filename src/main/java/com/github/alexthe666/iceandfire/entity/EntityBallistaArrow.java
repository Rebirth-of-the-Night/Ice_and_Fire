package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.IsImmune;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBallistaArrow extends EntityFireball {
    public int ticksInAir;

    public EntityBallistaArrow(World w) {
        super(w);
        this.setEntityBoundingBox(new AxisAlignedBB(2, 2, 2, 2, 2, 2));
    }

    public EntityBallistaArrow(World w, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(w, shooter, accelX, accelY, accelZ);
        double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.14D;
        this.accelerationY = accelY / d0 * 0.14D;
        this.accelerationZ = accelZ / d0 * 0.14D;

        this.motionX *= 3;
        this.motionY *= 3;
        this.motionZ *= 3;
    }

    public void onUpdate() {
        if (this.world.isRemote) {
            for (int i = 0; i < 14; ++i) {
                IceAndFire.PROXY.spawnParticle("dragonice", this.posX + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), this.posY + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), this.posZ + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), 0.0D, 0.0D, 0.0D);
            }
        }
        if (this.world.isRemote || (shootingEntity == null || !shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
            super.onUpdate();

            ++this.ticksInAir;
            RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, false, this.ticksInAir >= 25, shootingEntity);

            if (raytraceresult != null) {
                this.onImpact(raytraceresult);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float f = this.getMotionFactor();

            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
            this.motionX *= f;
            this.motionY *= f;
            this.motionZ *= f;
            this.world.spawnParticle(this.getParticleType(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
            this.setPosition(this.posX, this.posY, this.posZ);
        } else {
            this.setDead();
        }
    }

    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    protected void onImpact(RayTraceResult movingObject) {
        if (movingObject == null)
            return;

        if (!this.world.isRemote) {
            Entity entityHit = movingObject.entityHit;
            if (entityHit instanceof IDragonProjectile) {
                return;
            }
            EntityLivingBase shootingEntity = this.shootingEntity;
            if (shootingEntity instanceof EntityCastleBallista) {
                if (entityHit != null && !entityHit.isEntityEqual(shootingEntity)) {
                    if (!entityHit.isEntityEqual(shootingEntity)) {
                        float attackDamage = IceAndFire.CONFIG.ballistaBaseDamage;
                        if (!entityHit.onGround)
                            attackDamage *= 1.5;
                        if (entityHit instanceof EntityCastleBallista)
                            attackDamage *= 5;

                        entityHit.attackEntityFrom(IceAndFire.dragonFire, attackDamage);

                        if (entityHit instanceof EntityLivingBase) {
                            if (!IsImmune.toDragonIce(entityHit)) {
                                FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(entityHit, FrozenEntityProperties.class);
                                if (frozenProps != null) {
                                    frozenProps.setFrozenFor(200);
                                }
                            }
                        }
                    }
                    this.applyEnchantments(shootingEntity, entityHit);
                    this.setDead();
                }
            }
        }
        this.setDead();
    }
}