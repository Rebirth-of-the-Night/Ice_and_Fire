package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.compat.spartanweaponry.SpartanWeaponryCompat;
import com.github.alexthe666.iceandfire.item.ItemTideTrident;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

import javax.annotation.Nullable;
import java.util.List;

/*
 *
 * Smarter TideTrident Pickup From Ice and Fire - RLCraft Edition
 * Code by Alexthe668, Shivaxi, FonnyMunkey and Kotlin-Programmer
 * Under LGPL-3.0 License
 * Port by keletu
 *
 * */
public class EntityTideTrident extends EntityArrow implements IThrowableEntity {
    private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, Entity::canBeCollidedWith);
    private static final DataParameter<ItemStack> WEAPON = EntityDataManager.createKey(EntityTideTrident.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Byte> RETURN = EntityDataManager.createKey(EntityTideTrident.class, DataSerializers.BYTE);
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private int inData;
    private int ticksInGround;
    private int ticksInAir;
    private int knockbackStrength;
    private boolean isReturning = false;
    private boolean playedReturnSound = false;

    public EntityTideTrident(World world) {
        super(world);
        init();
    }

    public EntityTideTrident(World world, double x, double y, double z) {
        super(world, x, y, z);
        init();
    }

    public EntityTideTrident(World world, EntityLivingBase shooter) {
        super(world, shooter);
        init();
    }

    private void init() {
        this.setDamage(IceAndFire.CONFIG.tideTridentBaseDamage);
        this.setSize(0.85F, 0.5F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(WEAPON, ItemStack.EMPTY);
        this.dataManager.register(RETURN, (byte) 0);
    }

    public void setWeapon(ItemStack weaponStack) {
        ItemStack stack = weaponStack.copy();
        if (stack.getItem() instanceof ItemTideTrident) {
            ItemTideTrident.setEmpty(stack, false);
            ItemTideTrident.setOriginal(stack, false);
        }
        this.getDataManager().set(WEAPON, stack);
        this.getDataManager().setDirty(WEAPON);
        if (SpartanWeaponryCompat.hasReturnEnchantment()) {
            this.getDataManager().set(RETURN, (byte) EnchantmentHelper.getEnchantmentLevel(SpartanWeaponryCompat.getReturnEnchantment(), stack));
            this.getDataManager().setDirty(RETURN);
        }
    }

    public void onUpdate() {
        Vec3d vec3d;
        if (this.timeInGround > 4 || this.noClip || this.isReturning && this.shootingEntity != null) {
            int returnLevel = this.getDataManager().get(RETURN);
            if (returnLevel > 0 && this.shootingEntity.isEntityAlive() && (!(this.shootingEntity instanceof EntityPlayerMP) || !((EntityPlayer) this.shootingEntity).isSpectator())) {
                if (!this.isReturning) {
                    this.noClip = true;
                    this.inGround = false;
                    this.isReturning = true;
                    this.setNoGravity(true);
                }

                Vec3d distance = this.shootingEntity.getPositionEyes(1.0F).subtract(this.getPositionVector());
                this.posY += distance.y * 0.015 * (double)returnLevel;
                if (this.world.isRemote) {
                    this.prevPosY = this.posY;
                }

                double velocity = 1.0 + 0.25 * (double)(returnLevel - 1);
                vec3d = distance.normalize().scale(velocity);
                this.motionX = vec3d.x;
                this.motionY = vec3d.y;
                this.motionZ = vec3d.z;
                if (!this.playedReturnSound) {
                    SoundEvent returnSound = SpartanWeaponryCompat.getReturnSoundEvent();
                    if (returnSound != null) {
                        this.world.playSound(null, this.posX, this.posY, this.posZ, returnSound, SoundCategory.NEUTRAL, 10.0F, 0.1F);
                    }
                    this.playedReturnSound = true;
                }
            } else if (returnLevel > 0 && !this.shootingEntity.isEntityAlive()) {
                this.noClip = false;
                this.isReturning = false;
                this.setNoGravity(false);
                this.getDataManager().set(RETURN, (byte)0);
            }
        }

        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        if (iblockstate.getMaterial() != Material.AIR && !noClip) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);
            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround && !this.noClip) {
            int j = block.getMetaFromState(iblockstate);
            if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05))) {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                this.ticksInGround++;
                if (this.ticksInGround >= 1200) {
                    this.attemptDropAsItem();
                    this.setDead();
                }
            }

            this.timeInGround++;
        } else {
            this.timeInGround = 0;
            this.ticksInAir++;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);
            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            boolean caught = false;
            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) raytraceresult.entityHit;
                if (this.shootingEntity instanceof EntityPlayer) {
                    if (this.canBeCaughtInMidair(this.shootingEntity, entityplayer)) {
                        caught = this.attemptCatch(entityplayer);
                    }

                    if (!((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
                        raytraceresult = null;
                    }
                }
            }

            if (raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS && !caught && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }

            if (this.getIsCritical()) {
                for (int k = 0; k < 4; ++k) {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double) k / 4.0D, this.posY + this.motionY * (double) k / 4.0D, this.posZ + this.motionZ * (double) k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (noClip) {
                this.rotationYaw = (float) (MathHelper.atan2(-this.motionX, -this.motionZ) * (180D / Math.PI));
            } else {
                this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            }
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, f4) * (180D / Math.PI));

            while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
                this.prevRotationPitch -= 360.0F;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
                }
            }

            if (this.isWet()) {
                this.extinguish();
            }

            if (!this.noClip) {
                float f1 = 0.99F;
                this.motionX *= f1;
                this.motionY *= f1;
                this.motionZ *= f1;
                if (!this.hasNoGravity()) {
                    this.motionY -= 0.05000000074505806;
                }
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    @Nullable
    @Override
    protected Entity findEntityOnPath(Vec3d currentVec, Vec3d nextVec) {
        Entity entityOnPath = null;
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0), ARROW_TARGETS);
        double d0 = 0.0;

        for (Entity entity : list) {
            if (entity != this.shootingEntity || this.ticksInAir >= 5) {
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(0.30000001192092896);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(currentVec, nextVec);
                if (raytraceresult != null) {
                    double d1 = currentVec.squareDistanceTo(raytraceresult.hitVec);
                    if (d1 < d0 || d0 == 0.0) {
                        entityOnPath = entity;
                        d0 = d1;
                    }
                }
            }
        }

        return entityOnPath;
    }

    @Override
    public void setKnockbackStrength(int knockback) {
        this.knockbackStrength = knockback;
    }

    @Override
    public Entity getThrower() {
        return this.shootingEntity;
    }

    @Override
    public void setThrower(Entity entity) {
        this.shootingEntity = entity;
    }

    @Override
    public void onKillCommand() {
        this.attemptDropAsItem();
        super.onKillCommand();
    }

    /**
     * Called when the arrow hits a block or an entity
     */
    protected void onHit(RayTraceResult raytraceResultIn) {
        Entity entity = raytraceResultIn.entityHit;

        if (entity != null) {
            if (this.noClip) {
                return;
            }

            float f = isInWater() ? IceAndFire.CONFIG.tideTridentUnderwaterDamageMultiplier : 1.0f;
            int i = MathHelper.ceil((double) f * this.getDamage());

            if (this.getIsCritical()) {
                i += this.rand.nextInt(i / 2 + 2);
            }

            if (this.isBurning() && !(entity instanceof EntityEnderman)) {
                entity.setFire(5);
            }

            DamageSource damagesource = DamageSource.causeThrownDamage(this, shootingEntity);
            if (entity.attackEntityFrom(damagesource, (float) i)) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity;

                    if (!this.world.isRemote) {
                        entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                    }

                    if (this.knockbackStrength > 0) {
                        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (f1 > 0.0F) {
                            entitylivingbase.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
                        }
                    }

                    if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entitylivingbase);
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }
                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            } else {
                this.motionX *= -0.10000000149011612;
                this.motionY *= -0.10000000149011612;
                this.motionZ *= -0.10000000149011612;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;
                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513) {
                    if (this.getDataManager().get(RETURN) > 0 && !this.noClip) {
                        this.noClip = true;
                    } else {
                        this.attemptDropAsItem();
                        this.setDead();
                    }
                }
            }
        }  else {
            if (!this.noClip) {
                if (raytraceResultIn.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (!this.world.isRemote) {
                        ((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 5, 0.1, 0.1, 0.1, 0.05, Block.getIdFromBlock(this.world.getBlockState(raytraceResultIn.getBlockPos()).getBlock()));
                    }
                }

                BlockPos blockpos = raytraceResultIn.getBlockPos();
                this.xTile = blockpos.getX();
                this.yTile = blockpos.getY();
                this.zTile = blockpos.getZ();
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                this.inTile = iblockstate.getBlock();
                this.inData = this.inTile.getMetaFromState(iblockstate);
                this.motionX = (float)(raytraceResultIn.hitVec.x - this.posX);
                this.motionY = (float)(raytraceResultIn.hitVec.y - this.posY);
                this.motionZ = (float)(raytraceResultIn.hitVec.z - this.posZ);
                float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / (double)f2 * 0.05000000074505806;
                this.posY -= this.motionY / (double)f2 * 0.05000000074505806;
                this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806;
                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;
                this.arrowShake = 7;
                this.setIsCritical(false);
                if (iblockstate.getMaterial() != Material.AIR) {
                    this.inTile.onEntityCollision(this.world, blockpos, iblockstate, this);
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setShort("life", (short) this.ticksInGround);
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("inData", (byte) this.inData);
        compound.setByte("shake", (byte) this.arrowShake);
        compound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        compound.setByte("pickup", (byte) this.pickupStatus.ordinal());
        compound.setDouble("damage", this.getDamage());
        compound.setBoolean("crit", this.getIsCritical());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.ticksInGround = compound.getShort("life");

        if (compound.hasKey("inTile", 8)) {
            this.inTile = Block.getBlockFromName(compound.getString("inTile"));
        } else {
            this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        }

        this.inData = compound.getByte("inData") & 255;
        this.arrowShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;

        if (compound.hasKey("damage", 99)) {
            this.setDamage(compound.getDouble("damage"));
        }

        if (compound.hasKey("pickup", 99)) {
            this.pickupStatus = EntityArrow.PickupStatus.getByOrdinal(compound.getByte("pickup"));
        } else if (compound.hasKey("player", 99)) {
            this.pickupStatus = compound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
        }

        this.setIsCritical(compound.getBoolean("crit"));
    }

    protected void attemptDropAsItem() {
        if (this.pickupStatus == PickupStatus.ALLOWED) {
            this.entityDropItem(this.getArrowStack(), 0.1F);
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (this.inGround) {
            this.attemptCatch(entityIn);
        }
    }

    protected boolean attemptCatch(EntityPlayer player) {
        if (!this.world.isRemote && this.arrowShake <= 0) {
            if (this.pickupStatus == PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode) {
                player.onItemPickup(this, 1);
                this.setDead();
                return true;
            } else if (this.pickupStatus == PickupStatus.ALLOWED) {
                ItemStack weapon = this.getArrowStack();

                for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack slotStack = player.inventory.getStackInSlot(i);
                    if (slotStack.getItem() instanceof ItemTideTrident && ItemTideTrident.hasMatchingUUID(slotStack, weapon)) {
                        boolean empty = ItemTideTrident.isEmpty(slotStack);
                        if (empty) {
                            int itemDamage = slotStack.getItemDamage() + 1;
                            if (itemDamage > slotStack.getMaxDamage()) {
                                player.renderBrokenItemStack(slotStack);
                                slotStack.setCount(0);
                            } else {
                                ItemTideTrident.setEmpty(slotStack, ItemTideTrident.isEmpty(weapon));
                                slotStack.setItemDamage(itemDamage);
                            }
                            player.onItemPickup(this, 1);
                            this.setDead();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected boolean canBeCaughtInMidair(Entity shooter, Entity entityHit) {
        return shooter == entityHit && this.noClip;
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.getDataManager().get(WEAPON);
    }
}