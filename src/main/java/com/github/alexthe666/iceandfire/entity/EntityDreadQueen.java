package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.DreadAIMountDragon;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityDreadQueen extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {

    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "dread_queen"));

    private final BossInfoServer bossInfo = (BossInfoServer) new BossInfoServer(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS).setDarkenSky(true);
    private static final DataParameter<Boolean> AWAKEN = EntityDataManager.createKey(EntityDreadQueen.class, DataSerializers.BOOLEAN);
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    private int animationTick;
    private Animation currentAnimation;

    private boolean awake = false;

    public EntityDreadQueen(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(AWAKEN, false);

    }

    protected void initEntityAI() {

    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    /**
     * Pabilo & Carver, Quality Coding(tm)
     */
    public void doRoboty() {
        if (!awake) {
            awake = true;
            this.tasks.addTask(0, new DreadAIMountDragon(this));
            this.tasks.addTask(1, new EntityAISwimming(this));
            this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
            this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
            this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
            this.tasks.addTask(7, new EntityAILookIdle(this));
            this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
            this.targetTasks.addTask(3, new DreadAITargetNonDread(this, EntityLivingBase.class, false, DragonUtils::canHostilesTarget));
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.dreadQueenMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(256.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(30.0D);
    }

    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected void updateAITasks() {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        this.setAnimation(ANIMATION_SPAWN);
        this.setEquipmentBasedOnDifficulty(difficulty);
        return data;
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(IafItemRegistry.dread_queen_sword));
        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(IafItemRegistry.dread_queen_staff));
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
        return new Animation[]{ANIMATION_SPAWN};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote) {
            boolean server = dataManager.get(AWAKEN);
            if (server && !awake)
                awake = true;
        }
        if (!isAwaken() && !world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(20)).isEmpty())
            doRoboty();
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("awake", awake);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        awake = compound.getBoolean("awake");

    }

    public boolean isAwaken() {
        return awake;
    }
}
