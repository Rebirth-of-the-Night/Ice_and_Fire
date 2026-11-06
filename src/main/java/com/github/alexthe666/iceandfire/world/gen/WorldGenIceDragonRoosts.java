package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.HomePosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WorldGenIceDragonRoosts extends WorldGenerator {
    private BlockPos lastGeneratedPosition;

    public BlockPos getLastGeneratedPosition() {
        return lastGeneratedPosition;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        return generate(worldIn, rand, position, candidate -> true);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position, Predicate<BlockPos> allowed) {
        return generate(worldIn, rand, position, allowed, Collections.emptyList());
    }

    public boolean generate(World worldIn, Random rand, BlockPos position, Predicate<BlockPos> allowed,
                            List<BlockPos> nearbySites) {
        lastGeneratedPosition = null;
        boolean isMale = rand.nextBoolean();
        BlockPos origin = position;
        DragonRoostTerrain.Site site = DragonRoostTerrain.findSite(worldIn, origin, 12 + rand.nextInt(8),
            candidate -> worldIn.getBiome(candidate) == worldIn.getBiome(origin) && allowed.test(candidate), nearbySites);
        if (site == null) {
            return false;
        }
        position = site.position;
        DragonRoostRuins.generate(worldIn, rand, site, DragonRoostRuins.Palette.ICE, isMale);
        {
            EntityIceDragon dragon = new EntityIceDragon(worldIn);
            dragon.setGender(isMale);
            dragon.growDragon(53 + site.radius);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(new Random().nextInt(4));
            dragon.setPositionAndRotation(position.getX() + 0.5, worldIn.getHeight(position).getY() + 1.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
            dragon.homePos = new HomePosition(position, worldIn);
            dragon.hasHomePosition = true;
            dragon.setSleeping(true);
            dragon.setHunger(IceAndFire.CONFIG.dragonStartingHunger);
            worldIn.spawnEntity(dragon);
        }

        lastGeneratedPosition = position;
        return true;
    }
}
