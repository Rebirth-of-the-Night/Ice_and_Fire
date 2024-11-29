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
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class EntityLightningDragon extends EntityDragonBase {

    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    public static final ResourceLocation FEMALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/lightning_dragon_female"));
    public static final ResourceLocation MALE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/lightning_dragon_male"));
    public static final ResourceLocation SKELETON_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dragon/lightning_dragon_skeleton"));
    private static final DataParameter<Boolean> HAS_LIGHTNING_TARGET = EntityDataManager.createKey(EntityLightningDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> LIGHTNING_TARGET_X = EntityDataManager.createKey(EntityLightningDragon.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LIGHTNING_TARGET_Y = EntityDataManager.createKey(EntityLightningDragon.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LIGHTNING_TARGET_Z = EntityDataManager.createKey(EntityLightningDragon.class, DataSerializers.FLOAT);

    public EntityLightningDragon(World worldIn) {
        super(worldIn, DragonType.LIGHTNING, 1, 1 + IceAndFire.CONFIG.dragonAttackDamage, IceAndFire.CONFIG.dragonHealth * 0.04, IceAndFire.CONFIG.dragonHealth, 0.15F, 0.4F);
        this.setSize(0.78F, 1.2F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.LAVA, 8.0F);
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
    public void onStruckByLightning(@Nonnull EntityLightningBolt lightningBolt) {
        this.heal(IceAndFire.CONFIG.lightningDragonHealAmount);
        this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20, 1));
    }

    @Override
    public boolean isEntityInvulnerable(@Nonnull DamageSource i) {
        return super.isEntityInvulnerable(i) || i == DamageSource.LIGHTNING_BOLT || i == IceAndFire.dragonLightning;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HAS_LIGHTNING_TARGET, false);
        this.dataManager.register(LIGHTNING_TARGET_X, 0.0F);
        this.dataManager.register(LIGHTNING_TARGET_Y, 0.0F);
        this.dataManager.register(LIGHTNING_TARGET_Z, 0.0F);
    }

    public boolean isDaytime() {
        return !this.world.isDaytime();
    }

    public String getVariantName(int variant) {
        switch (variant) {
            default:
                return "electric_";
            case 1:
                return "amethyst_";
            case 2:
                return "copper_";
            case 3:
                return "black_";
        }
    }

    public Item getVariantScale(int variant) {
        switch (variant) {
            default:
                return IafItemRegistry.dragonscales_electric;
            case 1:
                return IafItemRegistry.dragonscales_amethyst;
            case 2:
                return IafItemRegistry.dragonscales_copper;
            case 3:
                return IafItemRegistry.dragonscales_black;
        }
    }

    public Item getVariantEgg(int variant) {
        switch (variant) {
            default:
                return IafItemRegistry.dragonegg_electric;
            case 1:
                return IafItemRegistry.dragonegg_amethyst;
            case 2:
                return IafItemRegistry.dragonegg_copper;
            case 3:
                return IafItemRegistry.dragonegg_black;
        }
    }

    public void setHasLightningTarget(boolean lightning_target) {
        this.dataManager.set(HAS_LIGHTNING_TARGET, lightning_target);
    }

    public boolean hasLightningTarget() {
        return this.dataManager.get(HAS_LIGHTNING_TARGET);
    }

    public void setLightningTargetVec(float x, float y, float z) {
        this.dataManager.set(LIGHTNING_TARGET_X, x);
        this.dataManager.set(LIGHTNING_TARGET_Y, y);
        this.dataManager.set(LIGHTNING_TARGET_Z, z);
    }

    public float getLightningTargetX() {
        return this.dataManager.get(LIGHTNING_TARGET_X);
    }

    public float getLightningTargetY() {
        return this.dataManager.get(LIGHTNING_TARGET_Y);
    }

    public float getLightningTargetZ() {
        return this.dataManager.get(LIGHTNING_TARGET_Z);
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
                if (this.isInLava()) {
                    this.jump();
                    this.motionY += 0.8D;
                }
                this.flyTicks = 0;
            }
            if (this.getAttackTarget() != null && !this.isSleeping() && this.getAnimation() != ANIMATION_SHAKEPREY) {
                if ((!usingGroundAttack || this.isFlying()) && !this.isInWater() && !this.isInLava() && !isTargetBlocked(new Vec3d(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ))) {
                    shootLightningAtMob(this.getAttackTarget());
                } else {
                    if (this.getEntityBoundingBox().grow(this.getRenderSize() * 0.5F, this.getRenderSize() * 0.5F, this.getRenderSize() * 0.5F).intersects(this.getAttackTarget().getEntityBoundingBox())) {
                        attackEntityAsMob(this.getAttackTarget());
                    }

                }
            } else {
                this.setBreathingFire(this.burningTarget != null);
            }
        }
        if (!isBreathingFire()) {
            this.setHasLightningTarget(false);
        }
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
                this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH_CRACKLE, 4, 1);
                EntityDragonLightningCharge entitylargefireball = new EntityDragonLightningCharge(world, this, d2, d3, d4);
                float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                entitylargefireball.setSizes(size, size);
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!world.isRemote) {
                    world.spawnEntity(entitylargefireball);
                }
            }
            return;
        }
        this.burnParticleX = burnX;
        this.burnParticleY = burnY;
        this.burnParticleZ = burnZ;
        Vec3d headPos = this.getHeadPosition();
        double d2 = burnX - headPos.x;
        double d3 = burnY - headPos.y;
        double d4 = burnZ - headPos.z;
        double distance = Math.max(2.5F * this.getDistance(burnX, burnY, burnZ), 0);

        int increment = (int) Math.ceil(distance / 100);
        for (int i = 0; i < distance; i += increment) {
            double progressX = headPos.x + d2 * (i / (float) distance);
            double progressY = headPos.y + d3 * (i / (float) distance);
            double progressZ = headPos.z + d4 * (i / (float) distance);
            if (canPositionBeSeen(progressX, progressY, progressZ)) {
                setHasLightningTarget(true);
                setLightningTargetVec((float) burnX, (float) burnY, (float) burnZ);
            } else {
                if (!world.isRemote) {
                    RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ), new Vec3d(progressX, progressY, progressZ), false, true, false);
                    if (result == null) return;
                    BlockPos pos = result.getBlockPos();
                    IafDragonDestructionManager.destroyAreaLightning(world, pos, this);
                    setHasLightningTarget(true);
                    setLightningTargetVec((float) result.getBlockPos().getX(), (float) result.getBlockPos().getY(), (float) result.getBlockPos().getZ());
                }
            }
        }
        if (canPositionBeSeen(burnX, burnY, burnZ)) {
            double spawnX = burnX + (rand.nextFloat() * 3.0) - 1.5;
            double spawnY = burnY + (rand.nextFloat() * 3.0) - 1.5;
            double spawnZ = burnZ + (rand.nextFloat() * 3.0) - 1.5;
            setHasLightningTarget(true);
            setLightningTargetVec((float) spawnX, (float) spawnY, (float) spawnZ);
            if (!world.isRemote) {
                IafDragonDestructionManager.destroyAreaLightning(world, new BlockPos(spawnX, spawnY, spawnZ), this);
            }
        }
    }

    @Override
    protected void breathFireAtPos(BlockPos burningTarget) {
        if (this.isBreathingFire()) {
            if (this.isActuallyBreathingFire()) {
                rotationYaw = renderYawOffset;
                if (this.fireTicks % 7 == 0) {
                    this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH, 4, 1);
                }
                stimulateFire(burningTarget.getX() + 0.5F, burningTarget.getY() + 0.5F, burningTarget.getZ() + 0.5F, 1);
            }
        } else {
            this.setBreathingFire(true);
        }
    }

    public void riderShootFire(Entity controller) {
        if (this.getRNG().nextInt(5) == 0 && !this.isChild()) {
            if (this.getAnimation() != ANIMATION_FIRECHARGE) {
                this.setAnimation(ANIMATION_FIRECHARGE);
            } else if (this.getAnimationTick() == 20) {
                rotationYaw = renderYawOffset;
                Vec3d headVec = this.getHeadPosition();
                this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH_CRACKLE, 4, 1);
                double d2 = controller.getLookVec().x;
                double d3 = controller.getLookVec().y;
                double d4 = controller.getLookVec().z;
                float inaccuracy = 1.0F;
                d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                EntityDragonLightningCharge entitylargefireball = new EntityDragonLightningCharge(world, this, d2, d3, d4);
                float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                entitylargefireball.setSizes(size, size);
                entitylargefireball.setPosition(headVec.x, headVec.y, headVec.z);
                if (!world.isRemote) {
                    world.spawnEntity(entitylargefireball);
                }
            }
        } else {
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    rotationYaw = renderYawOffset;
                    if (this.fireTicks % 7 == 0) {
                        this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH, 4, 1);
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
    protected void initEntityAI() {
        this.tasks.addTask(1, this.aiSit = new EntityAISit(this));
        this.tasks.addTask(2, new DragonAISwim(this));
        this.tasks.addTask(2, new DragonAIMate(this, 1.0D));
        this.tasks.addTask(3, new DragonAIAttackMelee(this, 1.5D, false));
        this.tasks.addTask(4, new AquaticAITempt(this, 1.0D, IafItemRegistry.lightning_stew, false));
        this.tasks.addTask(5, new DragonAIAirTarget(this));
        this.tasks.addTask(6, new DragonAIWander(this, 1.0D));
        this.tasks.addTask(7, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
        this.tasks.addTask(7, new DragonAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new DragonAITarget<>(this, EntityLivingBase.class, true, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof EntityLivingBase && DragonUtils.isAlive((EntityLivingBase) entity) && !EntityLightningDragon.this.isControllingPassenger(entity);
            }
        }));
        this.targetTasks.addTask(5, new DragonAITargetItems<>(this, false));
    }

    @Override
    public ResourceLocation getDeadLootTable() {
        if (this.getDeathStage() >= (this.getAgeInDays() / 5) / 2) {
            return SKELETON_LOOT;
        } else {
            return isMale() ? MALE_LOOT : FEMALE_LOOT;
        }
    }

    private void shootLightningAtMob(EntityLivingBase entity) {
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
                    this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH, 4, 1);
                    EntityDragonLightningCharge lightningChargeProjectile = new EntityDragonLightningCharge(world, this, d2, d3, d4);
                    float size = this.isChild() ? 0.4F : this.isAdult() ? 1.3F : 0.8F;
                    lightningChargeProjectile.setSizes(size, size);
                    lightningChargeProjectile.setPosition(headPos.x, headPos.y, headPos.z);
                    if (!world.isRemote) {
                        world.spawnEntity(lightningChargeProjectile);
                    }
                    if (entity.isDead) {
                        this.setBreathingFire(false);
                        this.usingGroundAttack = true;
                    }
                }
            } else {
                if (this.isBreathingFire()) {
                    if (this.isActuallyBreathingFire()) {
                        rotationYaw = renderYawOffset;
                        if (this.fireTicks % 7 == 0) {
                            this.playSound(IafSoundRegistry.LIGHTNINGDRAGON_BREATH, 4, 1);
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
        return this.isTeen() ? IafSoundRegistry.LIGHTNINGDRAGON_TEEN_IDLE : this.isAdult() ? IafSoundRegistry.LIGHTNINGDRAGON_ADULT_IDLE : IafSoundRegistry.LIGHTNINGDRAGON_CHILD_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource p_184601_1_) {
        return this.isTeen() ? IafSoundRegistry.LIGHTNINGDRAGON_TEEN_HURT : this.isAdult() ? IafSoundRegistry.LIGHTNINGDRAGON_ADULT_HURT : IafSoundRegistry.LIGHTNINGDRAGON_CHILD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTeen() ? IafSoundRegistry.LIGHTNINGDRAGON_TEEN_DEATH : this.isAdult() ? IafSoundRegistry.LIGHTNINGDRAGON_ADULT_DEATH : IafSoundRegistry.LIGHTNINGDRAGON_CHILD_DEATH;
    }

    @Override
    public SoundEvent getRoarSound() {
        return this.isTeen() ? IafSoundRegistry.LIGHTNINGDRAGON_TEEN_ROAR : this.isAdult() ? IafSoundRegistry.LIGHTNINGDRAGON_ADULT_ROAR : IafSoundRegistry.LIGHTNINGDRAGON_CHILD_ROAR;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT, EntityDragonBase.ANIMATION_SPEAK, EntityDragonBase.ANIMATION_BITE, EntityDragonBase.ANIMATION_SHAKEPREY, EntityLightningDragon.ANIMATION_TAILWHACK, EntityLightningDragon.ANIMATION_FIRECHARGE, EntityLightningDragon.ANIMATION_WINGBLAST, EntityLightningDragon.ANIMATION_ROAR, EntityLightningDragon.ANIMATION_EPIC_ROAR};
    }

    public boolean isBreedingItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItemRegistry.lightning_stew;
    }

    protected void spawnDeathParticles() {
        for (int k = 0; k < 3; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            if (world.isRemote) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
            }
        }
    }

    protected void spawnBabyParticles() {
        for (int i = 0; i < 5; i++) {
            float radiusAdd = i * 0.15F;
            float headPosX = (float) (posX + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.cos((rotationYaw + 90) * Math.PI / 180));
            float headPosZ = (float) (posZ + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.sin((rotationYaw + 90) * Math.PI / 180));
            float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, headPosX, headPosY, headPosZ, 0, 0, 0);
        }
    }

    /*Code by Kotlin-Programmer*/
    @Override
    public Vec3d getHeadPosition() {
        float sitProg = this.sitProgress * 0.005F;
        float deadProg = this.modelDeadProgress * -0.02F;
        float hoverProg = this.hoverProgress * 0.03F;
        float flyProg = Math.max(0, this.flyProgress * 0.01F);
        int tick;
        if (this.getAnimationTick() < 10) {
            tick = this.getAnimationTick();
        } else if (this.getAnimationTick() > 50) {
            tick = 60 - this.getAnimationTick();
        } else {
            tick = 10;
        }
        float epicRoarProg = this.getAnimation() == ANIMATION_EPIC_ROAR ? tick * 0.1F : 0;
        float sleepProg = this.sleepProgress * 0.025F;
        float pitchY = 0;
        float dragonPitch = -getDragonPitch();// -90 = down, 0 = straight, 90 = up
        if (this.isFlying() || this.isHovering()) {
            if (dragonPitch > 0) {
                pitchY = (dragonPitch / 90F) * 1.2F;
            } else {
                pitchY = (dragonPitch / 90F) * 3F;
            }
        }
        float flightXz = 1.0F + flyProg + hoverProg;
        float absPitch = Math.abs(dragonPitch) / 90F;//1 down/up, 0 straight
        float minXZ = dragonPitch > 20 ? (dragonPitch - 20) * 0.009F : 0;
        float xzMod = (0.58F - hoverProg * 0.45F + flyProg * 0.2F + absPitch * 0.3F - sitProg - sleepProg * 0.9F) * flightXz * getRenderSize();
        double xzModSine = xzMod * (Math.max(0.25F, Math.cos((float) Math.toRadians(dragonPitch))) - minXZ);
        float xzSleepMod = -1.25F * sleepProg * getRenderSize();
        float headPosX = (float) (posX + (xzModSine) * Math.cos((float) ((rotationYaw + 90) * Math.PI / 180)) + xzSleepMod * Math.cos(rotationYaw * Math.PI / 180));
        float headPosY = (float) (posY + (0.7F + (sitProg * 5F) + hoverProg + deadProg + epicRoarProg + sleepProg + flyProg + pitchY) * getRenderSize() * 0.3F);
        float headPosZ = (float) (posZ + (xzModSine) * Math.sin((float) ((rotationYaw + 90) * Math.PI / 180)) + xzSleepMod * Math.sin(rotationYaw * Math.PI / 180));
        return new Vec3d(headPosX, headPosY, headPosZ);
    }

    public int getStartMetaForType() {
        return 8;
    }

    @Override
    public Item getSummoningCrystal() {
        return IafItemRegistry.summoning_crystal_lightning;
    }

    protected ItemStack getSkull() {
        return new ItemStack(IafItemRegistry.dragon_skull, 1, 2);
    }

    @Override
    protected ItemStack getHorn() {
        return new ItemStack(IafItemRegistry.dragon_horn_lightning);
    }

    @Override
    protected Item getHeartItem() {
        return IafItemRegistry.lightning_dragon_heart;
    }

    @Override
    protected Item getBloodItem() {
        return IafItemRegistry.lightning_dragon_blood;
    }
}
