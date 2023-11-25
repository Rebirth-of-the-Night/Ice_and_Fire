package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSingleUseSpawner;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSingleUseSpawnerDragon;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
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
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
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
		return state.getBlock() != IafBlockRegistry.dread_single_spawner_queen;
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		if(!worldIn.isRemote) {
			EntityDreadQueen queen = new EntityDreadQueen(worldIn);
			queen.setPosition(pos.getX(), pos.getY() + 3, pos.getZ());
			queen.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);

			if (state.getBlock() == IafBlockRegistry.dread_single_spawner_queen) {
				worldIn.spawnEntity(queen);
				if (worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.dread_single_spawner_dragon) {
					worldIn.destroyBlock(pos, false);
				}
			}
		}

	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_lich)
			return new TileEntityDreadSingleUseSpawner();
		if(state.getBlock() == IafBlockRegistry.dread_single_spawner_dragon)
			return new TileEntityDreadSingleUseSpawnerDragon();
		return null;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
}