package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.block.BlockDragonforgeBricks;
import com.github.alexthe666.iceandfire.block.BlockDragonforgeCore;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntityDragonforge extends TileEntity implements ITickable, ISidedInventory {
    private static final int[] SLOTS_TOP = new int[]{0, 1};
    private static final int[] SLOTS_BOTTOM = new int[]{2};
    private static final int[] SLOTS_SIDES = new int[]{0, 1};
    public int dragonType;
    net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.UP);
    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);
    private NonNullList<ItemStack> forgeItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    private int cookTime;
    private int lastDragonFlameTimer = 0;
    private boolean prevAssembled;

    public TileEntityDragonforge() {
    }

    public TileEntityDragonforge(int dragonType) {
        this.dragonType = dragonType;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory inventory) {
        return inventory.getField(0) > 0;
    }

    @Override
    public int getSizeInventory() {
        return this.forgeItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.forgeItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private void updateGrills(boolean grill) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos grillPos = this.getPos().offset(facing);
            if (grillMatches(world.getBlockState(grillPos).getBlock())) {
                IBlockState grillState = getGrillBlock().getDefaultState().withProperty(BlockDragonforgeBricks.GRILL, grill);
                if (world.getBlockState(grillPos) != grillState) {
                    world.setBlockState(grillPos, grillState);
                }
            }
        }
    }

    public Block getGrillBlock() {
        if (dragonType == 0) {
            return IafBlockRegistry.dragonforge_fire_brick;
        }
        if (dragonType == 1) {
            return IafBlockRegistry.dragonforge_ice_brick;
        }
        if (dragonType == 2) {
            return IafBlockRegistry.dragonforge_lightning_brick;
        }
        return IafBlockRegistry.dragonforge_fire_brick;
    }

    public boolean grillMatches(Block block) {
        if (dragonType == 0 && block == IafBlockRegistry.dragonforge_fire_brick) {
            return true;
        }
        if (dragonType == 1 && block == IafBlockRegistry.dragonforge_ice_brick) {
            return true;
        }
        if (dragonType == 2 && block == IafBlockRegistry.dragonforge_lightning_brick) {
            return true;
        }
        return false;
    }

    public ItemStack getStackInSlot(int index) {
        return this.forgeItemStacks.get(index);
    }

    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.forgeItemStacks, index, count);
    }

    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.forgeItemStacks, index);
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        this.forgeItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.forgeItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.forgeItemStacks);
        this.cookTime = compound.getShort("CookTime");
        this.lastDragonFlameTimer = compound.getShort("LastFlameTimer");
        this.prevAssembled = compound.getBoolean("prevAssembled");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("CookTime", (short) this.cookTime);
        compound.setShort("LastFlameTimer", (short) this.lastDragonFlameTimer);
        compound.setBoolean("prevAssembled", this.prevAssembled);
        ItemStackHelper.saveAllItems(compound, this.forgeItemStacks);
        return compound;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isBurning() {
        return this.cookTime > 0;
    }

    public int getForgeType(Block block) {
        if (block == IafBlockRegistry.dragonforge_fire_core || block == IafBlockRegistry.dragonforge_fire_core_disabled) {
            return 0;
        }
        if (block == IafBlockRegistry.dragonforge_ice_core || block == IafBlockRegistry.dragonforge_ice_core_disabled) {
            return 1;
        }
        if (block == IafBlockRegistry.dragonforge_lightning_core || block == IafBlockRegistry.dragonforge_lightning_core_disabled) {
            return 2;
        }
        return 0;
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            boolean flag = this.isBurning();
            boolean flag1 = false;
            dragonType = getForgeType(this.getBlockType());

            updateGrills(assembled());

            if (prevAssembled != assembled()) {
                BlockDragonforgeCore.setState(dragonType, prevAssembled, world, pos);
            }
            prevAssembled = this.assembled();

            if (!assembled()) {
                return;
            }

            if (this.lastDragonFlameTimer > 0) {
                this.lastDragonFlameTimer--;
            }

            if (this.isBurning()) {
                if (!this.canSmelt() || this.lastDragonFlameTimer == 0) {
                    this.cookTime = Math.max(this.cookTime - 1, 0);
                }

                if (this.canSmelt()) {
                    if (this.cookTime >= getMaxCookTime()) {
                        this.smeltItem();
                        this.cookTime = 0;
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            }

            if (flag != this.isBurning()) {
                flag1 = true;
            }

            if (flag1) {
                this.markDirty();
            }
        }
    }

    public String getTypeID() {
        switch (getForgeType(this.getBlockType())) {
            case 0:
                return "fire";
            case 1:
                return "ice";
            case 2:
                return "lightning";
        }
        return "";
    }

    public int getMaxCookTime() {
        return 1000;
    }

    private DragonForgeRecipe getRecipe(String type) {
        ItemStack inputItemStack = this.forgeItemStacks.get(0);
        DragonForgeRecipe recipe = IafRecipeRegistry.getForgeRecipe(type, inputItemStack);
        if (recipe != null && recipe.canSmelt(this.forgeItemStacks)) {
            return recipe;
        }
        return null;
    }

    public boolean canSmelt() {
        return canSmelt(getTypeID());
    }

    public boolean canSmelt(String type) {
        DragonForgeRecipe recipe = getRecipe(type);
        if (recipe == null) {
            return false;
        }
        return recipe.canSmelt(this.forgeItemStacks);
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        DragonForgeRecipe recipe = getRecipe(getTypeID());
        if (recipe == null) {
            return;
        }
        recipe.smelt(this.forgeItemStacks);
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        } else if (index == 1) {
            DragonForgeRecipe forgeRecipe = IafRecipeRegistry.getForgeRecipeForBlood(getTypeID(), stack);
            if (forgeRecipe != null) {
                return true;
            }
        }
        return index == 0;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
        }
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.DOWN && index == 1) {
            Item item = stack.getItem();

            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }

        return true;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDragonForge(playerInventory, this);
    }

    public int getField(int id) {
        return cookTime;
    }

    public void setField(int id, int value) {
        cookTime = value;
    }

    public int getFieldCount() {
        return 1;
    }

    public void clear() {
        this.forgeItemStacks.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    public String getName() {
        if (dragonType == 0) {
            return "container.dragonforge_fire";
        }
        if (dragonType == 1) {
            return "container.dragonforge_ice";
        }
        return "container.dragonforge_lightning";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    public void transferPower() {
        if (this.canSmelt(getTypeID())) {
            if (this.lastDragonFlameTimer != 40) {
                this.cookTime = Math.min(this.cookTime + 1, getMaxCookTime());
            }

            this.lastDragonFlameTimer = 40;
        }
    }

    private boolean checkBoneCorners(BlockPos pos) {
        return doesBlockEqual(pos.north().east(), IafBlockRegistry.dragon_bone_block) &&
                doesBlockEqual(pos.north().west(), IafBlockRegistry.dragon_bone_block) &&
                doesBlockEqual(pos.south().east(), IafBlockRegistry.dragon_bone_block) &&
                doesBlockEqual(pos.south().west(), IafBlockRegistry.dragon_bone_block);
    }

    private boolean checkBrickCorners(BlockPos pos) {
        return doesBlockEqual(pos.north().east(), getBrick()) &&
                doesBlockEqual(pos.north().west(), getBrick()) &&
                doesBlockEqual(pos.south().east(), getBrick()) &&
                doesBlockEqual(pos.south().west(), getBrick());
    }

    private boolean checkBrickSlots(BlockPos pos) {
        return doesBlockEqual(pos.north(), getBrick()) &&
                doesBlockEqual(pos.east(), getBrick()) &&
                doesBlockEqual(pos.west(), getBrick()) &&
                doesBlockEqual(pos.south(), getBrick());
    }

    public boolean assembled() {
        return checkBoneCorners(pos.down()) && checkBrickSlots(pos.down()) &&
                checkBrickCorners(pos) && atleastThreeAreBricks(pos) &&
                checkBoneCorners(pos.up()) && checkBrickSlots(pos.up());
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        return getCapability(capability, facing) != null;
    }

    private Block getBrick() {
        switch (dragonType) {
            default:
                return IafBlockRegistry.dragonforge_fire_brick;
            case 1:
                return IafBlockRegistry.dragonforge_ice_brick;
            case 2:
                return IafBlockRegistry.dragonforge_lightning_brick;
        }
    }

    private Block getHatch() {
        switch (dragonType) {
            default:
                return IafBlockRegistry.dragonforge_fire_input;
            case 1:
                return IafBlockRegistry.dragonforge_ice_input;
            case 2:
                return IafBlockRegistry.dragonforge_lightning_input;
        }
    }

    private boolean doesBlockEqual(BlockPos pos, Block block) {
        return world.getBlockState(pos).getBlock() == block;
    }

    private boolean atleastThreeAreBricks(BlockPos pos) {
        int countBrick = 0;
        int countHatch = 0;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == getBrick()) {
                countBrick++;
            }
            if (world.getBlockState(pos.offset(facing)).getBlock() == getHatch()) {
                countHatch++;
            }
        }
        return countBrick > 2 && countBrick + countHatch == 4;
    }
}
