package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.util.EntityUtil;
import com.google.common.base.Optional;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityBlackFrostDragon extends EntityIceDragon implements IDreadMob, IBlacklistedFromStatues {

    protected static final DataParameter<Optional<UUID>> COMMANDER_UNIQUE_ID = EntityDataManager.createKey(EntityBlackFrostDragon.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityBlackFrostDragon.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_PHRASE_ONE = EntityDataManager.createKey(EntityBlackFrostDragon.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> LOOK = EntityDataManager.createKey(EntityBlackFrostDragon.class, DataSerializers.FLOAT);
    protected static final DataParameter<Boolean> AWAKEN = EntityDataManager.createKey(EntityBlackFrostDragon.class, DataSerializers.BOOLEAN);

    public BlockPos spawnPointPos;
    public int leapingTick;

    public EntityBlackFrostDragon(World worldIn) {
        super(worldIn);
        this.maximumArmor = 70D;
    }

    public void setPitch(Vec3d look) {
        float prevLook = this.getPitch();
        float newLook = (float) EntityUtil.toPitch(look);
        float deltaLook = 5;
        float clampedLook = MathHelper.clamp(newLook, prevLook - deltaLook, prevLook + deltaLook / 2);
        this.dataManager.set(LOOK, clampedLook);
    }

    public float getPitch() {
        return this.dataManager == null ? 0 : this.dataManager.get(LOOK);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(COMMANDER_UNIQUE_ID, Optional.absent());
        this.dataManager.register(IS_LEAPING, false);
        this.dataManager.register(IS_PHRASE_ONE, false);
        this.dataManager.register(AWAKEN, false);
        this.dataManager.register(LOOK, 0f);
    }

    @Override
    public void onLivingUpdate() {
        EntityDreadQueen queen = this.getRidingQueen();
        if (this.isPhraseOne()) {
            if (this.canMove()
                    && !this.isHovering()
                    && !this.isFlying()
                    && !this.isChild()) {
                this.setHovering(true);
                this.setSleeping(false);
                this.setSitting(false);
                this.hoverTicks = 0;
                this.flyTicks = 0;
            }
            if (queen != null && queen.getAttackTarget() != null)
                this.setAttackTarget(queen.getAttackTarget());
        } else if (!this.isModelDead()) {
            this.setSleeping(true);
            this.setHovering(false);
            this.setFlying(false);
            this.setSwimming(false);
        }

        super.onLivingUpdate();
        this.stepHeight = this.getDragonStage() * 0.5F;
        if (!world.isRemote) {
            if ((int) this.prevPosX == (int) this.posX && (int) this.prevPosZ == (int) this.posZ) {
                this.ticksStill++;
            } else {
                ticksStill = 0;
            }
            if (this.getDragonStage() >= 3 && isStuck() && this.world.getGameRules().getBoolean("mobGriefing") && IceAndFire.CONFIG.dragonGriefing != 2) {
                if (this.getAnimation() == NO_ANIMATION && this.ticksExisted % 5 == 0) {
                    this.setAnimation(ANIMATION_TAILWHACK);
                }
                if (this.getAnimation() == ANIMATION_TAILWHACK && this.getAnimationTick() == 10) {
                    BlockBreakExplosion explosion = new BlockBreakExplosion(world, this, this.posX, this.posY, this.posZ, (4) * this.getDragonStage() - 2);
                    explosion.doExplosionA();
                    explosion.doExplosionB(true);
                    this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);
                }
            }
        }
        if (this.walkCycle < 39) {
            this.walkCycle++;
        } else {
            this.walkCycle = 0;
        }
        if (this.getAnimation() == ANIMATION_WINGBLAST && (this.getAnimationTick() == 17 || this.getAnimationTick() == 22 || this.getAnimationTick() == 28)) {
            this.spawnGroundEffects();
            if (!this.world.isRemote && this.getAttackTarget() != null) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()) / 4);
                this.getAttackTarget().knockBack(this.getAttackTarget(), this.getDragonStage() * 0.6F, 1, 1);
                this.usingGroundAttack = this.getRNG().nextBoolean();
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.legSolver.update(this);
        if ((this.isFlying() || this.isHovering()) && !this.isModelDead()) {
            if (flightCycle < 58) {
                flightCycle += 2;
            } else {
                flightCycle = 0;
            }
            if (flightCycle == 2) {
                this.playSound(IafSoundRegistry.DRAGON_FLIGHT, this.getSoundVolume() * IceAndFire.CONFIG.dragonFlapNoiseDistance, getSoundPitch());
            }
        } else if (this.isModelDead()) {
            flightCycle = 0;
        }

        boolean sitting = isSitting() && !isModelDead() && !isSleeping() && !isHovering() && !isFlying();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }
        boolean sleeping = isSleeping() && !isHovering() && !isFlying();
        if (sleeping && sleepProgress < 20.0F) {
            sleepProgress += 0.5F;
        } else if (!sleeping && sleepProgress > 0.0F) {
            sleepProgress -= 0.5F;
        }
        boolean fireBreathing = isBreathingFire();
        prevFireBreathProgress = fireBreathProgress;
        if (fireBreathing && fireBreathProgress < 5.0F) {
            fireBreathProgress += 0.5F;
        } else if (!fireBreathing && fireBreathProgress > 0.0F) {
            fireBreathProgress -= 0.5F;
        }
        boolean hovering = isHovering();
        if (hovering && hoverProgress < 20.0F) {
            hoverProgress += 0.5F;
        } else if (!hovering && hoverProgress > 0.0F) {
            hoverProgress -= 0.5F;
        }
        boolean tackling = isTackling();
        if (tackling && tackleProgress < 5F) {
            tackleProgress += 0.5F;
        } else if (!tackling && tackleProgress > 0.0F) {
            tackleProgress -= 1.5F;
        }
        boolean flying = !tackling && this.isFlying() || !this.onGround && !this.isHovering() && this.airTarget != null;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 0.5F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 0.5F;
        }
        boolean isModelDead = isModelDead();
        if (isModelDead && modelDeadProgress < 20.0F) {
            modelDeadProgress += 0.5F;
        } else if (!isModelDead && modelDeadProgress > 0.0F) {
            modelDeadProgress -= 0.5F;
        }
        boolean riding = isRiding() && this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer;
        if (riding && ridingProgress < 20.0F) {
            ridingProgress += 0.5F;
        } else if (!riding && ridingProgress > 0.0F) {
            ridingProgress -= 0.5F;
        }
        if (this.isModelDead()) {
            return;
        }
        if (!this.world.isRemote) {
            if (this.isBreathingFire()) {
                this.fireTicks++;
                if (this.fireTicks > this.getDragonStage() * 25 || this.fireStopTicks <= 0 && this.isPlayerControlled()) {
                    this.setBreathingFire(false);
                    this.usingGroundAttack = this.getRNG().nextBoolean();
                    this.fireTicks = 0;
                }
                if (this.fireStopTicks > 0 && this.isPlayerControlled()) {
                    this.fireStopTicks--;
                }
            }
            if (this.isFlying() && this.getAttackTarget() != null && this.getEntityBoundingBox().expand(3.0F, 3.0F, 3.0F).intersects(this.getAttackTarget().getEntityBoundingBox())) {
                this.attackEntityAsMob(this.getAttackTarget());
            }
            this.breakBlock();
        }
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new DreadAIDragonWaitForQueen(this));
    }

    public void doRoboty() {
        //this.tasks.addTask(1, new BlackFrostAILeap(this));
        //this.tasks.addTask(2, new DragonAIAttackMelee(this, 1.5D, false));
        //this.tasks.addTask(3, new AquaticAITempt(this, 1.0D, IafItemRegistry.frost_stew, false));
        //this.tasks.addTask(4, new DragonAIAirTarget(this));
        //this.tasks.addTask(4, new DragonAIWaterTarget(this));
        //this.tasks.addTask(5, new DragonAIWander(this, 1.0D));
        //this.tasks.addTask(6, new DragonAIWatchClosest(this, EntityLivingBase.class, 6.0F));
        //this.tasks.addTask(6, new DragonAILookIdle(this));
        this.tasks.addTask(1, new AIBlackFrostPassiveCircle<>(this, 55));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new DreadAITargetNonDread(this, EntityLivingBase.class, false, DragonUtils::canHostilesTarget));
        this.targetTasks.addTask(5, new DragonAITargetItems<>(this, false));
    }

    /*@Nullable
    public Entity getControllingPassenger() {
        Entity commander = getCommander();
        if (commander != null) {
            for (Entity passenger : this.getPassengers()) {
                if (passenger.getUniqueID().equals(commander.getUniqueID())) {
                    return passenger;
                }
            }
        }
        return super.getControllingPassenger();
    }*/

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isFlying() && this.isLeaping()) {
            this.motionY += 0.1;
            this.leapingTick++;
        }
        if (this.leapingTick >= 30) {
            this.setLeaping(false);
            this.setPhraseOne(true);
        }
        if (this.isPhraseOne()) {
            //Phrase One AI
        }
    }

    public boolean hasNoGravity() {
        return true;
    }

    public boolean isPlayerControlled() {
        return false;
    }

    @Override
    public boolean canBeSteered() {
        return false;
    }

    public boolean canMove() {
        return !this.isSitting() && !this.isSleeping() && !this.isModelDead() && sleepProgress == 0 && this.getAnimation() != ANIMATION_SHAKEPREY;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            passenger.setPosition(this.posX, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ);
        }
        if (this.isPassenger(passenger)) {
            if (!(passenger instanceof EntityDreadQueen)) {
                updatePreyInMouth(passenger);
            } else {
                if (this.isModelDead()) {
                    passenger.dismountRidingEntity();
                }
                float speed_walk = 0.2F;
                float speed_idle = 0.05F;
                float speed_fly = 0.2F;
                float degree_walk = 0.5F;
                float degree_idle = 0.5F;
                float degree_fly = 0.5F;
                if (passenger instanceof EntityPlayer) {
                    this.renderYawOffset = this.rotationYaw;
                    this.rotationYaw = passenger.rotationYaw;
                }
                float hoverAddition = hoverProgress * -0.001F;
                float flyAddition = flyProgress * -0.0001F;
                float flyBody = Math.max(flyProgress, hoverProgress) * 0.0065F;
                float radius = 0.75F * ((0.3F - flyBody) * getRenderSize()) + ((this.getRenderSize() / 3) * flyAddition * 0.0065F);
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraZ = radius * MathHelper.cos(angle);
                float bob0 = this.isFlying() || this.isHovering() ? (hoverProgress > 0 || flyProgress > 0 ? this.bob(-speed_fly, degree_fly * 5, false, this.ticksExisted, -0.0625F) : 0) : 0;
                float bob1 = this.bob(speed_walk * 2, degree_walk * 1.7F, false, this.limbSwing, this.limbSwingAmount * -0.0625F);
                float bob2 = this.bob(speed_idle, degree_idle * 1.3F, false, this.ticksExisted, -0.0625F);
                float extraAgeScale = (Math.max(0, this.getAgeInDays() - 75) / 75F) * 1.65F;

                double extraY_pre = 0.8F;
                double extraY = ((extraY_pre - (hoverAddition) + (flyAddition)) * (this.getRenderSize() / 3)) - (0.35D * (1 - (this.getRenderSize() / 30))) + bob0 + bob1 + bob2 + extraAgeScale;

                passenger.setPosition(this.posX, this.posY + extraY, this.posZ);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.getCommanderId() == null) {
            compound.setString("CommanderUUID", "");
        } else {
            compound.setString("CommanderUUID", this.getCommanderId().toString());
        }
        compound.setBoolean("isLeaping", this.isLeaping());
        compound.setInteger("tickLeaping", this.leapingTick);
        compound.setBoolean("phrase_one", this.isPhraseOne());
        compound.setFloat("Look", this.getPitch());
        compound.setFloat("spawnPointPosX", this.spawnPointPos.getX());
        compound.setFloat("spawnPointPosY", this.spawnPointPos.getY());
        compound.setFloat("spawnPointPosZ", this.spawnPointPos.getZ());
        compound.setBoolean("isAwaken", this.isAwaken());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        String s;
        if (compound.hasKey("CommanderUUID", 8)) {
            s = compound.getString("CommanderUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }
        if (!s.isEmpty()) {
            try {
                this.setCommanderId(UUID.fromString(s));
            } catch (Throwable var4) {
            }
        }
        this.setLeaping(compound.getBoolean("isLeaping"));
        this.leapingTick = compound.getInteger("tickLeaping");
        this.setPhraseOne(compound.getBoolean("phrase_one"));
        this.dataManager.set(LOOK, compound.getFloat("Look"));
        this.spawnPointPos = new BlockPos(compound.getFloat("spawnPointPosX"), compound.getFloat("spawnPointPosY"), compound.getFloat("spawnPointPosZ"));
        this.dataManager.set(AWAKEN, compound.getBoolean("isAwaken"));
    }

    public BlockPos getSpawnPointPos() {
        return this.spawnPointPos;
    }

    public void setSpawnPointPos(BlockPos pos) {
        this.spawnPointPos = pos;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isOnSameTeam(entityIn);
    }

    public boolean shouldRiderSit() {
        return this.getControllingPassenger() != null || getRidingQueen() != null;
    }

    public EntityDreadQueen getRidingQueen() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof EntityDreadQueen) {
                return (EntityDreadQueen) passenger;
            }
        }
        return null;
    }

    @Nullable
    public UUID getCommanderId() {
        return (UUID) ((Optional<?>) this.dataManager.get(COMMANDER_UNIQUE_ID)).orNull();
    }

    public void setCommanderId(@Nullable UUID uuid) {
        this.dataManager.set(COMMANDER_UNIQUE_ID, Optional.fromNullable(uuid));
    }

    public boolean isLeaping() {
        return this.dataManager.get(IS_LEAPING);
    }

    public void setLeaping(Boolean leap) {
        this.dataManager.set(IS_LEAPING, leap);
    }

    public boolean isPhraseOne() {
        return this.dataManager.get(IS_PHRASE_ONE);
    }

    public void setPhraseOne(Boolean phrase) {
        this.dataManager.set(IS_PHRASE_ONE, phrase);
    }

    public boolean isAwaken() {
        return this.dataManager.get(AWAKEN);
    }

    public void setAwaken(Boolean active) {
        this.dataManager.set(AWAKEN, active);
    }

    @Override
    public Entity getCommander() {
        try {
            UUID uuid = this.getCommanderId();
            EntityLivingBase player = uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
            if (player != null) {
                return player;
            } else {
                if (!world.isRemote) {
                    Entity entity = world.getMinecraftServer().getWorld(this.dimension).getEntityFromUuid(uuid);
                    if (entity instanceof EntityLivingBase) {
                        return entity;
                    }
                }
            }
        } catch (IllegalArgumentException var2) {
            return null;
        }
        return null;
    }

    @Override
    public String getVariantName(int variant) {
        return "blue_";
    }

    public Item getVariantScale(int variant) {
        return null;
    }

    public Item getVariantEgg(int variant) {
        return null;
    }

    protected Item getHeartItem() {
        return null;
    }

    protected Item getBloodItem() {
        return null;
    }

    public boolean isBreedingItem(@Nullable ItemStack stack) {
        return false;
    }

    public int getDragonStage() {
        return 5;
    }

    public int getAgeInDays() {
        return 125;
    }

    public int getArmorOrdinal(ItemStack stack) {
        return 0;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    public boolean isMale() {
        return false;
    }

    @Override
    public boolean isTimeToWake() {
        return true;
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setGender(this.getRNG().nextBoolean());
        this.setSleeping(false);
        this.updateAttributes();
        this.growDragon(125);
        this.heal((float) maximumHealth);
        this.usingGroundAttack = true;
        this.setHunger(50);
        return livingdata;
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    protected float getFlightChancePerTick() {
        return 1 / 15F;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }
}
