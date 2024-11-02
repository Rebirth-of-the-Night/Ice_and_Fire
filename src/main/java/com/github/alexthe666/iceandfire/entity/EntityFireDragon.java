package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.event.DragonFireEvent;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageDragonSyncFire;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityFireDragon extends EntityDragonBase {

    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    public static final ResourceLocation FEMALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_female"));
    public static final ResourceLocation MALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_male"));
    public static final ResourceLocation SKELETON_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/fire_dragon_skeleton"));

    public EntityFireDragon(World worldIn) {
        super(worldIn, DragonType.FIRE, 1, 1 + IceAndFire.CONFIG.dragonAttackDamage, IceAndFire.CONFIG.dragonHealth * 0.04, IceAndFire.CONFIG.dragonHealth, 0.15F, 0.4F);
        this.setSize(0.78F, 1.2F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.isImmuneToFire = true;
        ANIMATION_SPEAK = Animation.create(20);
        ANIMATION_BITE = Animation.create(35);
        ANIMATION_SHAKEPREY = Animation.create(65);
        ANIMATION_TAILWHACK = Animation.create(40);
        ANIMATION_FIRECHARGE = Animation.create(30);
        ANIMATION_WINGBLAST = Animation.create(50);
        ANIMATION_ROAR = Animation.create(40);
        ANIMATION_EPIC_ROAR = Animation.create(60);
        this.growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, this.aiSit = new EntityAISit(this));
        this.tasks.addTask(2, new EntityAISwimming(this));
        this.tasks.addTask(3, new DragonAIMate(this, 1.0D));
        this.tasks.addTask(4, new DragonAIAttackMelee(this, 1.5D, false));
        this.tasks.addTask(5, new AquaticAITempt(this, 1.0D, IafItemRegistry.fire_stew, false));
        this.tasks.addTask(6, new DragonAIAirTarget(this));
        this.tasks.addTask(7, new DragonAIWander(this, 1.0D));
        this.tasks.addTask(8, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
        this.tasks.addTask(8, new DragonAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new DragonAITarget<>(this, EntityLivingBase.class, true, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof EntityLivingBase && DragonUtils.isAlive((EntityLivingBase) entity) && !EntityFireDragon.this.isControllingPassenger(entity);
            }
        }));
        this.targetTasks.addTask(5, new DragonAITargetItems<>(this, false));
    }

    public void stimulateFire(double burnX, double burnY, double burnZ, int syncType) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireEvent(this, burnX, burnY, burnZ))) return;
        if (syncType == 1 && !world.isRemote) {
            //sync with client
            IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageDragonSyncFire(this.getEntityId(), burnX, burnY, burnZ, 0));
        }
        if (syncType == 2 && world.isRemote) {
            //sync with server
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonSyncFire(this.getEntityId(), burnX, burnY, burnZ, 0));
        }
        if (syncType == 3 && !world.isRemote) {
            //sync with client, fire bomb
            IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageDragonSyncFire(this.getEntityId(), burnX, burnY, burnZ, 5));
        }
        if (syncType == 4 && world.isRemote) {
            //sync with server, fire bomb
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonSyncFire(this.getEntityId(), burnX, burnY, burnZ, 5));
        }
        if (syncType > 2 && syncType < 6) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                rotationYaw = renderYawOffset;
                Vec3d headVec = this.getHeadPosition();
                double d2 = burnX - headVec.x;
                double d3 = burnY - headVec.y;
                double d4 = burnZ - headVec.z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                EntityDragonFireCharge entitylargefireball = new EntityDragonFireCharge(world, this, d2, d3, d4);
                float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                entitylargefireball.setSizes(size, size);
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!world.isRemote) {
                    world.spawnEntity(entitylargefireball);
                }
            }
            return;
        }
        this.getNavigator().clearPath();
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3d headPos = getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(5 * this.getDistance(burnX, burnY, burnZ), 0);
        int increment = (int) Math.ceil(distance / 100);

        double progressX = headPos.x + d2 * (distance / (float) distance);
        double progressY = headPos.y + d3 * (distance / (float) distance);
        double progressZ = headPos.z + d4 * (distance / (float) distance);

        if (canPositionBeSeen(progressX, progressY, progressZ)) {
            if (world.isRemote && rand.nextInt(5) == 0) {
                IceAndFire.PROXY.spawnDragonParticle("dragonfire", headPos.x, headPos.y, headPos.z, 0, 0, 0, this);
            }
        } else {
            if (!world.isRemote) {
                RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ), new Vec3d(progressX, progressY, progressZ), false, true, false);
                BlockPos pos = result.getBlockPos();
                IafDragonDestructionManager.destroyAreaFire(world, pos, this);
            }
        }

        if (canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (rand.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (rand.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (rand.nextFloat() * 3.0) - 1.5;
            if (!world.isRemote) {
                IafDragonDestructionManager.destroyAreaFire(world, new BlockPos(spawnX, spawnY, spawnZ), this);
            }
        }
    }

    public String getVariantName(int variant) {
        switch (variant) {
            default:
                return "red_";
            case 1:
                return "green_";
            case 2:
                return "bronze_";
            case 3:
                return "gray_";
        }
    }

    public Item getVariantScale(int variant) {
        switch (variant) {
            default:
                return IafItemRegistry.dragonscales_red;
            case 1:
                return IafItemRegistry.dragonscales_green;
            case 2:
                return IafItemRegistry.dragonscales_bronze;
            case 3:
                return IafItemRegistry.dragonscales_gray;
        }
    }

    public Item getVariantEgg(int variant) {
        switch (variant) {
            default:
                return IafItemRegistry.dragonegg_red;
            case 1:
                return IafItemRegistry.dragonegg_green;
            case 2:
                return IafItemRegistry.dragonegg_bronze;
            case 3:
                return IafItemRegistry.dragonegg_gray;
        }
    }

    @Override
    public Item getSummoningCrystal() {
        return IafItemRegistry.summoning_crystal_fire;
    }

    public boolean canBeSteered() {
        return true;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == ANIMATION_WINGBLAST) {
            return false;
        }
        switch (new Random().nextInt(4)) {
            case 0:
                if (this.getAnimation() != ANIMATION_BITE) {
                    this.setAnimation(ANIMATION_BITE);
                    return false;
                } else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                    boolean success = this.doBiteAttack(entityIn);
                    this.usingGroundAttack = this.getRNG().nextBoolean();
                    return success;
                }
                break;
            case 1:
                if (new Random().nextInt(2) == 0 && isDirectPathBetweenPoints(this, this.getPositionVector(), entityIn.getPositionVector()) && entityIn.width < this.width * 0.5F && this.getControllingPassenger() == null && this.getDragonStage() > 1 && !(entityIn instanceof EntityDragonBase) && !DragonUtils.isAnimaniaMob(entityIn)) {
                    if (this.getAnimation() != ANIMATION_SHAKEPREY) {
                        this.setAnimation(ANIMATION_SHAKEPREY);
                        entityIn.dismountRidingEntity();
                        entityIn.startRiding(this);
                        this.usingGroundAttack = this.getRNG().nextBoolean();
                        return true;
                    }
                } else {
                    if (this.getAnimation() != ANIMATION_BITE) {
                        this.setAnimation(ANIMATION_BITE);
                        return false;
                    } else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                        boolean success = this.doBiteAttack(entityIn);
                        this.usingGroundAttack = this.getRNG().nextBoolean();
                        return success;
                    }
                }
                break;
            case 2:
                if (this.getAnimation() != ANIMATION_TAILWHACK) {
                    this.setAnimation(ANIMATION_TAILWHACK);
                    return false;
                } else if (this.getAnimationTick() > 20 && this.getAnimationTick() < 30) {
                    boolean success = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
                    if (entityIn instanceof EntityLivingBase) {
                        ((EntityLivingBase) entityIn).knockBack(entityIn, this.getDragonStage() * 0.6F, 1, 1);
                    }
                    this.usingGroundAttack = this.getRNG().nextBoolean();
                    return success;
                }
                break;
            case 3:
                if (this.onGround && !this.isHovering() && !this.isFlying()) {
                    if (this.getAnimation() != ANIMATION_WINGBLAST) {
                        this.setAnimation(ANIMATION_WINGBLAST);
                        return true;
                    }
                } else {
                    if (this.getAnimation() != ANIMATION_BITE) {
                        this.setAnimation(ANIMATION_BITE);
                        return false;
                    } else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                        boolean success = this.doBiteAttack(entityIn);
                        this.usingGroundAttack = this.getRNG().nextBoolean();
                        return success;
                    }
                }

                break;
            default:
                if (this.getAnimation() != ANIMATION_BITE) {
                    this.setAnimation(ANIMATION_BITE);
                    return false;
                } else if (this.getAnimationTick() > 15 && this.getAnimationTick() < 25) {
                    boolean success = this.doBiteAttack(entityIn);
                    this.usingGroundAttack = this.getRNG().nextBoolean();
                    return success;
                }
                break;
        }

        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote) {
            if ((this.isInLava() || isInWater()) && !this.isFlying() && !this.isChild() && !this.isHovering() && this.canMove()) {
                this.setHovering(true);
                this.flyTicks = 0;
            }
            if (this.getAttackTarget() != null && !this.isSleeping() && this.getAnimation() != ANIMATION_SHAKEPREY) {
                if ((!usingGroundAttack || this.isFlying()) && !this.isInWater() && !this.isInLava() && !isTargetBlocked(new Vec3d(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ))) {
                    shootFireAtMob(this.getAttackTarget());
                } else {
                    if (this.getEntityBoundingBox().grow(this.getRenderSize() * 0.5F, this.getRenderSize() * 0.5F, this.getRenderSize() * 0.5F).intersects(this.getAttackTarget().getEntityBoundingBox())) {
                        attackEntityAsMob(this.getAttackTarget());
                    }
                }
            }else {
                this.setBreathingFire(this.burningTarget != null);
            }
        }
    }

    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                rotationYaw = renderYawOffset;
                if (this.ticksExisted % 5 == 0) {
                    this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                }
                stimulateFire(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, 1);
            }
        } else {
            this.setBreathingFire(true);
        }
    }

    public void riderShootFire(Entity controller) {
        if (this.getRNG().nextInt(5) == 0 && !this.isChild()) {
            if (this.getAnimation() != this.ANIMATION_FIRECHARGE) {
                this.setAnimation(this.ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                rotationYaw = renderYawOffset;
                Vec3d headPos = getHeadPosition();
                this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                double d2 = controller.getLookVec().x;
                double d3 = controller.getLookVec().y;
                double d4 = controller.getLookVec().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                EntityDragonFireCharge fireChargeProjectile = new EntityDragonFireCharge(world, this, d2, d3, d4);
                float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                fireChargeProjectile.setSizes(size, size);
                fireChargeProjectile.setPosition(headPos.x, headPos.y, headPos.z);
                if (!world.isRemote) {
                    world.spawnEntity(fireChargeProjectile);
                }
            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    rotationYaw = renderYawOffset;
                    if (this.ticksExisted % 5 == 0) {
                        this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                    }
                    RayTraceResult mop = rayTraceRider(controller, 10 * this.getDragonStage(), 1.0F);
                    if (mop != null) {
                        stimulateFire(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z, 1);
                    }
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    @Override
    public ResourceLocation getDeadLootTable() {
        if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
            return SKELETON_LOOT;
        } else {
            return isMale() ? MALE_LOOT : FEMALE_LOOT;
        }
    }

    private void shootFireAtMob(EntityLivingBase entity) {
        if (!this.usingGroundAttack) {
            if (this.getRNG().nextInt(5) == 0) {
                if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                    this.setAnimation(ANIMATION_FIRECHARGE);
                } else if (this.getAnimationTick() == 20) {
                    rotationYaw = renderYawOffset;
                    Vec3d headPos = getHeadPosition();
                    double d2 = entity.posX - headPos.x;
                    double d3 = entity.posY - headPos.y;
                    double d4 = entity.posZ - headPos.z;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                    EntityDragonFireCharge fireChargeProjectile = new EntityDragonFireCharge(world, this, d2, d3, d4);
                    float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                    fireChargeProjectile.setSizes(size, size);
                    fireChargeProjectile.setPosition(headPos.x, headPos.y, headPos.z);
                    if (!world.isRemote) {
                        world.spawnEntity(fireChargeProjectile);
                    }
                    if (entity.isDead) {
                        this.setBreathingFire(false);
                        this.usingGroundAttack = this.getRNG().nextBoolean();
                    }
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        rotationYaw = renderYawOffset;
                        if (this.ticksExisted % 5 == 0) {
                            this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                        }
                        stimulateFire(entity.posX, entity.posY, entity.posZ, 1);
                        if (entity.isDead) {
                            this.setBreathingFire(false);
                            this.usingGroundAttack = this.getRNG().nextBoolean();
                        }
                    }
                } else {
                    this.setBreathingFire(true);
                }
            }
        }
        this.faceEntity(entity, 360, 360);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTeen() ? IafSoundRegistry.FIREDRAGON_TEEN_IDLE : this.isAdult() ? IafSoundRegistry.FIREDRAGON_ADULT_IDLE : IafSoundRegistry.FIREDRAGON_CHILD_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return this.isTeen() ? IafSoundRegistry.FIREDRAGON_TEEN_HURT : this.isAdult() ? IafSoundRegistry.FIREDRAGON_ADULT_HURT : IafSoundRegistry.FIREDRAGON_CHILD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSoundRegistry.FIREDRAGON_TEEN_DEATH : this.isAdult() ? IafSoundRegistry.FIREDRAGON_ADULT_DEATH : IafSoundRegistry.FIREDRAGON_CHILD_DEATH;
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSoundRegistry.FIREDRAGON_TEEN_ROAR : this.isAdult() ? IafSoundRegistry.FIREDRAGON_ADULT_ROAR : IafSoundRegistry.FIREDRAGON_CHILD_ROAR;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT, EntityDragonBase.ANIMATION_SPEAK, EntityDragonBase.ANIMATION_BITE, EntityDragonBase.ANIMATION_SHAKEPREY, EntityFireDragon.ANIMATION_TAILWHACK, EntityFireDragon.ANIMATION_FIRECHARGE, EntityFireDragon.ANIMATION_WINGBLAST, EntityFireDragon.ANIMATION_ROAR, EntityFireDragon.ANIMATION_EPIC_ROAR};
    }

    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItemRegistry.fire_stew;
    }

    protected void spawnDeathParticles() {
        for (int k = 0; k < 3; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            if (world.isRemote) {
                this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
            }
        }
    }

    protected void spawnBabyParticles() {
        for (int i = 0; i < 5; i++) {
            float radiusAdd = i * 0.15F;
            float headPosX = (float) (posX + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.cos((rotationYaw + 90) * Math.PI / 180));
            float headPosZ = (float) (posZ + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.sin((rotationYaw + 90) * Math.PI / 180));
            float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, headPosX, headPosY, headPosZ, 0, 0, 0);
        }
    }

    protected ItemStack getSkull() {
        return new ItemStack(IafItemRegistry.dragon_skull);
    }

    @Override
    protected Item getHeartItem() {
        return IafItemRegistry.fire_dragon_heart;
    }

    @Override
    protected Item getBloodItem() {
        return IafItemRegistry.fire_dragon_blood;
    }
}
