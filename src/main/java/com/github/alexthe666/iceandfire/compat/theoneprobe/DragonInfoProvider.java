package com.github.alexthe666.iceandfire.compat.theoneprobe;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DragonInfoProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return "iceandfire.dragon_info";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (entity instanceof EntityDragonBase) {
            EntityDragonBase dragon = (EntityDragonBase) entity;

            if (dragon.isTamed() && dragon.getOwner() != null) {
                probeInfo.text(localize("dragon.owner") + " " + dragon.getOwner().getName());
            } else {
                probeInfo.text(localize("dragon.untamed"));
            }
            probeInfo.text(localize("dragon.gender") + " " + localize(dragon.isMale() ? "dragon.gender.male" : "dragon.gender.female"));
            probeInfo.text(localize("dragon.stage") + " " + dragon.getDragonStage());
        }
    }

    private String localize(String text) {
        return IProbeInfo.STARTLOC + text + IProbeInfo.ENDLOC;
    }
}
