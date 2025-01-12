package com.github.alexthe666.iceandfire.block.keletu;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IDreadBlock;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSingleUseSpawner;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSingleUseSpawnerBallista;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSingleUseSpawnerDragon;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawnerQueen;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class BlockDreadSingleMobSpawner extends Block implements IDreadBlock {

	public BlockDreadSingleMobSpawner(String name) {
		super(Material.ROCK);
		this.setHardness(9.0F);
		this.setResistance(10000F);
		this.setTranslationKey("iceandfire." + name);
		this.setSoundType(SoundType.METAL);
		this.setRegistryName(IceAndFire.MODID, name);
		GameRegistry.registerTileEntity(TileEntityDreadSingleUseSpawner.class, new ResourceLocation(IceAndFire.MODID, "dread_spawner_single"));
		GameRegistry.registerTileEntity(TileEntityDreadSingleUseSpawnerDragon.class, new ResourceLocation(IceAndFire.MODID, "dread_spawner_single_dragon"));
		GameRegistry.registerTileEntity(TileEntityDreadSingleUseSpawnerBallista.class, new ResourceLocation(IceAndFire.MODID, "dread_spawner_single_ballista"));
		GameRegistry.registerTileEntity(TileEntityDreadSpawnerQueen.class, new ResourceLocation(IceAndFire.MODID, "dread_spawner_single_queen"));
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_lich)
			return new TileEntityDreadSingleUseSpawner();
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_queen)
			return new TileEntityDreadSpawnerQueen();
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_dragon)
			return new TileEntityDreadSingleUseSpawnerDragon();
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_ballista)
			return new TileEntityDreadSingleUseSpawnerBallista();
		return null;
	}
}