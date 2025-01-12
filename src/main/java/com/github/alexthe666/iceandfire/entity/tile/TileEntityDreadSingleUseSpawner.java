package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.entity.EntityDreadLich;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityDreadSingleUseSpawner extends TileEntity implements ITickable {

	private int activatingRangeFromPlayer = 16;

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			world.tickableTileEntities.remove(this);
	}

	public boolean isActivated() {
		return world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, activatingRangeFromPlayer, true) != null;
	}

	@Override
	public void update() {
		if (isActivated()) {
			EntityDreadLich mob = new EntityDreadLich(world);
			mob.setPosition(pos.getX(), pos.getY(), pos.getZ());
			mob.onInitialSpawn(world.getDifficultyForLocation(pos), null);
			world.spawnEntity(mob);
			world.setBlockToAir(pos);
		}
	}
}