package com.github.alexthe666.iceandfire.entity.util;

import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

// RLCombat uses this class and getParent() for Ice and Fire 2.x.
public abstract class EntityMultipartPart extends PartEntity {

    protected EntityMultipartPart(EntityLiving parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    public EntityLivingBase getParent() {
        return this.parent;
    }
}
