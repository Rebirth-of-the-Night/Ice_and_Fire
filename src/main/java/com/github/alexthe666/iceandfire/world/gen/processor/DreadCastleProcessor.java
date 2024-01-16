package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.BlockDreadBase;
import com.github.alexthe666.iceandfire.block.BlockDreadStoneFace;
import com.github.alexthe666.iceandfire.block.IDreadBlock;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;

public class DreadCastleProcessor implements ITemplateProcessor {

    private float integrity = 1.0F;
    public static final ResourceLocation DREAD_CHEST_LOOT_TRASH = LootTableList.register(new ResourceLocation("iceandfire", "dredcastlebasic1"));
    public static final ResourceLocation DREAD_CHEST_LOOT_NORMAL = LootTableList.register(new ResourceLocation("iceandfire", "dredcastleloot1"));
    public static final ResourceLocation DREAD_CHEST_LOOT_NORMAL2 = LootTableList.register(new ResourceLocation("iceandfire", "dredcastleloot2"));
    public static final ResourceLocation DREAD_CHEST_LOOT_NORMAL3 = LootTableList.register(new ResourceLocation("iceandfire", "dredcastleloot3"));
    public static final ResourceLocation DREAD_CHEST_LOOT_VALUABLE = LootTableList.register(new ResourceLocation("iceandfire", "dredcastlecitadel1"));
    public static final ResourceLocation DREAD_CHEST_LOOT_QUEEN = LootTableList.register(new ResourceLocation("iceandfire", "dredcastlequeen1"));
    public static final ResourceLocation DREAD_CHEST_LOOT_LIBRARY = LootTableList.register(new ResourceLocation("iceandfire", "dredcastleloot5"));
    public static final ResourceLocation DREAD_CHEST_LOOT_TREASURE = LootTableList.register(new ResourceLocation("iceandfire", "treasureroom1"));
    public static final ResourceLocation DREAD_CHEST_LOOT_MAGIC = LootTableList.register(new ResourceLocation("iceandfire", "dredcastlemagic1"));
    public static final ResourceLocation LOOT_TOWER_KEY = LootTableList.register(new ResourceLocation("iceandfire", "towerkey1"));
    public static final ResourceLocation LOOT_ACCESS_KEY = LootTableList.register(new ResourceLocation("iceandfire", "toweraccesskey1"));
    public static final ResourceLocation LOOT_QUEEN_KEY = LootTableList.register(new ResourceLocation("iceandfire", "queenkey1"));
    public static final ResourceLocation LOOT_LIBRARY_KEY = LootTableList.register(new ResourceLocation("iceandfire", "librarykey1"));
    public static final ResourceLocation LOOT_SPECIAL_KEY = LootTableList.register(new ResourceLocation("iceandfire", "specialkey1"));
    public static final ResourceLocation LOOT_TREASURY_PEDESTAL = LootTableList.register(new ResourceLocation("iceandfire", "treasurepedestal1"));

    public DreadCastleProcessor() {
    }

    protected Template.BlockInfo getSpawnedMob(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn, Class<? extends Entity> entity){
            NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
            NBTTagCompound spawnData = new NBTTagCompound();
            ResourceLocation spawnerMobId = EntityList.getKey(entity);
            if(spawnerMobId != null){
                spawnData.setString("id", spawnerMobId.toString());
                tag.removeTag("SpawnPotentials");
                tag.setTag("SpawnData", spawnData.copy());
            }
        return new Template.BlockInfo(pos, IafBlockRegistry.dread_spawner.getDefaultState(), tag);
    }

    protected Template.BlockInfo getLootTable(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn, ResourceLocation loot){
        Random rand = new Random(worldIn.getSeed() + pos.toLong());
        NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
        tag.setString("LootTable", loot.toString());
        tag.setLong("LootTableSeed", rand.nextLong());
        return new Template.BlockInfo(pos, Blocks.CHEST.getDefaultState(), tag);
    }

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        Block block = blockInfoIn.blockState.getBlock();

        if (worldIn.rand.nextFloat() <= integrity) {
            if (block instanceof IDreadBlock) {

                if(block == IafBlockRegistry.dread_stone_bricks){
                    IBlockState state = getRandomCrackedBlock(null, worldIn.rand);
                    return new Template.BlockInfo(pos, state, null);
                }

                if(block instanceof BlockDreadBase)
                {
                    return new Template.BlockInfo(pos, block.getDefaultState(), null);
                }

                if(block instanceof BlockDreadStoneFace) {
                    return new Template.BlockInfo(pos, IafBlockRegistry.dread_stone_face.getStateFromMeta(4), null);
                }
            }
            if (block == Blocks.PISTON) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_piston.getStateFromMeta(block.getMetaFromState(blockInfoIn.blockState)), null);
            }
            if (block == Blocks.STICKY_PISTON) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_sticky_piston.getStateFromMeta(block.getMetaFromState(blockInfoIn.blockState)), null);
            }
            if (block == Blocks.PISTON_HEAD) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_piston_head.getStateFromMeta(block.getMetaFromState(blockInfoIn.blockState)), null);
            }
            if (block == Blocks.PISTON_EXTENSION) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_piston_moving.getStateFromMeta(block.getMetaFromState(blockInfoIn.blockState)), null);
            }
            if (block instanceof BlockChest) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_TRASH);
            }
            if (block == Blocks.CLAY) {NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
                NBTTagCompound spawnData = new NBTTagCompound();
                EntityDreadHorse horse = new EntityDreadHorse(worldIn);
                horse.updatePassenger(new EntityDreadKnight(worldIn));
                ResourceLocation spawnerMobId = EntityList.getKey(horse);
                if(spawnerMobId != null){
                    spawnData.setString("id", spawnerMobId.toString());
                    tag.removeTag("SpawnPotentials");
                    tag.setTag("SpawnData", spawnData.copy());
                }

                return new Template.BlockInfo(pos, IafBlockRegistry.dread_spawner.getDefaultState(), tag);
            }
            if (block == Blocks.LAPIS_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadThrall.class);
            }
            if (block == Blocks.DIAMOND_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadKnight.class);
            }
            if (block == Blocks.GOLD_ORE) {
                Random rand = new Random(worldIn.getSeed() + pos.toLong());
                return getSpawnedMob(worldIn, pos, blockInfoIn, getRandomMobForMobSpawner(rand));
            }
            if (block == Blocks.ENDER_CHEST) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadGhoul.class);
            }
            if (block == Blocks.END_PORTAL_FRAME) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadScuttler.class);
            }
            if (block == Blocks.MOSSY_COBBLESTONE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadBeast.class);
            }
            if (block == Blocks.IRON_ORE) {
                if(worldIn.rand.nextInt(2) < 1)
                    return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadBeast.class);
                else
                    return new Template.BlockInfo(pos, Blocks.AIR.getDefaultState(), null);
            }
            if (block == Blocks.REDSTONE_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadKnightRoyal.class);
            }
            if (block == Blocks.LIT_PUMPKIN) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_lich.getDefaultState(), null);
            }
            if (block == Blocks.BRICK_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tripwire.getDefaultState(), null);
            }
            if (block == Blocks.EMERALD_ORE) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tripwire_dragon.getDefaultState(), null);
            }
            if (block == Blocks.BEDROCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_queen.getDefaultState(), null);
            }
            if (block == Blocks.END_ROD) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_dragon.getDefaultState(), null);
            }
            if (block == Blocks.PURPUR_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_ballista.getDefaultState(), null);
            }
            if (block == Blocks.NETHERRACK) {
                if(worldIn.rand.nextInt(2) < 1)
                    return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_NORMAL);
                else
                    return new Template.BlockInfo(pos, Blocks.AIR.getDefaultState(), null);
            }
            if (blockInfoIn.blockState == Blocks.STONE.getStateFromMeta(6)) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_NORMAL2);
            }
            if (blockInfoIn.blockState == Blocks.STONE.getStateFromMeta(2)) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_NORMAL3);
            }
            if (blockInfoIn.blockState == Blocks.SPONGE.getStateFromMeta(1)) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_VALUABLE);
            }
            if (block == Blocks.NETHER_BRICK) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_QUEEN);
            }
            if (block == IafBlockRegistry.sapphireOre) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_LIBRARY);
            }
            if (block == Blocks.MONSTER_EGG) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_TREASURE);
            }
            if (blockInfoIn.blockState == IafBlockRegistry.myrmex_resin_sticky.getStateFromMeta(1)) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_MAGIC);
            }
            if (block == Blocks.MYCELIUM) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_TOWER_KEY);
            }
            if (block == IafBlockRegistry.myrmex_desert_resin_block) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_ACCESS_KEY);
            }
            if (block == IafBlockRegistry.myrmex_resin) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_QUEEN_KEY);
            }
            if (block == IafBlockRegistry.jungle_myrmex_cocoon) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_LIBRARY_KEY);
            }
            if (block == Blocks.DIAMOND_BLOCK || block == Blocks.QUARTZ_ORE || block == Blocks.RED_NETHER_BRICK || block == Blocks.EMERALD_BLOCK) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_SPECIAL_KEY);
            }
            if (block == Blocks.MELON_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tower_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState == IafBlockRegistry.myrmex_resin_sticky.getStateFromMeta(0)) {
                return new Template.BlockInfo(pos, IafBlockRegistry.queen_keyhole.getDefaultState(), null);
            }
            if (block == IafBlockRegistry.myrmex_jungle_resin_block) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tower_access_keyhole.getDefaultState(), null);
            }
            if (block == IafBlockRegistry.silverBlock) {
                return new Template.BlockInfo(pos, IafBlockRegistry.library_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState == Blocks.QUARTZ_BLOCK.getStateFromMeta(1) || block == Blocks.MAGMA || block == Blocks.LAPIS_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.special_keyhole.getDefaultState(), null);
            }
            if (block == Blocks.CONCRETE_POWDER) {
                return new Template.BlockInfo(pos, IafBlockRegistry.treasury_keyhole.getDefaultState(), null);
            }
            if(block == Blocks.YELLOW_GLAZED_TERRACOTTA){
                    TileEntityPodium tile = new TileEntityPodium();
                    LootTable table = worldIn.getLootTableManager().getLootTableFromLocation(DreadCastleProcessor.LOOT_TREASURY_PEDESTAL);

                    LootContext context = new LootContext.Builder((WorldServer) worldIn).build();
                    // imagine using IInventory in 2020
                    tile.setInventorySlotContents(0, table.generateLootForPools(worldIn.rand, context).get(0));
                return new Template.BlockInfo(pos, IafBlockRegistry.podium.getStateFromMeta(1), tile.getUpdateTag());
            }
            return blockInfoIn;
        }
        return blockInfoIn;

    }

    private Class<? extends Entity> getRandomMobForMobSpawner(Random random) {
        float rand = random.nextFloat();
        if(rand < 0.3D){
            return EntityDreadThrall.class;
        }else if(rand < 0.5D){
            return EntityDreadGhoul.class;
        }else if(rand < 0.7D){
            return EntityDreadBeast.class;
        }else if(rand < 0.85D){
            return EntityDreadScuttler.class;
        }
        return EntityDreadKnight.class;
    }

    public static IBlockState getRandomCrackedBlock(@Nullable IBlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return IafBlockRegistry.dread_stone_bricks.getDefaultState();
        } else if (rand < 0.9) {
            return IafBlockRegistry.dread_stone_bricks_cracked.getDefaultState();
        } else {
            return IafBlockRegistry.dread_stone_bricks_mossy.getDefaultState();
        }
    }
}
