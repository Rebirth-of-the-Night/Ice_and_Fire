package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDragonforgeCore extends BlockContainer implements IDragonProof {
    private static boolean keepInventory;
    private final int dragonType;
    private final boolean activated;

    public BlockDragonforgeCore(int dragonType, boolean activated) {
        super(Material.IRON);
        this.setHardness(40F);
        this.setResistance(500F);
        this.setSoundType(SoundType.METAL);
        if (!activated) {
            this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        }
        String disabled = activated ? "" : "_disabled";
        this.setTranslationKey("iceandfire.dragonforge_" + DragonType.getNameFromInt(dragonType) + "_core");
        this.setRegistryName(IceAndFire.MODID, "dragonforge_" + DragonType.getNameFromInt(dragonType) + "_core" + disabled);
        if (activated) {
            this.setLightLevel(1.0F);
        }
        this.dragonType = dragonType;
        this.activated = activated;
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    public static void setState(int dragonType, boolean active, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active) {
            if (dragonType == 0) {
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_fire_core.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_fire_core.getDefaultState(), 3);
            } else if(dragonType == 1){
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_ice_core.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_ice_core.getDefaultState(), 3);
            } else if(dragonType == 2){
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_lightning_core.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_lightning_core.getDefaultState(), 3);
            }
        } else {
            if (dragonType == 0) {
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_fire_core_disabled.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_fire_core_disabled.getDefaultState(), 3);
            } else if(dragonType == 1){
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_ice_core_disabled.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_ice_core_disabled.getDefaultState(), 3);
            } else if(dragonType == 2){
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_lightning_core_disabled.getDefaultState(), 3);
                worldIn.setBlockState(pos, IafBlockRegistry.dragonforge_lightning_core_disabled.getDefaultState(), 3);
            }
        }

        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking()) {
            return false;
        } else {
            playerIn.openGui(IceAndFire.INSTANCE, 7, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if(dragonType == 0) {
            return Item.getItemFromBlock(IafBlockRegistry.dragonforge_fire_core_disabled);
        }
        if(dragonType == 1) {
            return Item.getItemFromBlock(IafBlockRegistry.dragonforge_ice_core_disabled);
        }
        if(dragonType == 2) {
            return Item.getItemFromBlock(IafBlockRegistry.dragonforge_lightning_core_disabled);
        }
        return Item.getItemFromBlock(IafBlockRegistry.dragonforge_fire_core_disabled);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        if(dragonType == 0) {
            return new ItemStack(Item.getItemFromBlock(IafBlockRegistry.dragonforge_fire_core_disabled));
        }
        if(dragonType == 1) {
            return new ItemStack(Item.getItemFromBlock(IafBlockRegistry.dragonforge_ice_core_disabled));
        }
        if(dragonType == 2) {
            return new ItemStack(Item.getItemFromBlock(IafBlockRegistry.dragonforge_lightning_core_disabled));
        }
        return new ItemStack(Item.getItemFromBlock(IafBlockRegistry.dragonforge_fire_core_disabled));
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return dragonType == 0;
    }

    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (this.activated) {

        }
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDragonforge(dragonType);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityDragonforge) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDragonforge) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }


}
