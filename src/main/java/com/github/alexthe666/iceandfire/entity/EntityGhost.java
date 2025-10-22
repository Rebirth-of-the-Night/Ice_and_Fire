package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIRestrictSunFlying;
import com.github.alexthe666.iceandfire.entity.ai.GhostAICharge;
import com.github.alexthe666.iceandfire.entity.ai.GhostPathNavigator;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityGhost extends EntityMob implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid, IBlacklistedFromStatues {

    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "ghost"));
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityGhost.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DAYTIME_MODE = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WAS_FROM_CHEST = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DAYTIME_COUNTER = EntityDataManager.createKey(EntityGhost.class, DataSerializers.VARINT);

    public static Animation ANIMATION_SCARE;
    public static Animation ANIMATION_HIT;
    private int animationTick;
    private Animation currentAnimation;

    public EntityGhost(World worldIn) {
        super(worldIn);
        ANIMATION_SCARE = Animation.create(30);
        ANIMATION_HIT = Animation.create(10);
        this.moveHelper = new MoveHelper(this);
        this.setSize(0.6F, 1.8F);
        this.experienceValue = 5;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSunFlying(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(3, new GhostAICharge(this));
        this.tasks.addTask(5, new EntityAIWander(this, 0.6D) {
            @Override
            public boolean shouldExecute() {
                this.executionChance = 60;
                return super.shouldExecute();
            }
        });
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F, 1.0F) {
            @Override
            public boolean shouldContinueExecuting() {
                if (this.closestEntity != null && this.closestEntity instanceof EntityPlayer && ((EntityPlayer) this.closestEntity).isCreative()) {
                    return false;
                }
                return super.shouldContinueExecuting();
            }
        });
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, false, false, new Predicate<EntityPlayer>() {
            @Override
            public boolean apply(@Nullable EntityPlayer entity) {
                return entity != null && entity.isEntityAlive();
            }
        }));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 10, false, false, new Predicate<EntityLivingBase>() {
            @Override
            public boolean apply(@Nullable EntityLivingBase entity) {
                return entity != null && entity.isEntityAlive() && DragonUtils.isVillager(entity);
            }
        }));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.ghostMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(IceAndFire.CONFIG.ghostAttackStrength);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D);
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return this.wasFromChest() ? LootTableList.EMPTY : LOOT;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.GHOST_IDLE;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.GHOST_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.GHOST_DIE;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return potioneffectIn.getPotion() != MobEffects.POISON &&
                potioneffectIn.getPotion() != MobEffects.WITHER &&
                super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return super.isEntityInvulnerable(source) ||
                source.isFireDamage() ||
                source == DamageSource.IN_WALL ||
                source == DamageSource.CACTUS ||
                source == DamageSource.DROWN ||
                source == DamageSource.FALLING_BLOCK ||
                source == DamageSource.ANVIL;
    }

    @Override
    protected PathNavigate createNavigator(World worldIn) {
        return new GhostPathNavigator(this, worldIn);
    }

    public boolean isCharging() {
        return this.dataManager.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.dataManager.set(CHARGING, charging);
    }

    public boolean isDaytimeMode() {
        return this.dataManager.get(IS_DAYTIME_MODE);
    }

    public void setDaytimeMode(boolean mode) {
        this.dataManager.set(IS_DAYTIME_MODE, mode);
    }

    public boolean wasFromChest() {
        return this.dataManager.get(WAS_FROM_CHEST);
    }

    public void setFromChest(boolean fromChest) {
        this.dataManager.set(WAS_FROM_CHEST, fromChest);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.noClip = true;

        if (!world.isRemote) {
            boolean day = isInDaylight() && !this.wasFromChest();
            if (day) {
                if (!this.isDaytimeMode()) {
                    this.setAnimation(ANIMATION_SCARE);
                }
                this.setDaytimeMode(true);
            } else {
                this.setDaytimeMode(false);
                this.setDaytimeCounter(0);
            }

            if (isDaytimeMode()) {
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
                this.setDaytimeCounter(this.getDaytimeCounter() + 1);
                if (getDaytimeCounter() >= 100) {
                    this.setInvisible(true);
                }
            } else {
                this.setInvisible(this.isPotionActive(MobEffects.INVISIBILITY));
                this.setDaytimeCounter(0);
            }
        } else {
            if (this.getAnimation() == ANIMATION_SCARE && this.getAnimationTick() == 3 && !this.isHauntedShoppingList() && rand.nextInt(3) == 0) {
                this.playSound(IafSoundRegistry.GHOST_JUMPSCARE, this.getSoundVolume(), this.getSoundPitch());
                IceAndFire.PROXY.spawnParticle("ghost_appearance", this.posX, this.posY, this.posZ, this.getEntityId(), 0, 0);
            }
        }

        if (this.getAnimation() == ANIMATION_HIT && this.getAttackTarget() != null) {
            if (this.getDistance(this.getAttackTarget()) < 1.4D && this.getAnimationTick() >= 4 && this.getAnimationTick() < 6) {
                this.playSound(IafSoundRegistry.GHOST_ATTACK, this.getSoundVolume(), this.getSoundPitch());
                this.attackEntityAsMob(this.getAttackTarget());
            }
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public boolean isAIDisabled() {
        return this.isDaytimeMode() || super.isAIDisabled();
    }

    @Override
    public boolean isSilent() {
        return this.isDaytimeMode() || super.isSilent();
    }

    protected boolean isInDaylight() {
        if (this.world.isDaytime() && !this.world.isRemote) {
            float f = this.getBrightness();
            BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ?
                    (new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ)).up() :
                    new BlockPos(this.posX, (double)Math.round(this.posY + 4), this.posZ);
            return f > 0.5F && this.world.canSeeSky(blockpos);
        }
        return false;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack != null && itemstack.getItem() == IafItemRegistry.manuscript && !this.isHauntedShoppingList()) {
            this.setColor(-1);
            this.playSound(IafSoundRegistry.BESTIARY_PAGE, 1, 1);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isDaytimeMode()) {
            super.travel(0, 0, 0);
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setColor(this.rand.nextInt(3));
        if (rand.nextInt(200) == 0) {
            this.setColor(-1);
        }
        return livingdata;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(COLOR, Integer.valueOf(0));
        this.dataManager.register(CHARGING, false);
        this.dataManager.register(IS_DAYTIME_MODE, false);
        this.dataManager.register(WAS_FROM_CHEST, false);
        this.dataManager.register(DAYTIME_COUNTER, 0);
    }

    public int getColor() {
        return MathHelper.clamp(this.dataManager.get(COLOR), -1, 2);
    }

    public void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    public int getDaytimeCounter() {
        return this.dataManager.get(DAYTIME_COUNTER);
    }

    public void setDaytimeCounter(int counter) {
        this.dataManager.set(DAYTIME_COUNTER, counter);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setColor(compound.getInteger("Color"));
        this.setDaytimeMode(compound.getBoolean("DaytimeMode"));
        this.setDaytimeCounter(compound.getInteger("DaytimeCounter"));
        this.setFromChest(compound.getBoolean("FromChest"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Color", this.getColor());
        compound.setBoolean("DaytimeMode", this.isDaytimeMode());
        compound.setInteger("DaytimeCounter", this.getDaytimeCounter());
        compound.setBoolean("FromChest", this.wasFromChest());
    }

    public boolean isHauntedShoppingList() {
        return this.getColor() == -1;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_SCARE, ANIMATION_HIT};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return false;
    }

    class MoveHelper extends EntityMoveHelper {
        EntityGhost ghost;

        public MoveHelper(EntityGhost ghost) {
            super(ghost);
            this.ghost = ghost;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.posX - ghost.posX, this.posY - ghost.posY, this.posZ - ghost.posZ);
                double d0 = vec3d.length();
                double edgeLength = ghost.getEntityBoundingBox().getAverageEdgeLength();

                if (d0 < edgeLength) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    ghost.motionX *= 0.5D;
                    ghost.motionY *= 0.5D;
                    ghost.motionZ *= 0.5D;
                } else {
                    ghost.motionX += vec3d.x * this.speed * 0.5D * 0.05D / d0;
                    ghost.motionY += vec3d.y * this.speed * 0.5D * 0.05D / d0;
                    ghost.motionZ += vec3d.z * this.speed * 0.5D * 0.05D / d0;

                    if (ghost.getAttackTarget() == null) {
                        ghost.rotationYaw = -((float) MathHelper.atan2(ghost.motionX, ghost.motionZ)) * (180F / (float) Math.PI);
                        ghost.renderYawOffset = ghost.rotationYaw;
                    } else {
                        double d4 = ghost.getAttackTarget().posX - ghost.posX;
                        double d5 = ghost.getAttackTarget().posZ - ghost.posZ;
                        ghost.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                        ghost.renderYawOffset = ghost.rotationYaw;
                    }
                }
            }
        }
    }
}