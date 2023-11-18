package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

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
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    private boolean canAddFlameAgain = true;

    public TileEntityDragonforge() {
    }

    public TileEntityDragonforge(int dragonType) {
        this.dragonType = dragonType;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBurning(IInventory inventory) {
        return inventory.getField(0) > 0;
    }

    public int getSizeInventory() {
        return this.forgeItemStacks.size();
    }

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
    
    public Block getGrillBlock(){
        if(dragonType == 0){
            return IafBlockRegistry.dragonforge_fire_brick;
        } if(dragonType == 1){
            return IafBlockRegistry.dragonforge_ice_brick;
        } if(dragonType == 2){
            return IafBlockRegistry.dragonforge_lightning_brick;
        }
        return IafBlockRegistry.dragonforge_fire_brick;
    }

    public boolean grillMatches(Block block){
        if(dragonType == 0 && block == IafBlockRegistry.dragonforge_fire_brick){
            return true;
        }
        if(dragonType == 1 && block == IafBlockRegistry.dragonforge_ice_brick){
            return true;
        }
        if(dragonType == 2 && block == IafBlockRegistry.dragonforge_lightning_brick){
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
        ItemStack itemstack = this.forgeItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.forgeItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag) {
            this.cookTime = 0;
            this.markDirty();
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.forgeItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.forgeItemStacks);
        this.cookTime = compound.getInteger("CookTime");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", (short) this.cookTime);
        ItemStackHelper.saveAllItems(compound, this.forgeItemStacks);
        return compound;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isBurning() {
        return this.cookTime > 0;
    }
    
    public int getForgeType(Block block){
        if(block == IafBlockRegistry.dragonforge_fire_core || block == IafBlockRegistry.dragonforge_fire_core_disabled){
            return 0;
        }
        if(block == IafBlockRegistry.dragonforge_ice_core || block == IafBlockRegistry.dragonforge_ice_core_disabled){
            return 1;
        }
        if(block == IafBlockRegistry.dragonforge_lightning_core || block == IafBlockRegistry.dragonforge_lightning_core_disabled){
            return 2;
        }
        return 0;
    }

    public String getTypeID(){
        switch (getForgeType(this.getBlockType())){
            case 0:
                return "fire";
            case 1:
                return "ice";
            case 2:
                return "lightning";
        }
        return "";
    }

    public void update() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        dragonType = getForgeType(this.getBlockType());
        if (lastDragonFlameTimer > 0) {
            lastDragonFlameTimer--;
        }
        updateGrills(assembled());
        if (!world.isRemote) {
            if (prevAssembled != assembled()) {
                BlockDragonforgeCore.setState(dragonType, prevAssembled, world, pos);
            }
            prevAssembled = this.assembled();
            if (!assembled()) {
                return;
            }
        }
        if (this.canSmelt() && cookTime > 0 && lastDragonFlameTimer == 0) {
            this.cookTime--;
        }

        if (!this.world.isRemote) {
            if (this.isBurning()) {
                if (this.isBurning() && this.canSmelt()) {
                    ++this.cookTime;

                    if (this.cookTime >= getMaxCookTime()) {
                        this.cookTime = 0;
                        this.smeltItem();
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, getMaxCookTime());
            }

            if (flag != this.isBurning()) {
                flag1 = true;
            }
        }

        if (flag1) {
            this.markDirty();
        }
        if (!canAddFlameAgain) {
            canAddFlameAgain = true;
        }
    }

    public int getMaxCookTime() {
        ItemStack stack = getCurrentResult();
        if (stack.getItem() == Item.getItemFromBlock(IafBlockRegistry.ash) || stack.getItem() == Item.getItemFromBlock(IafBlockRegistry.dragon_ice)) {
            return 100;
        }
        return 1000;
    }

    private ItemStack getCurrentResult() {
        DragonForgeRecipe forgeRecipe = null;
        if (dragonType == 0) {
            forgeRecipe =  IafRecipeRegistry.getFireForgeRecipe(this.forgeItemStacks.get(0));
        } else if (dragonType == 1) {
            forgeRecipe =  IafRecipeRegistry.getIceForgeRecipe(this.forgeItemStacks.get(0));
        } else if (dragonType == 2){
            forgeRecipe =  IafRecipeRegistry.getLightningForgeRecipe(this.forgeItemStacks.get(0));
        } else {
            forgeRecipe =  IafRecipeRegistry.getFireForgeRecipe(this.forgeItemStacks.get(0));
        }
        ItemStack itemstack = ItemStack.EMPTY;
        if (forgeRecipe != null && this.forgeItemStacks.get(1).isItemEqual(forgeRecipe.getBlood())) {
            itemstack = forgeRecipe.getOutput();
        }
        if (itemstack == ItemStack.EMPTY) {
		    Block defaultOutput = IafBlockRegistry.ash;
            if (this.dragonType == 1) {
                defaultOutput = IafBlockRegistry.dragon_ice;
            }
            itemstack = new ItemStack(defaultOutput);
        }
        return itemstack;
    }

    public boolean canSmelt() {
        if (this.forgeItemStacks.get(0).isEmpty()) {
            return false;
        } else {
            ItemStack itemstack = getCurrentResult();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = this.forgeItemStacks.get(2);

                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize())  // Forge fix: make furnace respect stack sizes in furnace recipes
                {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        }
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack itemstack = this.forgeItemStacks.get(0);
            ItemStack bloodStack = this.forgeItemStacks.get(1);
            ItemStack itemstack1 = getCurrentResult();
            ItemStack itemstack2 = this.forgeItemStacks.get(2);

            if (itemstack2.isEmpty()) {
                this.forgeItemStacks.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }
            if (!bloodStack.isEmpty() && this.cookTime == 0) {
                bloodStack.shrink(1);
            }
            itemstack.shrink(1);
        }
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        } else if (index == 1) {
            DragonForgeRecipe forgeRecipe;
            if (dragonType == 0) {
                forgeRecipe = IafRecipeRegistry.getFireForgeRecipeForBlood(stack);
            } else if (dragonType == 1) {
                forgeRecipe = IafRecipeRegistry.getIceForgeRecipeForBlood(stack);
            } else if (dragonType == 2) {
                forgeRecipe = IafRecipeRegistry.getLightningForgeRecipeForBlood(stack);
            } else {
                forgeRecipe = IafRecipeRegistry.getFireForgeRecipeForBlood(stack);
            }
            if (forgeRecipe != null) {
                return true;
            }
        }
        return index == 0;
    }

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
        if(dragonType == 0) {
            return "container.dragonforge_fire";
        }
        if(dragonType == 1){
            return "container.dragonforge_ice";
        }
        return "container.dragonforge_lightning";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }


    public void transferPower(int i) {
        if (this.canSmelt()) {
            if (canAddFlameAgain) {
                cookTime = Math.min(this.getMaxCookTime() + 1, cookTime + i);
                canAddFlameAgain = false;
            }
        } else {
            cookTime = 0;
        }
        lastDragonFlameTimer = 40;
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
        if(dragonType == 0){
            return IafBlockRegistry.dragonforge_fire_brick;
        } else if(dragonType == 1){
            return IafBlockRegistry.dragonforge_ice_brick;
        } else if(dragonType == 2){
            return IafBlockRegistry.dragonforge_lightning_brick;
        }
        return IafBlockRegistry.dragonforge_fire_brick;
    }

    private boolean doesBlockEqual(BlockPos pos, Block block) {
        return world.getBlockState(pos).getBlock() == block;
    }

    private boolean atleastThreeAreBricks(BlockPos pos) {
        int count = 0;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == getBrick()) {
                count++;
            }
        }
        return count > 2;
    }
}
