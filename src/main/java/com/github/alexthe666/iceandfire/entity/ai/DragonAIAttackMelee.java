package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class DragonAIAttackMelee extends EntityAIAttackMelee {

    private final EntityDragonBase dragon;

    public DragonAIAttackMelee(EntityDragonBase dragon, double speedIn, boolean useLongMemory) {
        super(dragon, speedIn, useLongMemory);
        this.dragon = dragon;
    }

    @Override
    public boolean shouldExecute() {
        return this.dragon.canMove() && super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.dragon.canMove() && super.shouldExecute();
    }
}