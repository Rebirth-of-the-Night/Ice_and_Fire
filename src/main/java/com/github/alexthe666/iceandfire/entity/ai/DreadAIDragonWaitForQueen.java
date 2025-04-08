package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.entity.ai.EntityAIBase;

public class DreadAIDragonWaitForQueen extends EntityAIBase {
    private final EntityBlackFrostDragon dragon;
    private EntityDreadQueen queen;

    public DreadAIDragonWaitForQueen(EntityBlackFrostDragon dragon) {
        this.dragon = dragon;
        this.setMutexBits(0);
    }

    public boolean shouldExecute() {
        if (this.dragon.getRidingQueen() != null) {
            this.queen = dragon.getRidingQueen();
        }

        return this.queen != null || this.dragon.isAwaken();
    }

    public boolean shouldContinueExecuting() {
        return this.dragon.isAwaken();
    }

    public void startExecuting() {
        if (this.queen != null)
            this.queen.getNavigator().clearPath();
        this.dragon.setFlying(true);
        this.dragon.setLeaping(true);
        this.dragon.setAwaken(true);
    }

    public void resetTask() {
        this.queen = null;
        this.dragon.getNavigator().clearPath();
    }

    public void updateTask() {
        this.dragon.doRoboty();
    }
}