package com.github.alexthe666.iceandfire.block.keletu;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BlockGraveyardSoil extends Block {

    public BlockGraveyardSoil() {

        super(Material.GROUND);
        this.setTranslationKey("iceandfire.graveyard_soil");
        this.setHardness(5);
        this.setResistance(1F);
        this.setLightLevel(0.0F);
        this.setSoundType(SoundType.GROUND);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        setTickRandomly(true);
        setRegistryName(IceAndFire.MODID, "graveyard_soil");
    }


    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (!worldIn.isDaytime() && !worldIn.getBlockState(pos.up()).causesSuffocation() && rand.nextInt(9) == 0 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
                int checkRange = 32;
                int k = worldIn.getEntitiesWithinAABB(EntityGhost.class, (new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).grow(checkRange)).size();
                if (k < 10) {
                    EntityGhost ghost = new EntityGhost(worldIn);
                    ghost.moveToBlockPosAndAngles(pos.add(0.5, 0.5, 0.5),
                        ThreadLocalRandom.current().nextFloat() * 360F, 0);
                    if (!worldIn.isRemote) {
                        ghost.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);
                        worldIn.spawnEntity(ghost);
                    }
                    ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
                    ghost.setHomePosAndDistance(pos, 16);
                }
            }
        }
    }
}