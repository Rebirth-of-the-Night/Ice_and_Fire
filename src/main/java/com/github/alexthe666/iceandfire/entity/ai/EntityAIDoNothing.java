package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityAutomatonFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIDoNothing extends EntityAIBase {
    protected EntityLiving entity;

    public EntityAIDoNothing(EntityLiving entityIn) {
        this.entity = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity instanceof EntityAutomatonFlying && !((EntityAutomatonFlying)entity).isAwake();
    }

    public void updateTask() {}
}