package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


public class EntityFinalBallista extends EntityCreature implements IDreadMob {

    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityFinalBallista.class, DataSerializers.BYTE);
    int loadProgressInt;
    boolean isLoadInProgress;
    float loadProgress;
    float prevLoadProgress;
    public float loadProgressForRender;
    boolean attackedLastTick;
    int attackCount;

    public EntityFinalBallista(World worldIn) {
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
        isImmuneToFire = true;
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = dataManager.get(CONTROL_STATE);
        if (newState) {
            dataManager.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            dataManager.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    public byte getControlState() {
        return dataManager.get(CONTROL_STATE);
    }

    public void setControlState(byte state) {
        dataManager.set(CONTROL_STATE, state);
    }

    public boolean attack() {
        return (dataManager.get(CONTROL_STATE) & 1) == 1;
    }

    public boolean dismount() {
        return (dataManager.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public void attack(boolean attack) {
        setStateField(0, attack);
    }

    public void dismount(boolean dismount) {
        setStateField(1, dismount);
    }

    protected boolean canDespawn() {
        return false;
    }

    protected void initEntityAI() {
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            if (!isSwingInProgress) {
                swingProgressInt = -1;
                isSwingInProgress = true;
            }
        } else if (par1 == 17) {
            if (!isLoadInProgress) {
                loadProgressInt = -1;
                isLoadInProgress = true;
            }
        } else {
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
        } else {
            swingProgressInt = 0;
        }
        swingProgress = swingProgressInt / 6.0f;
        if (isLoadInProgress) {
            ++loadProgressInt;
            if (loadProgressInt >= 10) {
                loadProgressInt = 0;
                isLoadInProgress = false;
            }
        } else {
            loadProgressInt = 0;
        }
        loadProgress = loadProgressInt / 10.0f;
    }

    public void onEntityUpdate() {
        prevLoadProgress = loadProgress;
        super.onEntityUpdate();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (this.getHealth() > 0) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            player.startRiding(this, true);
            return true;
        }

        return super.processInteract(player, hand);
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
    }

    public int getTotalArmorValue() {
        return 20;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (!world.isRemote && this.attack() && this.getControllingPassenger() != null && this.getHealth() > 0) {
            this.riderShootFire(this.getControllingPassenger());
        }
        Entity controller = this.getControllingPassenger();
        if (controller instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) controller;

            this.rotationYaw = player.rotationYaw;
            this.rotationYawHead = player.rotationYawHead;
            this.rotationPitch = player.rotationPitch * 0.5f;
            this.renderYawOffset = this.rotationYaw;
            this.setRotation(this.rotationYaw, this.rotationPitch);

            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationYawHead = this.rotationYawHead;
            this.prevRotationPitch = this.rotationPitch;
        }
    }

    @SideOnly(Side.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.getControllingPassenger() != null) {
            byte previousState = getControlState();
            attack(IafKeybindRegistry.dragon_fireAttack.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, posX, posY, posZ));
            }
        }
        if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
            byte previousState = getControlState();
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, posX, posY, posZ));
            }
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof EntityPlayer) {
                return passenger;
            }
        }
        return null;
    }

    public void riderShootFire(Entity owner) {
        double d2, d3, d4;

        if (owner instanceof EntityLivingBase) {
            EntityLivingBase livingOwner = (EntityLivingBase) owner;

            float pitch = livingOwner.rotationPitch;
            float yaw = livingOwner.rotationYaw;

            float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
            float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
            float f2 = -MathHelper.cos(-pitch * 0.017453292F);
            float f3 = MathHelper.sin(-pitch * 0.017453292F);

            d2 = f1 * f2;
            d3 = f3;
            d4 = f * f2;
        } else {
            d2 = this.getLookVec().x;
            d3 = this.getLookVec().y;
            d4 = this.getLookVec().z;
        }

        float inaccuracy = 1.0F;
        d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;

        EntityBallistaArrow entityarrow = new EntityBallistaArrow(world, owner instanceof EntityLivingBase ? (EntityLivingBase) owner : this, d2, d3, d4);

        this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

        entityarrow.setPosition(this.posX + d2 * 3, this.posY + this.getEyeHeight() + d3 * 3, this.posZ + d4 * 3);

        if (!this.world.isRemote)
            this.world.spawnEntity(entityarrow);
    }

    public boolean canBePushed() {
        return true;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CONTROL_STATE, (byte) 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
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

    @Override
    public Entity getCommander() {
        return null;
    }
}