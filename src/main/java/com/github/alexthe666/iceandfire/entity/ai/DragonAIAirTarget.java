package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DragonAIAirTarget extends EntityAIBase {
	private EntityDragonBase dragon;

	public DragonAIAirTarget(EntityDragonBase dragon) {
		this.dragon = dragon;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if (!dragon.isFlying() && !dragon.isHovering() || dragon.onGround) {
			return false;
		}
		if (dragon.isSleeping()) {
			return false;
		}
		if (dragon.isChild()) {
			return false;
		}
		if (dragon.isPlayerControlled()) {
			return false;
		}

		if (dragon.airTarget == null || dragon.isTargetBlocked(new Vec3d(dragon.airTarget))) {
			EntityLivingBase attackTarget = dragon.getAttackTarget();
			if (attackTarget != null) {
				dragon.airTarget = new BlockPos(attackTarget.posX, attackTarget.posY, attackTarget.posZ);
				return true;
			}

			BlockPos pos = this.getNearbyAirTarget();
			if (pos == null) {
				dragon.airTarget = null;
				return false;
			}
			Vec3d vec = new Vec3d(pos);
			dragon.airTarget = new BlockPos(vec.x, vec.y, vec.z);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!dragon.isFlying() && !dragon.isHovering()) {
			return false;
		}
		if (dragon.isSleeping()) {
			return false;
		}
		if (dragon.isChild()) {
			return false;
		}

		StoneEntityProperties capability = EntityPropertiesHandler.INSTANCE.getProperties(dragon, StoneEntityProperties.class);
		if (capability != null && capability.isStone) {
			return false;
		}
		EntityLivingBase attackTarget = dragon.getAttackTarget();
		if (attackTarget != null) {
			dragon.airTarget = new BlockPos(attackTarget.posX, attackTarget.posY, attackTarget.posZ);
			return true;
		}
		if (dragon.airTarget != null) {
			if (dragon.isTargetBlocked(new Vec3d(dragon.airTarget))) {
				dragon.airTarget = null;
				return false;
			}
			return true;
		}
		return false;
	}

	public BlockPos getNearbyAirTarget() {
		BlockPos pos = DragonUtils.getBlockInView(dragon);
		if (pos != null && dragon.world.getBlockState(pos).getMaterial() == Material.AIR) {
			return pos;
		}
		return null;
	}
}