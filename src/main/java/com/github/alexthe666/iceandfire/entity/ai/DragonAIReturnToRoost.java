package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.util.IAFMath;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class DragonAIReturnToRoost extends EntityAIBase {
    private final EntityDragonBase dragon;
    private final double movementSpeed;
    private Path path;
    
    public DragonAIReturnToRoost(EntityDragonBase entityIn, double movementSpeedIn) {
        this.dragon = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);
    }

	@Override
	public boolean shouldExecute() {
		return this.dragon.canMove() && this.dragon.lookingForRoostAIFlag && (dragon.getAttackTarget() == null || !dragon.getAttackTarget().isEntityAlive()) && dragon.getHomePosition() != null && dragon.getDistanceSquared(IAFMath.copyCentered(dragon.getHomePosition())) > dragon.width * dragon.width;
	}
	
    @Override
    public void updateTask() {
        if (this.dragon.getHomePosition() != null) {
            double dist = Math.sqrt(dragon.getDistanceSquared(IAFMath.copyCentered(dragon.getHomePosition())));
            double xDist = Math.abs(dragon.posX - dragon.getHomePosition().getX() - 0.5F);
            double zDist = Math.abs(dragon.posZ - dragon.getHomePosition().getZ() - 0.5F);
            double xzDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if (dist < this.dragon.width) {
                this.dragon.setFlying(false);
                this.dragon.setHovering(false);
                this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1.0F);
            }else {
                double yAddition = 15 + dragon.getRNG().nextInt(3);
                if(xzDist < 40){
                    yAddition = 0;
                    if(this.dragon.onGround){
                        this.dragon.setFlying(false);
                        this.dragon.setHovering(false);
                        this.dragon.flightManager.setTarget(IAFMath.copyCenteredWithVerticalOffset(this.dragon.getHomePosition(), yAddition));
                        this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1.0F);
                        return;
                    }
                }
                if(!this.dragon.isFlying() && !this.dragon.isHovering() && xzDist > 40){
                    this.dragon.setHovering(true);
                }
                if(this.dragon.isFlying()){
                    this.dragon.flightManager.setTarget(IAFMath.copyCenteredWithVerticalOffset(this.dragon.getHomePosition(), yAddition));
                    this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), yAddition + this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1F);
                }
                this.dragon.flyTicks = 0;
            }
        }
    }
}