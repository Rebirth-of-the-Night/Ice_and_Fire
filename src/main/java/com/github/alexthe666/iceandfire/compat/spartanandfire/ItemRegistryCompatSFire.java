package com.github.alexthe666.iceandfire.compat.spartanandfire;

import com.chaosbuffalo.spartanfire.IAFMatConverter;
import com.chaosbuffalo.spartanfire.Utils;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Jacob on 7/20/2018.
 */
public class ItemRegistryCompatSFire {
    public static final Set<IAFMatConverter> MATERIALS_TO_REGISTER = new LinkedHashSet<>();

    public static final String LIGHTNING_DRAGONBONE = "lightning_dragonbone";
    public static final String LIGHTNING_DRAGONSTEEL = "lightning_dragonsteel";

    private static final Set<Item> ALL_ITEMS = new HashSet<>();

    public static void addItemToRegistry(Item item, String modelLoc) {
        SpartanWeaponryAPI.addItemModelToRegistry(item, "iceandfire", modelLoc);
    }
    
    static {
        MATERIALS_TO_REGISTER.add(new IAFMatConverter(LIGHTNING_DRAGONBONE,
                Utils.spartanMatFromToolMat(LIGHTNING_DRAGONBONE, IafItemRegistry.fireBoneTools,
                9867904, 14999238, "ingotDragonbone"),
                new LightningSwordWeaponProperty(LIGHTNING_DRAGONBONE, IceAndFire.MODID)));
        MATERIALS_TO_REGISTER.add(new IAFMatConverter(LIGHTNING_DRAGONSTEEL,
                Utils.spartanMatFromToolMat(LIGHTNING_DRAGONSTEEL, IafItemRegistry.dragonsteel_ice_tools,
                        9867904, 14999238, "ingotLightningDragonsteel"),
                new LightningDragonsteelWeaponProperty(LIGHTNING_DRAGONSTEEL, IceAndFire.MODID)));

    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> ev) {
        // Don't know why this was left out from ice and fire

        Set<Item> item_set = new LinkedHashSet<>();
        for (IAFMatConverter mat : MATERIALS_TO_REGISTER){
            if (!ConfigHandler.disableKatana){
                Item katana = SpartanWeaponryAPI.createKatana(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(katana,
                        "katana_" + mat.material.getUnlocName());
                item_set.add(katana);
            }
            if (!ConfigHandler.disableGreatsword){
                Item greatsword = SpartanWeaponryAPI.createGreatsword(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(greatsword, "greatsword_" + mat.material.getUnlocName());
                item_set.add(greatsword);
            }
            if (!ConfigHandler.disableLongsword){
                Item longsword = SpartanWeaponryAPI.createLongsword(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(longsword, "longsword_" + mat.material.getUnlocName());
                item_set.add(longsword);
            }
            if (!ConfigHandler.disableSaber){
                Item saber = SpartanWeaponryAPI.createSaber(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(saber, "saber_" + mat.material.getUnlocName());
                item_set.add(saber);
            }
            if (!ConfigHandler.disableRapier){
                Item rapier = SpartanWeaponryAPI.createRapier(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(rapier, "rapier_" + mat.material.getUnlocName());
                item_set.add(rapier);
            }
            if (!ConfigHandler.disableDagger) {
                Item dagger = SpartanWeaponryAPI.createDagger(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(dagger,"dagger_" + mat.material.getUnlocName());
                item_set.add(dagger);
            }
            if (!ConfigHandler.disableSpear) {
                Item spear = SpartanWeaponryAPI.createSpear(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(spear,"spear_" + mat.material.getUnlocName());
                item_set.add(spear);
            }
            if (!ConfigHandler.disablePike) {
                Item pike = SpartanWeaponryAPI.createPike(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(pike, "pike_" + mat.material.getUnlocName());
                item_set.add(pike);
            }
            if (!ConfigHandler.disableLance) {
                Item lance = SpartanWeaponryAPI.createLance(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(lance, "lance_" + mat.material.getUnlocName());
                item_set.add(lance);
            }
            if (!ConfigHandler.disableHalberd) {
                Item halberd = SpartanWeaponryAPI.createHalberd(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(halberd, "halberd_" + mat.material.getUnlocName());
                item_set.add(halberd);
            }
            if (!ConfigHandler.disableWarhammer) {
                Item warhammer = SpartanWeaponryAPI.createWarhammer(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(warhammer, "warhammer_" + mat.material.getUnlocName());
                item_set.add(warhammer);
            }
            if (!ConfigHandler.disableHammer) {
                Item hammer = SpartanWeaponryAPI.createHammer(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(hammer,"hammer_" + mat.material.getUnlocName());
                item_set.add(hammer);
            }
            if (!ConfigHandler.disableThrowingAxe) {
                Item throwing_axe = SpartanWeaponryAPI.createThrowingAxe(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(throwing_axe,
                        "throwing_axe_" + mat.material.getUnlocName());
                item_set.add(throwing_axe);
            }
            if (!ConfigHandler.disableThrowingKnife) {
                Item throwing_knife = SpartanWeaponryAPI.createThrowingKnife(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(throwing_knife,
                        "throwing_knife_" + mat.material.getUnlocName());
                item_set.add(throwing_knife);
            }
            if (!ConfigHandler.disableLongbow && !ConfigHandler.woodenLongbowOnly) {
                Item longbow = SpartanWeaponryAPI.createLongbow(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        null
                );

                addItemToRegistry(longbow, "longbow_" + mat.material.getUnlocName());
                item_set.add(longbow);
            }
            if (!ConfigHandler.disableCrossbow && !ConfigHandler.woodenCrossbowOnly) {
                Item crossbow = SpartanWeaponryAPI.createCrossbow(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        null
                );

                addItemToRegistry(crossbow,"crossbow_" + mat.material.getUnlocName());
                item_set.add(crossbow);
            }
            if (!ConfigHandler.disableJavelin) {
                Item javelin = SpartanWeaponryAPI.createJavelin(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(javelin,"javelin_" + mat.material.getUnlocName());
                item_set.add(javelin);
            }
            if (!ConfigHandler.disableBattleaxe) {
                Item battleaxe = SpartanWeaponryAPI.createBattleaxe(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(battleaxe, "battleaxe_" + mat.material.getUnlocName());
                item_set.add(battleaxe);
            }
            if (!ConfigHandler.disableBoomerang && !ConfigHandler.woodenBoomerangOnly) {
                Item boomerang = SpartanWeaponryAPI.createBoomerang(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(boomerang,
                        "boomerang_" + mat.material.getUnlocName());
                item_set.add(boomerang);
            }
            if (!ConfigHandler.disableMace) {
                Item mace = SpartanWeaponryAPI.createMace(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(mace, "mace_" + mat.material.getUnlocName());
                item_set.add(mace);
            }
            if (!ConfigHandler.disableQuarterstaff){
                Item quarterstaff = SpartanWeaponryAPI.createQuarterstaff(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(quarterstaff, "staff_" + mat.material.getUnlocName());
                item_set.add(quarterstaff);
            }
            if (!ConfigHandler.disableGlaive){
                Item glaive = SpartanWeaponryAPI.createGlaive(
                        mat.material,
                        IceAndFire.MODID,
                        IceAndFire.TAB_ITEMS,
                        mat.properties.toArray(new WeaponProperty[0])
                );
                addItemToRegistry(glaive, "glaive_" + mat.material.getUnlocName());
                item_set.add(glaive);
            }
            if (ConfigHandler.enableExperimentalWeapons && !ConfigHandler.disableParryingDagger){
                //empty method for now till a create parrying dagger method is created
            }
        }
        for (Item it : item_set){
            ev.getRegistry().register(it);
        }
        ALL_ITEMS.forEach(ev.getRegistry()::register);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ALL_ITEMS.stream()
                .filter(item -> item.getRegistryName() != null)
                .forEach(item ->
                        ModelLoader.setCustomModelResourceLocation(item, 0,
                                new ModelResourceLocation(item.getRegistryName(), "inventory")));
    }
}