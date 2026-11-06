package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

final class DragonRoostRuins {
    enum Palette {
        FIRE(IafBlockRegistry.charedGrass, IafBlockRegistry.charedDirt, IafBlockRegistry.charedStone,
            IafBlockRegistry.charedCobblestone, IafBlockRegistry.ash, IafBlockRegistry.goldPile,
            WorldGenFireDragonCave.FIREDRAGON_CHEST),
        ICE(IafBlockRegistry.frozenGrass, IafBlockRegistry.frozenDirt, IafBlockRegistry.frozenStone,
            IafBlockRegistry.frozenCobblestone, IafBlockRegistry.dragon_ice, IafBlockRegistry.silverPile,
            WorldGenIceDragonCave.ICEDRAGON_CHEST),
        LIGHTNING(IafBlockRegistry.crackledGrass, IafBlockRegistry.crackledDirt, IafBlockRegistry.crackledStone,
            IafBlockRegistry.crackledCobblestone, IafBlockRegistry.ash, IafBlockRegistry.copperPile,
            WorldGenLightningDragonCave.LIGHTNINGDRAGON_CHEST);

        final IBlockState grass;
        final IBlockState dirt;
        final IBlockState stone;
        final IBlockState cobblestone;
        final IBlockState debris;
        final Block hoard;
        final ResourceLocation lootTable;

        Palette(Block grass, Block dirt, Block stone, Block cobblestone, Block debris, Block hoard,
                ResourceLocation lootTable) {
            this.grass = grass.getDefaultState();
            this.dirt = dirt.getDefaultState();
            this.stone = stone.getDefaultState();
            this.cobblestone = cobblestone.getDefaultState();
            this.debris = debris.getDefaultState();
            this.hoard = hoard;
            this.lootTable = lootTable;
        }
    }

    private DragonRoostRuins() { }

    static int outerRadius(int radius) {
        // Increase the horizontal footprint by about 80% while keeping the treasure room the same size.
        return (int) Math.round(DragonRoostTerrain.floorRadius(radius + 13) * 1.35);
    }

    static void generate(World world, Random rand, DragonRoostTerrain.Site site, Palette palette, boolean male) {
        BlockPos center = site.position;
        int roomRadius = DragonRoostTerrain.floorRadius(site.radius);
        int nestRadius = roomRadius - 2;
        int outerRadius = outerRadius(site.radius);
        DragonRoostTerrain.clearRoom(world, center, roomRadius);
        // About 40% of roosts are charred nests instead of ruined stone-brick halls.
        boolean nest = rand.nextInt(5) < 2;

        NoiseGeneratorPerlin damage = new NoiseGeneratorPerlin(rand, 1);
        List<BlockPos> treasureSpots = new ArrayList<>();
        for (int x = -outerRadius; x <= outerRadius; x++) {
            for (int z = -outerRadius; z <= outerRadius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > outerRadius) continue;

                BlockPos column = center.add(x, 0, z);
                double patch = damage.getValue(x * 0.16, z * 0.16);
                if (distance <= roomRadius) {
                    boolean inNest = distance <= nestRadius;
                    BlockPos floor = inNest ? column.down() : column;
                    DragonRoostTerrain.levelFloor(world, floor);
                    double scorchThreshold = inNest ? 0.5 * distance / nestRadius - 0.34 : 0.45;
                    double dirtThreshold = inNest ? 0.65 : 0.85;
                    boolean scarred = patch > scorchThreshold;
                    IBlockState paving = patch > dirtThreshold ? palette.dirt : scarred ? palette.cobblestone : rubble(rand, palette, nest);
                    world.setBlockState(floor, paving, 2);
                    if (distance > roomRadius - 1.5 && patch > 0.28) {
                        int height = patch > 0.8 ? 4 : patch > 0.5 ? 3 : 2;
                        for (int y = 1; y <= height; y++) {
                            BlockPos remnant = column.up(y);
                            if (!world.isAirBlock(remnant)) break;
                            world.setBlockState(remnant, scarred ? palette.cobblestone : rubble(rand, palette, nest), 2);
                        }
                    }
                    // Leave room for the dragon in the center and keep pile edges inside the floor.
                    if (distance > 3 && distance <= roomRadius - 3 && !scarred && world.isAirBlock(floor.up())) {
                        treasureSpots.add(floor.up());
                    }
                } else {
                    double progress = (distance - roomRadius) / (outerRadius - roomRadius);
                    weatherGround(world, rand, column, palette, progress, patch, nest);
                }
            }
        }

        addOuterWalls(world, rand, center, roomRadius, outerRadius, palette, nest);
        addTreasure(world, rand, treasureSpots, roomRadius, palette, male);
    }

    /** Ruined masonry for hall-style roosts, charred earth and stone for nest-style ones. */
    private static IBlockState rubble(Random rand, Palette palette, boolean nest) {
        if (!nest) return masonry(rand);
        int block = rand.nextInt(10);
        if (block < 4) return palette.cobblestone;
        if (block < 6) return palette.dirt;
        if (block < 8) return palette.stone;
        return palette.debris;
    }

    private static IBlockState masonry(Random rand) {
        int block = rand.nextInt(10);
        if (block < 3) return Blocks.COBBLESTONE.getDefaultState();
        if (block == 3) return Blocks.MOSSY_COBBLESTONE.getDefaultState();
        BlockStoneBrick.EnumType variant = block < 7 ? BlockStoneBrick.EnumType.CRACKED : BlockStoneBrick.EnumType.DEFAULT;
        return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, variant);
    }

    private static void weatherGround(World world, Random rand, BlockPos column, Palette palette,
                                      double progress, double patch, boolean nest) {
        BlockPos surface = DragonRoostTerrain.surface(world, column);
        BlockPos ground = surface.down();
        IBlockState base = world.getBlockState(ground);
        if (!base.isFullCube() || base.getBlockHardness(world, ground) < 0 || world.getTileEntity(ground) != null) return;

        // Damage builds outward, then fades back into the landscape at the fringe.
        double fade = Math.max(0, (progress - 0.72) / 0.28);
        boolean scarred = patch > 0.78 - 0.95 * progress + 1.45 * fade;
        if (scarred) {
            Material material = base.getMaterial();
            IBlockState damaged = palette.dirt;
            if (material == Material.ROCK || material == Material.ICE || material == Material.PACKED_ICE) {
                damaged = base.getBlock() == Blocks.COBBLESTONE ? palette.cobblestone : palette.stone;
            } else if (material == Material.GRASS && rand.nextBoolean()) {
                damaged = palette.grass;
            }
            world.setBlockState(ground, damaged, 2);
        }
        if (!canPlaceDebris(world, surface)) return;

        if (patch > 0.15 && rand.nextInt(6) == 0 && rand.nextDouble() >= fade) {
            world.setBlockState(surface, scarred ? palette.cobblestone : rubble(rand, palette, nest), 2);
        } else if (scarred && rand.nextInt(14) == 0) {
            world.setBlockState(surface, palette.debris, 2);
        }
    }

    private static boolean canPlaceDebris(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return pos.getY() < world.getHeight() && world.getTileEntity(pos) == null
            && (world.isAirBlock(pos) || block == Blocks.SNOW_LAYER || block == Blocks.TALLGRASS);
    }

    private static void addOuterWalls(World world, Random rand, BlockPos center, int roomRadius, int outerRadius,
                                      Palette palette, boolean nest) {
        double start = rand.nextDouble() * 2 * Math.PI;
        for (int i = 0; i < 5; i++) {
            double angle = start + (i + rand.nextDouble() * 0.55) * 2 * Math.PI / 5;
            double distance = roomRadius + (outerRadius - roomRadius) * (0.55 + rand.nextDouble() * 0.22);
            BlockPos anchor = center.add(Math.round(Math.cos(angle) * distance), 0, Math.round(Math.sin(angle) * distance));
            EnumFacing along = Math.abs(Math.cos(angle)) > Math.abs(Math.sin(angle)) ? EnumFacing.NORTH : EnumFacing.EAST;
            int length = 3 + rand.nextInt(4);
            int anchorY = DragonRoostTerrain.surface(world, anchor).getY();
            for (int j = 0; j < length; j++) {
                BlockPos base = DragonRoostTerrain.surface(world, anchor.offset(along, j - length / 2));
                if (Math.abs(base.getY() - anchorY) > 1 || !world.getBlockState(base.down()).isFullCube()) continue;
                int height = j == length / 2 ? 2 + rand.nextInt(2) : 1 + rand.nextInt(2);
                for (int y = 0; y < height && canPlaceDebris(world, base.up(y)); y++) {
                    world.setBlockState(base.up(y), rand.nextInt(4) == 0 ? rubble(rand, palette, nest) : palette.cobblestone, 2);
                }
            }
        }
    }

    private static void addTreasure(World world, Random rand, List<BlockPos> spots, int roomRadius,
                                    Palette palette, boolean male) {
        Collections.shuffle(spots, rand);
        int chests = roomRadius * roomRadius / (male ? 30 : 40);
        int placedChests = 0;
        int spot = 0;
        for (; spot < spots.size() && placedChests < chests; spot++) {
            BlockPos pos = spots.get(spot);
            if (!Blocks.CHEST.canPlaceBlockAt(world, pos)) continue;
            EnumFacing facing = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
            world.setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing), 2);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityChest) {
                ((TileEntityChest) tile).setLootTable(palette.lootTable, rand.nextLong());
                placedChests++;
            }
        }
        WorldGenRoostGoldPile pile = new WorldGenRoostGoldPile(palette.hoard);
        int piles = roomRadius * roomRadius / (male ? 18 : 25);
        for (int i = 0; i < piles && spot < spots.size(); i++, spot++) {
            pile.generate(world, rand, spots.get(spot));
        }
    }
}
