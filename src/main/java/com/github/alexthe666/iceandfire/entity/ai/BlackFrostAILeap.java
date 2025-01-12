package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityAIBase;

public class BlackFrostAILeap extends EntityAIBase {


    private final EntityBlackFrostDragon dragon;
    public BlackFrostAILeap(EntityBlackFrostDragon dragon) {
        this.dragon = dragon;
    }
    @Override
    public boolean shouldExecute() {
        return dragon.posY < 190 && !dragon.isModelDead();
    }

    @Override
    public void updateTask() {
        if(!dragon.isFlying()){
            dragon.setFlying(true);
        }
        dragon.move(MoverType.SELF, 0, 1, 0);
    }
}
