package com.github.alexthe666.iceandfire.entity;

/*
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityGhostSword extends EntityArrow {

    public EntityGhostSword(World worldIn) {
        super(worldIn);
        this.setDamage(9F);
    }

    public EntityGhostSword(World worldIn, double x, double y, double z, float r, float g, float b) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.setDamage(9F);
    }

    public EntityGhostSword(World worldIn, EntityLivingBase shooter, double dmg) {
        super(worldIn);
        this.setDamage(dmg);
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        noClip = true;
        float sqrt = MathHelper.sqrt((float) (this.motionX * this.motionX + this.motionZ * this.motionZ));
        if ((sqrt < 0.1F) && this.ticksExisted > 200) {
            this.setDead();
        }
        double d0 = 0;
        double d1 = 0.0D;
        double d2 = 0.01D;
        double x = this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width;
        double y = this.posY + this.rand.nextFloat() * this.height - this.height;
        double z = this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width;
        float f = (this.width + this.height + this.width) * 0.333F + 0.5F;
        if (particleDistSq(x, y, z) < f * f) {
            this.world.spawnParticle(ParticleTypes.SNEEZE, x, y + 0.5D, z, d0, d1, d2);
        }
        Vec3d vector3d = this.getDeltaMovement();
        double f3 = vector3d.horizontalDistance();
        this.rotationYaw = ((float) (MathHelper.atan2(vector3d.x, vector3d.z) * (180F / (float) Math.PI)));
        this.rotationPitch = ((float) (MathHelper.atan2(vector3d.y, f3) * (180F / (float) Math.PI)));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        Vec3d vector3d2 = this.getPositionVector();
        Vec3d vector3d3 = vector3d2.add(vector3d);
        RayTraceResult raytraceresult = this.world.clip(new ClipContext(vector3d2, vector3d3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vector3d3 = raytraceresult.getLocation();
        }
        while (!this.isRemoved()) {
            Entity entityraytraceresult = this.findEntityOnPath(vector3d2, vector3d3);
            if (entityraytraceresult != null) {
                raytraceresult.entityHit = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) raytraceresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player) entity1).canHarmPlayer((Player) entity)) {
                    raytraceresult = null;
                    entityraytraceresult = null;
                }
            }

            if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                if (raytraceresult.getType() != HitResult.Type.BLOCK) {
                    this.onHit(raytraceresult);

                }
                this.hasImpulse = true;
            }

            if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
                break;
            }

            raytraceresult = null;
        }
    }


    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (!this.isSilent() && soundIn != SoundEvents.ENTITY_ARROW_HIT && soundIn != SoundEvents.ENTITY_ARROW_HIT_PLAYER) {
            this.world.playSound(null, this.posX, this.posY, this.posZ, soundIn, this.getSoundCategory(), volume, pitch);
        }
    }

    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    private IntOpenHashSet piercedEntities;
    private List<Entity> hitEntities;
    private int knockbackStrength;

    @Override
    public void setKnockback(int knockbackStrengthIn) {
        this.knockbackStrength = knockbackStrengthIn;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.entityHit != null) {
            this.onHitEntity(result);
        } else {
            super.onHit(result);
        }
    }

    protected void onHitEntity(RayTraceResult result) {
        Entity entity = result.entityHit;
        float damage = (float) this.getDamage();
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            damage += EnchantmentHelper.getModifierForCreature(this.getArrowStack(), living.getCreatureAttribute());
            if (entity.isWet()) damage += TridentEnchantments.getImpalingModifier(this.getArrowStack()) * 2.5;
        }
        Entity shooter = this.shootingEntity;
        DamageSource source = EntityTrident.causeTridentDamage(this, shooter == null ? this : shooter);
        this.dealtDamage = true;
        SoundEvent sound = TridentSounds.ITEM_TRIDENT_HIT;
        if (entity.attackEntityFrom(source, damage) && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            if (shooter instanceof EntityLivingBase) {
                EnchantmentHelper.applyThornEnchantments(living, shooter);
                EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) shooter, living);
            }
            this.arrowHit(living);
        }
        this.motionX *= -0.01F;
        this.motionY *= -0.1F;
        this.motionZ *= -0.01F;
        float volume = 1.0F;
        if (this.world.isThundering() && TridentEnchantments.hasChanneling(this.thrownStack)) {
            BlockPos position = entity.getPosition();
            if (this.world.canSeeSky(position)) {
                EntityLightningBolt lightningBolt = new EntityLightningBolt(this.world,
                        position.getX(), position.getY(), position.getZ(), false);
                this.world.addWeatherEffect(lightningBolt);
                this.addLightning(lightningBolt);
                sound = TridentSounds.ITEM_TRIDENT_THUNDER;
                volume = 5.0F;
            }
        }
        this.playSound(sound, volume, 1.0F);
    }


    @Override
    protected void arrowHit(EntityLivingBase entity) {
        float f = (float) this.getDeltaMovement().length();
        int i = MathHelper.ceil(Math.max(f * this.getDamage(), 0.0D));
        /*
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }

            if (this.hitEntities == null) {
                this.hitEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            this.piercedEntities.add(entity.getId());
        }
        if (this.getIsCritical()) {
            i += this.rand.nextInt(i / 2 + 2);
        }

        Entity entity1 = this.shootingEntity;
        DamageSource damagesource = DamageSource.MAGIC;

        if (entity1 != null) {
            if (entity1 instanceof EntityLiving) {
                damagesource = DamageSource.causeArrowDamage(this, entity1);
                damagesource.setMagicDamage();
                ((EntityLiving) entity1).setLastAttackedEntity(entity);
            }
        }

        boolean flag = entity == new EntityEnderman(world);
        int j = entity.getRemainingFireTicks();
        if (this.isBurning() && !flag) {
            entity.setFire(5);
        }

        if (entity.hurt(damagesource, i)) {
            if (flag) {
                return;
            }

            if (this.knockbackStrength > 0) {
                Vec3d vec3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize()
                    .scale(this.knockbackStrength * 0.6D);
                if (vec3d.lengthSqr() > 0.0D) {
                    entity.push(vec3d.x, 0.1D, vec3d.z);
                }
            }

            this.doPostHurtEffects(entity);
            if (entity1 != null && entity != entity1 && entity instanceof EntityPlayer && entity1 instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entity1).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
            }

            if (entity.isDead && this.hitEntities != null) {
                this.hitEntities.add(entity);
            }

            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.remove(RemovalReason.DISCARDED);
            }
        } else {
            this.motionX *= -0.01F;
            this.motionY *= -0.1F;
            this.motionZ *= -0.01F;
            //this.ticksInAir = 0;
            //if (!this.world.isRemote && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            //    this.remove(RemovalReason.DISCARDED);
            //}
        }

    }
}*/