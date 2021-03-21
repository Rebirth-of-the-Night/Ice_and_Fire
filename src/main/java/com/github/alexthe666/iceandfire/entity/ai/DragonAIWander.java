package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DragonAIWander extends EntityAIBase {
    private final EntityDragonBase dragon;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int executionChance;
    private boolean mustUpdate;

    public DragonAIWander(EntityDragonBase creatureIn, double speedIn) {
        this(creatureIn, speedIn, 20);
    }

    public DragonAIWander(EntityDragonBase creatureIn, double speedIn, int chance) {
        this.dragon = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!dragon.canMove()) {
            return false;
        }
        if (dragon.getControllingPassenger() != null) {
            return false;
        }
        if (dragon.isFlying() || dragon.isHovering()) {
            return false;
        }
        if (!this.mustUpdate) {
            if (this.dragon.getRNG().nextInt(executionChance) != 0) {
                return false;
            }
        }
        Vec3d vec3d;
        while (true) {
            vec3d = RandomPositionGenerator.findRandomTarget(this.dragon, 10, 7);
            if (vec3d == null) return false;

            BlockPos pos = new BlockPos(vec3d);
            Material material = dragon.world.getBlockState(pos).getMaterial();
            Material materialBelow = dragon.world.getBlockState(pos.down()).getMaterial();
            Material materialBelowBelow = dragon.world.getBlockState(pos.down(2)).getMaterial();

            if (!(material.isLiquid() || materialBelow.isLiquid() || materialBelowBelow.isLiquid())) break;
        }
        this.xPosition = vec3d.x;
        this.yPosition = vec3d.y;
        this.zPosition = vec3d.z;
        this.mustUpdate = false;

        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.dragon.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.dragon.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}