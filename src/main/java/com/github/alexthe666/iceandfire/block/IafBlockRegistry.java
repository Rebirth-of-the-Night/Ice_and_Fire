package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.carver.*;
import com.github.alexthe666.iceandfire.block.keletu.*;
import com.github.alexthe666.iceandfire.entity.tile.*;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class IafBlockRegistry {

    public static final SoundType SOUND_TYPE_GOLD = new SoundType(1.0F, 1.0F, IafSoundRegistry.GOLD_PILE_BREAK, IafSoundRegistry.GOLD_PILE_STEP, IafSoundRegistry.GOLD_PILE_BREAK, IafSoundRegistry.GOLD_PILE_STEP, IafSoundRegistry.GOLD_PILE_STEP);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":lectern")
    public static Block lectern = new BlockLectern();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":podium")
    public static Block podium = new BlockPodium();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":fire_lily")
    public static Block fire_lily = new BlockElementalFlower("fire_lily");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frost_lily")
    public static Block frost_lily = new BlockElementalFlower("frost_lily");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":goldpile")
    public static Block goldPile = new BlockGoldPile();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":silverpile")
    public static Block silverPile = new BlockSilverPile();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":silver_ore")
    public static Block silverOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.silverOre", "silver_ore");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":sapphire_ore")
    public static Block sapphireOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.sapphireOre", "sapphire_ore");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":silver_block")
    public static Block silverBlock = new BlockGeneric(Material.IRON, "silver_block", "iceandfire.silverBlock", "pickaxe", 2, 3.0F, 10.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":sapphire_block")
    public static Block sapphireBlock = new BlockGeneric(Material.IRON, "sapphire_block", "iceandfire.sapphireBlock", "pickaxe", 2, 3.0F, 10.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_dirt")
    public static Block charedDirt = new BlockReturningState(Material.GROUND, "chared_dirt", "iceandfire.charedDirt", "shovel", 0, 0.5F, 0.0F, SoundType.GROUND, Blocks.DIRT.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_grass")
    public static Block charedGrass = new BlockReturningState(Material.GRASS, "chared_grass", "iceandfire.charedGrass", "shovel", 0, 0.6F, 0.0F, SoundType.GROUND, Blocks.GRASS.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_stone")
    public static Block charedStone = new BlockReturningState(Material.ROCK, "chared_stone", "iceandfire.charedStone", "pickaxe", 0, 1.5F, 10.0F, SoundType.STONE, Blocks.STONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_cobblestone")
    public static Block charedCobblestone = new BlockReturningState(Material.ROCK, "chared_cobblestone", "iceandfire.charedCobblestone", "pickaxe", 0, 2F, 10.0F, SoundType.STONE, Blocks.COBBLESTONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_gravel")
    public static Block charedGravel = new BlockFallingReturningState(Material.GROUND, "chared_gravel", "iceandfire.charedGravel", "shovel", 0, 0.6F, 0F, SoundType.GROUND);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":chared_grass_path")
    public static Block charedGrassPath = new BlockCharedPath(0);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":ash")
    public static Block ash = new BlockFallingGeneric(Material.SAND, "ash", "iceandfire.ash", "shovel", 0, 0.5F, 0F, SoundType.SAND);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_dirt")
    public static Block frozenDirt = new BlockReturningState(Material.GROUND, "frozen_dirt", "iceandfire.frozenDirt", "shovel", 0, 0.5F, 0.0F, SoundType.GLASS, true, Blocks.DIRT.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_grass")
    public static Block frozenGrass = new BlockReturningState(Material.GRASS, "frozen_grass", "iceandfire.frozenGrass", "shovel", 0, 0.6F, 0.0F, SoundType.GLASS, true, Blocks.GRASS.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_stone")
    public static Block frozenStone = new BlockReturningState(Material.ROCK, "frozen_stone", "iceandfire.frozenStone", "pickaxe", 0, 1.5F, 10.0F, SoundType.GLASS, true, Blocks.STONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_cobblestone")
    public static Block frozenCobblestone = new BlockReturningState(Material.ROCK, "frozen_cobblestone", "iceandfire.frozenCobblestone", "pickaxe", 0, 2F, 10.0F, SoundType.GLASS, true, Blocks.COBBLESTONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_gravel")
    public static Block frozenGravel = new BlockFallingReturningState(Material.GROUND, "frozen_gravel", "iceandfire.frozenGravel", "shovel", 0, 0.6F, 0F, SoundType.GLASS, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_grass_path")
    public static Block frozenGrassPath = new BlockCharedPath(1);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":frozen_splinters")
    public static Block frozenSplinters = new BlockGeneric(Material.WOOD, "frozen_splinters", "iceandfire.frozenSplinters", "pickaxe", 0, 2.0F, 10.0F, SoundType.GLASS, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragon_ice")
    public static Block dragon_ice = new BlockGeneric(Material.PACKED_ICE, "dragon_ice", "iceandfire.dragon_ice", "pickaxe", 0, 0.5F, 0F, SoundType.GLASS, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragon_ice_spikes")
    public static Block dragon_ice_spikes = new BlockIceSpikes();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":nest")
    public static Block nest = new BlockGeneric(Material.GRASS, "nest", "iceandfire.nest", "axe", 0, 0.5F, 0F, SoundType.GROUND, false);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_red")
    public static Block dragonscale_red = new BlockDragonScales("dragonscale_red", EnumDragonEgg.RED);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_green")
    public static Block dragonscale_green = new BlockDragonScales("dragonscale_green", EnumDragonEgg.GREEN);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_bronze")
    public static Block dragonscale_bronze = new BlockDragonScales("dragonscale_bronze", EnumDragonEgg.BRONZE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_gray")
    public static Block dragonscale_gray = new BlockDragonScales("dragonscale_gray", EnumDragonEgg.GRAY);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_blue")
    public static Block dragonscale_blue = new BlockDragonScales("dragonscale_blue", EnumDragonEgg.BLUE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_white")
    public static Block dragonscale_white = new BlockDragonScales("dragonscale_white", EnumDragonEgg.WHITE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_sapphire")
    public static Block dragonscale_sapphire = new BlockDragonScales("dragonscale_sapphire", EnumDragonEgg.SAPPHIRE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_silver")
    public static Block dragonscale_silver = new BlockDragonScales("dragonscale_silver", EnumDragonEgg.SILVER);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragon_bone_block")
    public static Block dragon_bone_block = new BlockDragonBone();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragon_bone_wall")
    public static Block dragon_bone_block_wall = new BlockDragonBoneWall();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_fire_brick")
    public static Block dragonforge_fire_brick = new BlockDragonforgeBricks(0);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_ice_brick")
    public static Block dragonforge_ice_brick = new BlockDragonforgeBricks(1);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_fire_input")
    public static Block dragonforge_fire_input = new BlockDragonforgeInput(0);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_ice_input")
    public static Block dragonforge_ice_input = new BlockDragonforgeInput(1);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_fire_core_disabled")
    public static Block dragonforge_fire_core = new BlockDragonforgeCore(0, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_ice_core_disabled")
    public static Block dragonforge_ice_core = new BlockDragonforgeCore(1, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_fire_core")
    public static Block dragonforge_fire_core_disabled = new BlockDragonforgeCore(1, false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_ice_core")
    public static Block dragonforge_ice_core_disabled = new BlockDragonforgeCore(0, false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":egginice")
    public static Block eggInIce = new BlockEggInIce();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":pixie_house")
    public static Block pixieHouse = new BlockPixieHouse();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":venerable_stump")
    public static Block venerableStump = new BlockVenerableStump();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":jar_empty")
    public static Block jar_empty = new BlockJar(true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":jar_pixie")
    public static Block jar_pixie = new BlockJar(false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_resin")
    public static Block myrmex_resin = new BlockMyrmexResin(false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_resin_sticky")
    public static Block myrmex_resin_sticky = new BlockMyrmexResin(true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":desert_myrmex_cocoon")
    public static Block desert_myrmex_cocoon = new BlockMyrmexCocoon(false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":jungle_myrmex_cocoon")
    public static Block jungle_myrmex_cocoon = new BlockMyrmexCocoon(true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_desert_biolight")
    public static Block myrmex_desert_biolight = new BlockMyrmexBiolight(false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_jungle_biolight")
    public static Block myrmex_jungle_biolight = new BlockMyrmexBiolight(true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_desert_resin_block")
    public static Block myrmex_desert_resin_block = new BlockMyrmexConnectedResin(false, false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_jungle_resin_block")
    public static Block myrmex_jungle_resin_block = new BlockMyrmexConnectedResin(true, false);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_desert_resin_glass")
    public static Block myrmex_desert_resin_glass = new BlockMyrmexConnectedResin(false, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":myrmex_jungle_resin_glass")
    public static Block myrmex_jungle_resin_glass = new BlockMyrmexConnectedResin(true, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonsteel_fire_block")
    public static Block dragonsteel_fire_block = new BlockGeneric(Material.IRON, "dragonsteel_fire_block", "iceandfire.dragonsteel_fire_block", "pickaxe", 3, 10.0F, 1000.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonsteel_ice_block")
    public static Block dragonsteel_ice_block = new BlockGeneric(Material.IRON, "dragonsteel_ice_block", "iceandfire.dragonsteel_ice_block", "pickaxe", 3, 10.0F, 1000.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone")
    public static BlockDreadBase dread_stone = new BlockDreadBase(Material.ROCK, "dread_stone", "iceandfire.dread_stone", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_bricks")
    public static BlockDreadBase dread_stone_bricks = new BlockDreadBase(Material.ROCK, "dread_stone_bricks", "iceandfire.dread_stone_bricks", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_bricks_chiseled")
    public static BlockDreadBase dread_stone_bricks_chiseled = new BlockDreadBase(Material.ROCK, "dread_stone_bricks_chiseled", "iceandfire.dread_stone_bricks_chiseled", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_bricks_cracked")
    public static BlockDreadBase dread_stone_bricks_cracked = new BlockDreadBase(Material.ROCK, "dread_stone_bricks_cracked", "iceandfire.dread_stone_bricks_cracked", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_bricks_mossy")
    public static BlockDreadBase dread_stone_bricks_mossy = new BlockDreadBase(Material.ROCK, "dread_stone_bricks_mossy", "iceandfire.dread_stone_bricks_mossy", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_tile")
    public static BlockDreadBase dread_stone_tile = new BlockDreadBase(Material.ROCK, "dread_stone_tile", "iceandfire.dread_stone_tile", "pickaxe", 3, 20.0F, 100000.0F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_face")
    public static Block dread_stone_face = new BlockDreadStoneFace();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_torch")
    public static Block dread_torch = new BlockDreadTorch();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":tripwire_dragon")
    public static Block cracked_dreadstone = new BlockCrackedDreadstone();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_stairs")
    public static Block dread_stone_bricks_stairs = new BlockGenericStairs(dread_stone_bricks.getDefaultState(), "dread_stone_stairs").setHardness(20F);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_double_slab")
    public static BlockGenericSlab dread_stone_bricks_double_slab = new BlockDreadStoneBrickSlab.Double("dread_stone_slab", 10.0F, 10000F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_stone_slab")
    public static BlockGenericSlab dread_stone_bricks_slab = new BlockDreadStoneBrickSlab.Half("dread_stone_slab", 10.0F, 10000F, SoundType.STONE);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dreadwood_log")
    public static Block dreadwood_log = new BlockDreadWoodLog();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dreadwood_planks")
    public static BlockDreadBase dreadwood_planks = new BlockDreadBase(Material.WOOD, "dreadwood_planks", "iceandfire.dreadwood_planks", "axe", 3, 20.0F, 100000.0F, SoundType.WOOD);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dreadwood_planks_lock")
    public static Block dreadwood_planks_lock = new BlockDreadWoodLock();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_portal")
    public static Block dread_portal = new BlockDreadPortal();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_spawner")
    public static Block dread_spawner = new BlockDreadSpawner();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_single_spawner_lich")
    public static Block dread_single_spawner_lich = new BlockDreadSingleMobSpawner("dread_single_spawner_lich");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_single_spawner_queen")
    public static Block dread_single_spawner_queen = new BlockDreadSingleMobSpawner("dread_single_spawner_queen");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_single_spawner_dragon")
    public static Block dread_single_spawner_dragon = new BlockDreadSingleMobSpawner("dread_single_spawner_dragon");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dread_single_spawner_ballista")
    public static Block dread_single_spawner_ballista = new BlockDreadSingleMobSpawner("dread_single_spawner_ballista");

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":burnt_torch")
    public static Block burnt_torch = new BlockBurntTorch();

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_dirt")
    public static Block crackledDirt = new BlockReturningState(Material.GROUND, "crackled_dirt", "iceandfire.crackledDirt", "shovel", 0, 0.5F, 0.0F, SoundType.GROUND, Blocks.DIRT.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_grass")
    public static Block crackledGrass = new BlockReturningState(Material.GRASS, "crackled_grass", "iceandfire.crackledGrass", "shovel", 0, 0.6F, 0.0F, SoundType.GROUND, Blocks.GRASS.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_stone")
    public static Block crackledStone = new BlockReturningState(Material.ROCK, "crackled_stone", "iceandfire.crackledStone", "pickaxe", 0, 1.5F, 10.0F, SoundType.STONE, Blocks.STONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_cobblestone")
    public static Block crackledCobblestone = new BlockReturningState(Material.ROCK, "crackled_cobblestone", "iceandfire.crackledCobblestone", "pickaxe", 0, 2F, 10.0F, SoundType.STONE, Blocks.COBBLESTONE.getDefaultState());
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_gravel")
    public static Block crackledGravel = new BlockFallingReturningState(Material.GROUND, "crackled_gravel", "iceandfire.crackledGravel", "shovel", 0, 0.6F, 0F, SoundType.GROUND);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":crackled_grass_path")
    public static Block crackledGrassPath = new BlockCharedPath(2);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_lightning_brick")
    public static Block dragonforge_lightning_brick = new BlockDragonforgeBricks(2);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_lightning_input")
    public static Block dragonforge_lightning_input = new BlockDragonforgeInput(2);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_lightning_core_disabled")
    public static Block dragonforge_lightning_core = new BlockDragonforgeCore(2, true);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonforge_lightning_core")
    public static Block dragonforge_lightning_core_disabled = new BlockDragonforgeCore(2, false);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":copperpile")
    public static Block copperPile = new BlockCopperPile();
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":lightning_lily")
    public static Block lightning_lily = new BlockElementalFlower("lightning_lily");

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_electric")
    public static Block dragonscale_electric = new BlockDragonScales("dragonscale_electric", EnumDragonEgg.ELECTRIC);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_amythest")
    public static Block dragonscale_amythest = new BlockDragonScales("dragonscale_amythest", EnumDragonEgg.AMYTHEST);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_copper")
    public static Block dragonscale_copper = new BlockDragonScales("dragonscale_copper", EnumDragonEgg.COPPER);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonscale_black")
    public static Block dragonscale_black = new BlockDragonScales("dragonscale_black", EnumDragonEgg.BLACK);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":copper_ore")
    public static Block copperOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.copperOre", "copper_ore");
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":amythest_ore")
    public static Block amythestOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.amythestOre", "amythest_ore");

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":copper_block")
    public static Block copperBlock = new BlockGeneric(Material.IRON, "copper_block", "iceandfire.copperBlock", "pickaxe", 0, 4.0F, 10.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":amythest_block")
    public static Block amythestBlock = new BlockGeneric(Material.IRON, "amythest_block", "iceandfire.amythestBlock", "pickaxe", 2, 5.0F, 15.0F, SoundType.METAL);
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":dragonsteel_lightning_block")
    public static Block dragonsteel_lightning_block = new BlockGeneric(Material.IRON, "dragonsteel_lightning_block", "iceandfire.dragonsteel_lightning_block", "pickaxe", 3, 10.0F, 1000.0F, SoundType.METAL);

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":tower_keyhole")
    public static Block tower_keyhole = new BlockGenericKeyhole("tower_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.tower_key;
        }
    };
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":tower_access_keyhole")
    public static Block tower_access_keyhole = new BlockGenericKeyhole("tower_access_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.tower_access_key;
        }
    };
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":queen_keyhole")
    public static Block queen_keyhole = new BlockGenericKeyhole("queen_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.queen_key;
        }
    };
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":library_keyhole")
    public static Block library_keyhole = new BlockGenericKeyhole("library_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.library_key;
        }
    };

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":special_keyhole")
    public static Block special_keyhole = new BlockGenericKeyhole("special_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.special_key;
        }
    };
    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":treasury_keyhole")
    public static Block treasury_keyhole = new BlockGenericKeyhole("treasury_keyhole") {
        @Override
        public boolean isValidKey(ItemStack stack) {
            return stack.getItem() == IafItemRegistry.treasury_key;
        }
    };

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":graveyard_soil")
    public static Block graveyard_soil = new BlockGraveyardSoil();

    @GameRegistry.ObjectHolder(IceAndFire.MODID + ":ghost_chest")
    public static Block ghost_chest = new BlockGhostChest();

    static {
        GameRegistry.registerTileEntity(TileEntityDummyGorgonHead.class, new ResourceLocation(IceAndFire.MODID, "dummyGorgonHeadIdle"));
        GameRegistry.registerTileEntity(TileEntityDummyGorgonHeadActive.class, new ResourceLocation(IceAndFire.MODID, "dummyGorgonHeadActive"));
        GameRegistry.registerTileEntity(TileEntityMyrmexCocoon.class, new ResourceLocation(IceAndFire.MODID, "myrmexCocoon"));
        GameRegistry.registerTileEntity(TileEntityDragonforge.class, new ResourceLocation(IceAndFire.MODID, "dragonforge"));
        GameRegistry.registerTileEntity(TileEntityDragonforgeInput.class, new ResourceLocation(IceAndFire.MODID, "dragonforgeInput"));
        GameRegistry.registerTileEntity(TileEntityDragonforgeBrick.class, new ResourceLocation(IceAndFire.MODID, "dragonforgeBrick"));
        GameRegistry.registerTileEntity(TileEntityGhostChest.class, new ResourceLocation(IceAndFire.MODID, "ghostChest"));
    }

}
