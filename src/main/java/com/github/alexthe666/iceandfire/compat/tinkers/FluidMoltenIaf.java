package com.github.alexthe666.iceandfire.compat.tinkers;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import slimeknights.tconstruct.library.fluid.FluidMolten;

public class FluidMoltenIaf extends FluidMolten {
    public FluidMoltenIaf(String fluidName, int color) {
        super(fluidName, color);
    }

    @Override
    public Fluid setBlock(Block block) {
        return null;
    }
}
