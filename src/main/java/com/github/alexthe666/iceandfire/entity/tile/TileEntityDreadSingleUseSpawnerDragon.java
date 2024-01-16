package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.entity.EntityBlackFrostDragon;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityDreadSingleUseSpawnerDragon extends TileEntity implements ITickable {

	private int activatingRangeFromPlayer = 16;

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			world.tickableTileEntities.remove(this);
	}

	@Override
	public void update() {
		for (EntityDreadQueen queen : world.getEntitiesWithinAABB(EntityDreadQueen.class, new AxisAlignedBB((double) pos.getX() - 50, (double) pos.getY() - 50, (double) pos.getZ() - 50, (double) pos.getX() + 50, (double) pos.getY() + 50, (double) pos.getZ() + 50))) {

			if (queen != null) {
				EntityBlackFrostDragon blackFrost = new EntityBlackFrostDragon(world);
				blackFrost.setHealth(500);
				blackFrost.setOwnerId(queen.getUniqueID());
				blackFrost.setPosition(pos.getX(), pos.getY(), pos.getZ());
				blackFrost.onInitialSpawn(world.getDifficultyForLocation(pos), null);
				world.spawnEntity(blackFrost);
				world.setBlockToAir(pos);
			}
		}
	}
}