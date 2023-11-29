package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


public class EntityCastleBallista extends EntityCreature implements IRangedAttackMob, IDreadMob
{
    int loadProgressInt;
    boolean isLoadInProgress;
    float loadProgress;
    float prevLoadProgress;
    public float loadProgressForRender;
    boolean attackedLastTick;
    int attackCount;
    
    public EntityCastleBallista(World worldIn) {
        super(worldIn);
        loadProgressInt = 0;
        isLoadInProgress = false;
        loadProgress = 0.0f;
        prevLoadProgress = 0.0f;
        loadProgressForRender = 0.0f;
        attackedLastTick = false;
        attackCount = 0;
        setSize(1.5f, 3.00f);
        stepHeight = 0.0f;
    }

    protected boolean canDespawn() {
        return false;
    }

    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIAttackRanged(this, 0.0, 20, 60, 120.0f));
        tasks.addTask(2, new EntityAIWatchTarget(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityDragonBase.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        EntityBallistaArrow entityarrow = new EntityBallistaArrow(world, this);

        entityarrow.shoot(this, this.rotationPitch, this.rotationYaw, 0F, 3F, 1.0F);
        this.playSound(IafSoundRegistry.ICEDRAGON_BREATH, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            if (!isSwingInProgress) {
                swingProgressInt = -1;
                isSwingInProgress = true;
            }
        }
        else if (par1 == 17) {
            if (!isLoadInProgress) {
                loadProgressInt = -1;
                isLoadInProgress = true;
            }
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    protected void updateArmSwingProgress() {
        if (isSwingInProgress) {
            ++swingProgressInt;
            if (swingProgressInt >= 6) {
                swingProgressInt = 0;
                isSwingInProgress = false;
            }
        }
        else {
            swingProgressInt = 0;
        }
        swingProgress = swingProgressInt / 6.0f;
        if (isLoadInProgress) {
            ++loadProgressInt;
            if (loadProgressInt >= 10) {
                loadProgressInt = 0;
                isLoadInProgress = false;
            }
        }
        else {
            loadProgressInt = 0;
        }
        loadProgress = loadProgressInt / 10.0f;
    }
    
    public void onEntityUpdate() {
        prevLoadProgress = loadProgress;
        super.onEntityUpdate();
    }
    
    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isOnSameTeam(entityIn);
    }
    
    public float getEyeHeight() {
        return height * 0.66f;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(120.0);
    }
    
    public int getTotalArmorValue() {
        return 20;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getAttackTarget() != null && (getAttackTarget().isDead || isOnSameTeam(getAttackTarget()))) {
            setAttackTarget(null);
        }
        if (!world.isRemote) {
            rotationYaw = rotationYawHead;
            if (ticksExisted % 80 == 0) {
                heal(1.0f);
            }
            int k = MathHelper.floor(posX);
            int l = MathHelper.floor(posY);
            int i1 = MathHelper.floor(posZ);
            if (BlockRailBase.isRailBlock(world, new BlockPos(k, l - 1, i1))) {
                --l;
            }
        }
        else {
            updateArmSwingProgress();
        }
    }
    
    public boolean canBePushed() {
        return true;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }
    
    public boolean attackEntityFrom(DamageSource source, float amount) {
        rotationYaw += (float)(getRNG().nextGaussian() * 45.0);
        rotationPitch += (float)(getRNG().nextGaussian() * 20.0);
        return super.attackEntityFrom(source, amount);
    }
    
    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_ / 10.0, p_70653_5_ / 10.0);
        if (motionY > 0) {
            motionY = 0;
        }
    }
    
    public void move(MoverType mt, double x, double y, double z) {
        super.move(mt, x / 20.0, y, z / 20.0);
    }

    public void applyEntityCollision(Entity entityIn) {
    }

    public float getCollisionBorderSize() {
        return 0.0F;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
    }


    public int getVerticalFaceSpeed() {
        return 20;
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public Entity getCommander() {
        return null;
    }

    protected class EntityAIWatchTarget extends EntityAIBase
    {
        protected EntityLiving theWatcher;
        protected Entity closestEntity;
        private int lookTime;
        
        public EntityAIWatchTarget(EntityLiving p_i1631_1_) {
            theWatcher = p_i1631_1_;
            setMutexBits(2);
        }
        
        public boolean shouldExecute() {
            if (theWatcher.getAttackTarget() != null) {
                closestEntity = theWatcher.getAttackTarget();
            }
            return closestEntity != null;
        }
        
        public boolean shouldContinueExecuting() {
            float d = (float) getTargetDistance();
            return closestEntity.isEntityAlive() && theWatcher.getDistanceSq(closestEntity) <= d * d && lookTime > 0;
        }
        
        public void startExecuting() {
            lookTime = 40 + theWatcher.getRNG().nextInt(40);
        }
        
        public void resetTask() {
            closestEntity = null;
        }
        
        public void updateTask() {
            theWatcher.getLookHelper().setLookPosition(closestEntity.posX, closestEntity.posY + closestEntity.getEyeHeight(), closestEntity.posZ, 10.0f, (float) theWatcher.getVerticalFaceSpeed());
            --lookTime;
        }
        
        protected double getTargetDistance() {
            IAttributeInstance iattributeinstance = theWatcher.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return (iattributeinstance == null) ? 16.0 : iattributeinstance.getAttributeValue();
        }
    }
}