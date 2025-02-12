package com.github.alexthe666.iceandfire.compat.tinkers;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.*;
import static slimeknights.tconstruct.library.materials.MaterialTypes.HEAD;
import static slimeknights.tconstruct.library.materials.MaterialTypes.SHAFT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerMaterials;
import static slimeknights.tconstruct.tools.TinkerTraits.poisonous;
import static slimeknights.tconstruct.tools.TinkerTraits.sharp;
import slimeknights.tconstruct.tools.traits.TraitBonusDamage;

public class TinkersCompat {
    public static final Material MATERIAL_DRAGONBONE = new Material("dragonbone", 0XB7B29D);
    public static final Material MATERIAL_DESERT_MYRMEX = new Material("desert_myrmex", 0X783B0C);
    public static final Material MATERIAL_JUNGLE_MYRMEX = new Material("jungle_myrmex", 0X267A72);
    public static final Material MATERIAL_DRAGONSTEEL_FIRE = new Material("dragonsteel_fire", 0XCCBBC4);
    public static final Material MATERIAL_DRAGONSTEEL_ICE = new Material("dragonsteel_ice", 0XBBE4FD);
    public static final Material MATERIAL_DRAGONSTEEL_LIGHTNING = new Material("dragonsteel_lightning", 5317036);
    public static final Material MATERIAL_STYMPH_FEATHER = new Material("stymph_feather", 0X7D5B40);
    public static final Material MATERIAL_AMPHITHERE_FEATHER = new Material("amphithere_feather", 0X228760);
    public static final Material MATERIAL_WEEZER = new Material("weezer", 0X00AAE2, true);
    public static final AbstractTrait SPLINTERING_II = new TraitSplinteringII();
    public static final AbstractTrait SPLINTERS_II = new TraitSplitting2();
    public static final AbstractTrait FRACTURED_II = new TraitBonusDamage("fractured2", 3f);
    public static final AbstractTrait HIVE_DEFENDER = new TraitHiveDefender();
    public static final AbstractTrait FREEZE_II = new TraitFreeze(2);
    public static final AbstractTrait BURN_II = new TraitBurn(2);
    public static final AbstractTrait FREEZE_I = new TraitFreeze(1);
    public static final AbstractTrait BURN_I = new TraitBurn(1);
    public static final AbstractTrait LIGHTNING_I = new TraitThunder(1);
    public static final AbstractTrait LIGHTNING_II = new TraitThunder(2);
    public static final AbstractTrait ANTIGRAVITY = new TraitAntigravity();
    public static final AbstractTrait ARROW_KNOCKBACK = new TraitArrowKnockback();
    public static final AbstractTrait IN_THE_GARAGE = new TraitInTheGarage();
    public static final AbstractTrait SWEATER_SONG = new TraitSweaterSong();
    public static final AbstractTrait SURF_WAX_AMERICA = new TraitSurfWaxAmerica();
    private static final TinkersCompat INSTANCE = new TinkersCompat();
    public static FluidMoltenIaf MOLTEN_FIRE_DRAGONSTEEL;
    public static FluidMoltenIaf MOLTEN_ICE_DRAGONSTEEL;
    public static FluidMoltenIaf MOLTEN_LIGHTNING_DRAGONSTEEL;
    private static boolean registered = false;
    
    public static void register() {
        if (!registered) {
            registered = true;
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            init();
        } else {
            throw new RuntimeException("You can only call TinkersCompat.register() once");
        }
    }

    private static void init() {
        TinkerMaterials.materials.add(MATERIAL_DRAGONBONE);
        TinkerRegistry.integrate(MATERIAL_DRAGONBONE).preInit();
        MATERIAL_DRAGONBONE.addItem(IafItemRegistry.dragonbone, 1, Material.VALUE_Ingot);
        MATERIAL_DRAGONBONE.setRepresentativeItem(IafItemRegistry.dragonbone);
        MATERIAL_DRAGONBONE.setCraftable(true);
        MATERIAL_DRAGONBONE.setCastable(false);
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONBONE,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonboneToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatDragonboneToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatDragonboneToolHeadBaseDamage, HarvestLevels.OBSIDIAN),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonboneToolHandleModifier, IceAndFire.CONFIG.tinkersCompatDragonboneToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonboneExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONBONE, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonboneBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatDragonboneBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatDragonboneBowBaseDamage));
        MATERIAL_DRAGONBONE.addTrait(SPLINTERING_II, HEAD);
        MATERIAL_DRAGONBONE.addTrait(SPLINTERS_II, SHAFT);
        MATERIAL_DRAGONBONE.addTrait(FRACTURED_II);
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONBONE, new ArrowShaftMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonboneArrowShaftModifier, IceAndFire.CONFIG.tinkersCompatDragonboneArrowShaftBonusAmmo));
        TinkerMaterials.materials.add(MATERIAL_DESERT_MYRMEX);
        TinkerRegistry.integrate(MATERIAL_DESERT_MYRMEX).preInit();
        MATERIAL_DESERT_MYRMEX.addItem(IafItemRegistry.myrmex_desert_chitin, 1, Material.VALUE_Ingot);
        MATERIAL_DESERT_MYRMEX.setRepresentativeItem(IafItemRegistry.myrmex_desert_chitin);
        MATERIAL_DESERT_MYRMEX.setCraftable(true);
        MATERIAL_DESERT_MYRMEX.setCastable(false);
        TinkerRegistry.addMaterialStats(MATERIAL_DESERT_MYRMEX,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadBaseDamage, HarvestLevels.DIAMOND),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHandleModifier, IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatMyrmexChitinExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_DESERT_MYRMEX, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowBaseDamage));
        MATERIAL_DESERT_MYRMEX.addTrait(HIVE_DEFENDER, HEAD);
        MATERIAL_DESERT_MYRMEX.addTrait(poisonous);

        TinkerMaterials.materials.add(MATERIAL_JUNGLE_MYRMEX);
        TinkerRegistry.integrate(MATERIAL_JUNGLE_MYRMEX).preInit();
        MATERIAL_JUNGLE_MYRMEX.addItem(IafItemRegistry.myrmex_jungle_chitin, 1, Material.VALUE_Ingot);
        MATERIAL_JUNGLE_MYRMEX.setRepresentativeItem(IafItemRegistry.myrmex_jungle_chitin);
        TinkerRegistry.addMaterialStats(MATERIAL_JUNGLE_MYRMEX,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHeadBaseDamage, HarvestLevels.DIAMOND),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHandleModifier, IceAndFire.CONFIG.tinkersCompatMyrmexChitinToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatMyrmexChitinExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_JUNGLE_MYRMEX, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatMyrmexChitinBowBaseDamage));
        MATERIAL_JUNGLE_MYRMEX.addTrait(HIVE_DEFENDER, HEAD);
        MATERIAL_JUNGLE_MYRMEX.addTrait(poisonous);
        setupFluids();

        TinkerMaterials.materials.add(MATERIAL_DRAGONSTEEL_FIRE);
        TinkerRegistry.integrate(MATERIAL_DRAGONSTEEL_FIRE, MOLTEN_FIRE_DRAGONSTEEL, "FireDragonsteel").toolforge().preInit();
        MATERIAL_DRAGONSTEEL_FIRE.addItem(IafItemRegistry.dragonsteel_fire_ingot, 1, Material.VALUE_Ingot);
        MATERIAL_DRAGONSTEEL_FIRE.setRepresentativeItem(IafItemRegistry.dragonsteel_fire_ingot);
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_FIRE,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadBaseDamage, HarvestLevels.COBALT),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleModifier, IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_FIRE, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowBaseDamage));
        MATERIAL_DRAGONSTEEL_FIRE.addTrait(BURN_II, HEAD);
        MATERIAL_DRAGONSTEEL_FIRE.addTrait(sharp);

        TinkerMaterials.materials.add(MATERIAL_DRAGONSTEEL_ICE);
        TinkerRegistry.integrate(MATERIAL_DRAGONSTEEL_ICE, MOLTEN_ICE_DRAGONSTEEL, "IceDragonsteel").toolforge().preInit();
        MATERIAL_DRAGONSTEEL_ICE.addItem(IafItemRegistry.dragonsteel_ice_ingot, 1, Material.VALUE_Ingot);
        MATERIAL_DRAGONSTEEL_ICE.setRepresentativeItem(IafItemRegistry.dragonsteel_ice_ingot);
        MATERIAL_DRAGONSTEEL_ICE.setCraftable(false);
        MATERIAL_DRAGONSTEEL_ICE.setCastable(true);
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_ICE,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadBaseDamage, HarvestLevels.COBALT),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleModifier, IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_ICE, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowBaseDamage));
        MATERIAL_DRAGONSTEEL_ICE.addTrait(FREEZE_II, HEAD);
        MATERIAL_DRAGONSTEEL_ICE.addTrait(sharp);
        
        TinkerMaterials.materials.add(MATERIAL_DRAGONSTEEL_LIGHTNING);
        TinkerRegistry.integrate(MATERIAL_DRAGONSTEEL_LIGHTNING, MOLTEN_LIGHTNING_DRAGONSTEEL, "LightningDragonsteel").toolforge().preInit();
        MATERIAL_DRAGONSTEEL_LIGHTNING.addItem(IafItemRegistry.dragonsteel_lightning_ingot, 1, Material.VALUE_Ingot);
        MATERIAL_DRAGONSTEEL_LIGHTNING.setRepresentativeItem(IafItemRegistry.dragonsteel_lightning_ingot);
        MATERIAL_DRAGONSTEEL_LIGHTNING.setCraftable(false);
        MATERIAL_DRAGONSTEEL_LIGHTNING.setCastable(true);
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_LIGHTNING,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHeadBaseDamage, HarvestLevels.COBALT),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleModifier, IceAndFire.CONFIG.tinkersCompatDragonsteelToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatDragonsteelExtraMaterialDurability));
        TinkerRegistry.addMaterialStats(MATERIAL_DRAGONSTEEL_LIGHTNING, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatDragonsteelBowBaseDamage));
        MATERIAL_DRAGONSTEEL_LIGHTNING.addTrait(LIGHTNING_II, HEAD);
        MATERIAL_DRAGONSTEEL_LIGHTNING.addTrait(sharp);
        
        FREEZE_I.addItem(IafItemRegistry.ice_dragon_blood);
        FREEZE_I.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(IafItemRegistry.ice_dragon_blood)));
        BURN_I.addItem(IafItemRegistry.fire_dragon_blood);
        BURN_I.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(IafItemRegistry.fire_dragon_blood)));
        LIGHTNING_I.addItem(IafItemRegistry.lightning_dragon_blood);
        LIGHTNING_I.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(IafItemRegistry.lightning_dragon_blood)));
        
        TinkerMaterials.materials.add(MATERIAL_STYMPH_FEATHER);
        TinkerRegistry.integrate(MATERIAL_STYMPH_FEATHER).preInit();
        MATERIAL_STYMPH_FEATHER.addItem(IafItemRegistry.stymphalian_bird_feather, 1, Material.VALUE_Ingot);
        MATERIAL_STYMPH_FEATHER.setRepresentativeItem(IafItemRegistry.stymphalian_bird_feather);
        MATERIAL_STYMPH_FEATHER.addTrait(ANTIGRAVITY);
        TinkerRegistry.addMaterialStats(MATERIAL_STYMPH_FEATHER, new FletchingMaterialStats((float) IceAndFire.CONFIG.tinkersCompatStymphalianBirdFeatherFletchingAccuracy, (float) IceAndFire.CONFIG.tinkersCompatStymphalianBirdFeatherFletchingModifier));

        TinkerMaterials.materials.add(MATERIAL_AMPHITHERE_FEATHER);
        TinkerRegistry.integrate(MATERIAL_AMPHITHERE_FEATHER).preInit();
        MATERIAL_AMPHITHERE_FEATHER.addItem(IafItemRegistry.amphithere_feather, 1, Material.VALUE_Ingot);
        MATERIAL_AMPHITHERE_FEATHER.setRepresentativeItem(IafItemRegistry.amphithere_feather);
        MATERIAL_AMPHITHERE_FEATHER.addTrait(ARROW_KNOCKBACK);
        TinkerRegistry.addMaterialStats(MATERIAL_AMPHITHERE_FEATHER, new FletchingMaterialStats((float) IceAndFire.CONFIG.tinkersCompatAmphithereFeatherFletchingAccuracy, (float) IceAndFire.CONFIG.tinkersCompatAmphithereFeatherFletchingModifier));

        if(IceAndFire.CONFIG.weezerTinkers) {
            TinkerMaterials.materials.add(MATERIAL_WEEZER);
            TinkerRegistry.integrate(MATERIAL_WEEZER).preInit();
            MATERIAL_WEEZER.setCraftable(true);
            MATERIAL_WEEZER.setCastable(false);
            MATERIAL_WEEZER.addItem(IafItemRegistry.weezer_blue_album, 1, Material.VALUE_Ingot);
            MATERIAL_WEEZER.setRepresentativeItem(IafItemRegistry.weezer_blue_album);
            TinkerRegistry.addMaterialStats(MATERIAL_WEEZER,
                new HeadMaterialStats(IceAndFire.CONFIG.tinkersCompatWeezerToolHeadDurability, (float) IceAndFire.CONFIG.tinkersCompatWeezerToolHeadEfficiency, (float) IceAndFire.CONFIG.tinkersCompatWeezerToolHeadBaseDamage, HarvestLevels.COBALT),
                new HandleMaterialStats((float) IceAndFire.CONFIG.tinkersCompatWeezerToolHandleModifier, IceAndFire.CONFIG.tinkersCompatWeezerToolHandleDurability),
                new ExtraMaterialStats(IceAndFire.CONFIG.tinkersCompatWeezerExtraMaterialDurability));
            TinkerRegistry.addMaterialStats(MATERIAL_WEEZER, new BowMaterialStats((float) IceAndFire.CONFIG.tinkersCompatWeezerBowDrawSpeed, (float) IceAndFire.CONFIG.tinkersCompatWeezerBowRangeMultiplier, (float) IceAndFire.CONFIG.tinkersCompatWeezerBowBaseDamage));
            MATERIAL_WEEZER.addTrait(IN_THE_GARAGE);
            MATERIAL_WEEZER.addTrait(SWEATER_SONG);
            MATERIAL_WEEZER.addTrait(SURF_WAX_AMERICA);
        }
    }

    public static void setupFluids() {
        MOLTEN_FIRE_DRAGONSTEEL = fluidMetal("dragonsteel_fire", 0X594C58);
        MOLTEN_FIRE_DRAGONSTEEL.setTemperature(IceAndFire.CONFIG.tinkersCompatDragonsteelSmelteryTemperature);
        MOLTEN_ICE_DRAGONSTEEL = fluidMetal("dragonsteel_ice", 0x8299A7);
        MOLTEN_ICE_DRAGONSTEEL.setTemperature(IceAndFire.CONFIG.tinkersCompatDragonsteelSmelteryTemperature);
        MOLTEN_LIGHTNING_DRAGONSTEEL = fluidMetal("dragonsteel_lightning", 0X725691);
        MOLTEN_LIGHTNING_DRAGONSTEEL.setTemperature(IceAndFire.CONFIG.tinkersCompatDragonsteelSmelteryTemperature);
        MATERIAL_DRAGONSTEEL_FIRE.setFluid(MOLTEN_FIRE_DRAGONSTEEL);
        MATERIAL_DRAGONSTEEL_ICE.setFluid(MOLTEN_ICE_DRAGONSTEEL);
        MATERIAL_DRAGONSTEEL_LIGHTNING.setFluid(MOLTEN_LIGHTNING_DRAGONSTEEL);
    }

    public static void post() {
        //MATERIAL_WEEZER.hide = true;
    }

    protected static boolean isSmelteryLoaded() {
        return TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);
    }

    private static FluidMoltenIaf fluidMetal(String name, int color) {
        FluidMoltenIaf fluid = new FluidMoltenIaf(name, color);
        return registerFluid(fluid);
    }

    protected static <T extends Fluid> T registerFluid(T fluid) {
        fluid.setUnlocalizedName(Util.prefix(fluid.getName()));
        FluidRegistry.registerFluid(fluid);

        return fluid;
    }
}
