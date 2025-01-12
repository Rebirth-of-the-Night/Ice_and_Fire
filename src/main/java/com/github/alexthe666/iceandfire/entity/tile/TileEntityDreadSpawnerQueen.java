package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityDreadSpawnerQueen extends TileEntity implements ITickable {

	private int activatingRangeFromPlayer = 10;

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
			EntityDreadQueen queen = new EntityDreadQueen(world);
			queen.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			queen.onInitialSpawn(world.getDifficultyForLocation(pos), null);
			world.spawnEntity(queen);
			world.setBlockToAir(pos);
		}
	}
}