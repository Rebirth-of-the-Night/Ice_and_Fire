package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityDragonforgeBrick extends TileEntity {

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (getConnectedTileEntity() != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getConnectedTileEntity().getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return getConnectedTileEntity() != null && getConnectedTileEntity().hasCapability(capability, facing);
    }

    private ICapabilityProvider getConnectedTileEntity() {
        for (EnumFacing facing : EnumFacing.values()) {
            if (world.getTileEntity(pos.offset(facing)) != null && world.getTileEntity(pos.offset(facing)) instanceof TileEntityDragonforge) {
                return world.getTileEntity(pos.offset(facing));
            }
        }
        return null;
    }
}
