package com.github.alexthe666.iceandfire.compat.theoneprobe;

import java.util.function.Function;

import net.minecraftforge.fml.common.event.FMLInterModComms;

import mcjty.theoneprobe.api.ITheOneProbe;

public class IceAndFireOneProbeCompat implements Function<ITheOneProbe, Void>
{
    public static void register() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", IceAndFireOneProbeCompat.class.getName());
    }

    @Override
    public Void apply(ITheOneProbe input)
    {
        input.registerEntityProvider(new DragonInfoProvider());
        return null;
    }
}