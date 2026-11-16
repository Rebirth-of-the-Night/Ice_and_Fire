package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieJar;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class TileEntityJar extends TileEntity implements ITickable {

    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    public boolean hasPixie;
    public boolean prevHasProduced;
    public boolean hasProduced;
    public boolean tamedPixie;
    /** A small slime kept in the jar instead of a pixie. Purely decorative. */
    public boolean hasSlime;
    /** When {@link #hasSlime} is set: true for a magma cube instead of a slime. */
    public boolean magmaCube;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public int ticksExisted;
    public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
    public float rotationYaw;
    public float prevRotationYaw;
    private float targetRotationYaw;
    private int lookAtPlayerTicks;
    net.minecraftforge.items.IItemHandler handlerBottom = new PixieJarInvWrapper(this);
    private final Random rand;

    public TileEntityJar() {
        this.rand = new Random();
        this.hasPixie = true;
    }

    public TileEntityJar(boolean empty) {
        this.rand = new Random();
        this.hasPixie = !empty;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("HasPixie", hasPixie);
        compound.setInteger("PixieType", pixieType);
        compound.setBoolean("HasProduced", hasProduced);
        compound.setBoolean("TamedPixie", tamedPixie);
        compound.setBoolean("HasSlime", hasSlime);
        compound.setBoolean("MagmaCube", magmaCube);
        if (pixieOwnerUUID != null) {
            compound.setUniqueId("PixieOwnerUUID", pixieOwnerUUID);
        }
        compound.setInteger("TicksExisted", ticksExisted);
        ItemStackHelper.saveAllItems(compound, this.pixieItems);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieHouseModel(pos.toLong(), packet.getNbtCompound().getInteger("PixieType")));
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        hasPixie = compound.getBoolean("HasPixie");
        pixieType = compound.getInteger("PixieType");
        hasProduced = compound.getBoolean("HasProduced");
        ticksExisted = compound.getInteger("TicksExisted");
        tamedPixie = compound.getBoolean("TamedPixie");
        hasSlime = compound.getBoolean("HasSlime");
        magmaCube = compound.getBoolean("MagmaCube");
        pixieOwnerUUID = compound.getUniqueId("PixieOwnerUUID");
        this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, pixieItems);
        super.readFromNBT(compound);
    }

    @Override
    public void update() {
        ticksExisted++;
        if (this.world.isRemote && this.hasPixie && this.rand.nextFloat() < 0.3F) {
            IceAndFire.PROXY.spawnParticle("if_pixie", this.pos.getX() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, this.pos.getY() + (double) (this.rand.nextFloat() * PARTICLE_HEIGHT), this.pos.getZ() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, EntityPixie.PARTICLE_RGB[this.pixieType][0], EntityPixie.PARTICLE_RGB[this.pixieType][1], EntityPixie.PARTICLE_RGB[this.pixieType][2]);
        }
        if (ticksExisted % 24000 == 0 && !this.hasProduced && this.hasPixie) {
            this.hasProduced = true;
            if (!this.getWorld().isRemote) {
                IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieJar(pos.toLong(), hasProduced));
            }
        }
        if (this.hasSlime && IceAndFire.CONFIG.jarSlimeProduction && !this.world.isRemote && ticksExisted % 24000 == 0 && !this.hasProduced && this.rand.nextInt(3) == 0) {
            this.hasProduced = true;
            this.markDirty();
            IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieJar(pos.toLong(), true));
            this.world.playSound(null, this.pos, this.magmaCube ? SoundEvents.ENTITY_MAGMACUBE_SQUISH : SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.NEUTRAL, 1, 1);
        }
        if (this.hasPixie && hasProduced != prevHasProduced && ticksExisted > 5) {
            if (!this.getWorld().isRemote) {
                IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieJar(pos.toLong(), hasProduced));
            }
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, IafSoundRegistry.PIXIE_HURT, SoundCategory.NEUTRAL, 1, 1, false);
        }
        prevRotationYaw = rotationYaw;
        float turnSpeed = 0.25F;
        if (this.world.isRemote && this.hasPixie) {
            if (this.lookAtPlayerTicks > 0) {
                this.lookAtPlayerTicks--;
            } else if (rand.nextInt(160) == 0) {
                this.lookAtPlayerTicks = 40 + rand.nextInt(60);
            }
            if (this.lookAtPlayerTicks > 0) {
                EntityPlayer player = this.world.getClosestPlayer(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 8.0D, false);
                if (player != null) {
                    double dx = player.posX - (this.pos.getX() + 0.5D);
                    double dz = player.posZ - (this.pos.getZ() + 0.5D);
                    // The jar renderer is flipped upside down, which mirrors the yaw direction.
                    this.targetRotationYaw = (float) Math.toDegrees(MathHelper.atan2(dx, dz)) + 180.0F;
                    turnSpeed = 6.0F;
                } else {
                    this.lookAtPlayerTicks = 0;
                }
            }
        }
        if (this.lookAtPlayerTicks <= 0 && rand.nextInt(120) == 0) {
            this.targetRotationYaw = this.rotationYaw + (rand.nextFloat() - 0.5F) * 40F;
        }
        // Seated pixies turn gently instead of snapping to a random direction in one tick.
        float yawDifference = MathHelper.wrapDegrees(this.targetRotationYaw - this.rotationYaw);
        this.rotationYaw += MathHelper.clamp(yawDifference, -turnSpeed, turnSpeed);
        if (this.hasPixie && ticksExisted % 40 == 0 && this.rand.nextInt(6) == 0) {
            this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5, IafSoundRegistry.PIXIE_IDLE, SoundCategory.NEUTRAL, 1, 1, false);
        }
        prevHasProduced = hasProduced;
    }

    public void releaseSlime() {
        this.hasSlime = false;
        if (!world.isRemote) {
            EntitySlime slime = this.magmaCube ? new EntityMagmaCube(this.world) : new EntitySlime(this.world);
            slime.setSlimeSize(1, true);
            slime.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, this.world.rand.nextFloat() * 360F, 0);
            this.world.spawnEntity(slime);
        }
    }

    public void releasePixie() {
        EntityPixie pixie = new EntityPixie(this.world);
        pixie.setPositionAndRotation(this.pos.getX() + 0.5F, this.pos.getY() + 1F, this.pos.getZ() + 0.5F, new Random().nextInt(360), 0);
        pixie.setHeldItem(EnumHand.MAIN_HAND, pixieItems.get(0));
        pixie.setColor(this.pixieType);
        if (!world.isRemote) {
            world.spawnEntity(pixie);
        }
        this.hasPixie = false;
        this.pixieType = 0;
        pixie.ticksUntilHouseAI = 500;
        pixie.setTamed(this.tamedPixie);
        pixie.setOwnerId(this.pixieOwnerUUID);

        if (!world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageUpdatePixieHouse(pos.toLong(), false, 0));
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) handlerBottom;
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.hasPixie;
    }
}
