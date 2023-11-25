package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
            Template.BlockInfo newInfo = new Template.BlockInfo(pos, IafBlockRegistry.dread_spawner.getDefaultState(), tag);
            return newInfo;
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
        if (worldIn.rand.nextFloat() <= integrity) {
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.dread_stone_bricks) {
                IBlockState state = getRandomCrackedBlock(null, worldIn.rand);
                return new Template.BlockInfo(pos, state, null);
            }
            if (blockInfoIn.blockState.getBlock() instanceof BlockChest) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_TRASH);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.CLAY) {NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
                NBTTagCompound spawnData = new NBTTagCompound();
                EntityDreadHorse horse = new EntityDreadHorse(worldIn);
                horse.updatePassenger(new EntityDreadKnight(worldIn));
                ResourceLocation spawnerMobId = EntityList.getKey(horse);
                if(spawnerMobId != null){
                    spawnData.setString("id", spawnerMobId.toString());
                    tag.removeTag("SpawnPotentials");
                    tag.setTag("SpawnData", spawnData.copy());
                }
                Template.BlockInfo newInfo = new Template.BlockInfo(pos, IafBlockRegistry.dread_spawner.getDefaultState(), tag);

                return newInfo;
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.LAPIS_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadThrall.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.DIAMOND_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadKnight.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.GOLD_ORE) {
                Random rand = new Random(worldIn.getSeed() + pos.toLong());
                return getSpawnedMob(worldIn, pos, blockInfoIn, getRandomMobForMobSpawner(rand));
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.ENDER_CHEST) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadGhoul.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.END_PORTAL_FRAME) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadScuttler.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.MOSSY_COBBLESTONE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadBeast.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.IRON_ORE) {
                if(worldIn.rand.nextInt(2) < 1)
                    return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadBeast.class);
                else
                    return new Template.BlockInfo(pos, Blocks.AIR.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.REDSTONE_ORE) {
                return getSpawnedMob(worldIn, pos, blockInfoIn, EntityDreadKnightRoyal.class);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.LIT_PUMPKIN) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_lich.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.BRICK_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tripwire.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.EMERALD_ORE) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tripwire_dragon.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.BEDROCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_queen.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.END_ROD) {
                return new Template.BlockInfo(pos, IafBlockRegistry.dread_single_spawner_dragon.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.NETHERRACK) {
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
            if (blockInfoIn.blockState.getBlock() == Blocks.NETHER_BRICK) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_QUEEN);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.sapphireOre) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_LIBRARY);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.MONSTER_EGG) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_TREASURE);
            }
            if (blockInfoIn.blockState == IafBlockRegistry.myrmex_resin_sticky.getStateFromMeta(1)) {
                return getLootTable(worldIn, pos, blockInfoIn, DREAD_CHEST_LOOT_MAGIC);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.MYCELIUM) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_TOWER_KEY);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.myrmex_desert_resin_block) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_ACCESS_KEY);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.myrmex_resin) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_QUEEN_KEY);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.jungle_myrmex_cocoon) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_LIBRARY_KEY);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.DIAMOND_BLOCK || blockInfoIn.blockState.getBlock() == Blocks.QUARTZ_ORE || blockInfoIn.blockState.getBlock() == Blocks.RED_NETHER_BRICK || blockInfoIn.blockState.getBlock() == Blocks.EMERALD_BLOCK) {
                return getLootTable(worldIn, pos, blockInfoIn, LOOT_SPECIAL_KEY);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.MELON_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tower_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState == IafBlockRegistry.myrmex_resin_sticky.getStateFromMeta(0)) {
                return new Template.BlockInfo(pos, IafBlockRegistry.queen_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.myrmex_jungle_resin_block) {
                return new Template.BlockInfo(pos, IafBlockRegistry.tower_access_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == IafBlockRegistry.silverBlock) {
                return new Template.BlockInfo(pos, IafBlockRegistry.library_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState == Blocks.QUARTZ_BLOCK.getStateFromMeta(1) || blockInfoIn.blockState.getBlock() == Blocks.MAGMA || blockInfoIn.blockState.getBlock() == Blocks.LAPIS_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.special_keyhole.getDefaultState(), null);
            }
            if (blockInfoIn.blockState.getBlock() == Blocks.CONCRETE_POWDER) {
                return new Template.BlockInfo(pos, IafBlockRegistry.treasury_keyhole.getDefaultState(), null);
            }
            if(blockInfoIn.blockState.getBlock() == Blocks.YELLOW_GLAZED_TERRACOTTA){
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
