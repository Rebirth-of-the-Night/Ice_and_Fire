package com.github.alexthe666.iceandfire.compat.thaumcraft;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;

public class ThaumcraftCompat {

    static final Aspect MYTHICAL = new Aspect("mythus", 0XD9D5AB, new Aspect[]{Aspect.BEAST, Aspect.MAGIC}, new ResourceLocation("iceandfire:textures/thaumcraft/mythical.png"), 1);
    static Aspect DRAGON = getOrCreateAspect("draco", 0XA2271F, new Aspect[]{ThaumcraftCompat.MYTHICAL, Aspect.DESIRE}, new ResourceLocation("iceandfire", "textures/thaumcraft/dragon.png"), 1);

    private final Queue<AspectList> dracoItems = new ArrayDeque<>(68);

    static Aspect getOrCreateAspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {
        Aspect a = Aspect.getAspect(tag);
        if (a != null) return a;
        return new Aspect(tag, color, components, image, blend);
    }

    /**
     * AspectRegistryEvent fires in TC's post-init (I&F load order does not matter) ->
     * Then Thaumic Additions post-init replaces our Draco aspect from the TC registry.
     * So we need to remap the reference and all our items to the new Draco aspect.
     */
    public void remapDraco() {
        // Nothing overwrote our Draco aspect.
        if (DRAGON.equals(Aspect.getAspect("draco"))) {
            dracoItems.clear();
            return;
        }
        IceAndFire.logger.info("Detected a mod overwrote I&F's Draco aspect: remapping " + dracoItems.size() + " entries.");
        Aspect draco = Aspect.getAspect("draco");
        while (!dracoItems.isEmpty()) {
            AspectList aspects = dracoItems.poll();
            int amount = aspects.getAmount(DRAGON);
            aspects.remove(DRAGON);
            aspects.add(draco, amount);
        }
        DRAGON = draco;
    }

    private void registerItem(AspectRegistryEvent event, ItemStack stack, AspectList aspects) {
        if (aspects.getAmount(DRAGON) > 0) {
            dracoItems.offer(aspects);
        }
        event.register.registerObjectTag(stack, aspects);
    }

    @SuppressWarnings("deprecation")
    private void registerEntity(String entityName, AspectList aspects, ThaumcraftApi.EntityTagsNBT... nbt) {
        if (aspects.getAmount(DRAGON) > 0) {
            dracoItems.offer(aspects);
        }
        ThaumcraftApi.registerEntityTag(entityName, aspects, nbt);
    }

    @SubscribeEvent
    public void aspectRegistrationEvent(AspectRegistryEvent evt) {
        registerItem(evt, new ItemStack(IafItemRegistry.bestiary), new AspectList().add(ThaumcraftCompat.DRAGON, 2).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.BEAST, 6).add(Aspect.MAGIC, 2).add(Aspect.MIND, 4).add(Aspect.WATER, 2).add(Aspect.AIR, 1));
        registerItem(evt, new ItemStack(IafItemRegistry.manuscript), new AspectList().add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.PLANT, 3).add(Aspect.MIND, 2).add(Aspect.WATER, 2).add(Aspect.AIR, 1));
        registerItem(evt, new ItemStack(IafItemRegistry.amythestGem), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.sapphireGem), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.fire_stew), new AspectList().add(Aspect.FIRE, 8).add(ThaumcraftCompat.DRAGON, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.frost_stew), new AspectList().add(Aspect.COLD, 8).add(ThaumcraftCompat.DRAGON, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.lightning_stew), new AspectList().add(Aspect.ENTROPY, 8).add(ThaumcraftCompat.DRAGON, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_red), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_bronze), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_green), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_gray), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_blue), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_silver), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_white), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_sapphire), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_electric), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_amythest), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_copper), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonegg_black), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_red), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_bronze), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_green), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_gray), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.FIRE, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_blue), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_silver), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_white), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_sapphire), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_electric), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_amythest), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_copper), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonscales_black), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.ENTROPY, 5).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 7));  
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone), new AspectList().add(Aspect.DEATH, 5).add(ThaumcraftCompat.DRAGON, 5).add(Aspect.MAGIC, 2));
        registerItem(evt, new ItemStack(IafItemRegistry.witherbone), new AspectList().add(Aspect.DEATH, 6).add(Aspect.DARKNESS, 5).add(Aspect.MAGIC, 2).add(Aspect.ENTROPY, 5).add(Aspect.UNDEAD, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.wither_shard), new AspectList().add(Aspect.DEATH, 2).add(Aspect.DARKNESS, 1).add(Aspect.ENTROPY, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.fishing_spear), new AspectList().add(Aspect.PLANT, 2).add(Aspect.WATER, 7).add(Aspect.BEAST, 2).add(Aspect.TOOL, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_sword), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 20).add(Aspect.DEATH, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_sword_fire), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 20).add(Aspect.DEATH, 5).add(Aspect.FIRE, 25).add(Aspect.MAGIC, 2));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_sword_ice), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 20).add(Aspect.DEATH, 5).add(Aspect.COLD, 25).add(Aspect.MAGIC, 2));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_sword_lightning), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.PROTECT, 20).add(Aspect.DEATH, 5).add(Aspect.ENTROPY, 25).add(Aspect.MAGIC, 2));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_pickaxe), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.TOOL, 20).add(Aspect.DEATH, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_axe), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.TOOL, 20).add(Aspect.DEATH, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_shovel), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.TOOL, 20).add(Aspect.DEATH, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_hoe), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.BEAST, 6).add(Aspect.TOOL, 20).add(Aspect.DEATH, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_arrow), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.DESIRE, 2).add(Aspect.TOOL, 2).add(Aspect.AIR, 5).add(Aspect.FLIGHT, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonbone_bow), new AspectList().add(ThaumcraftCompat.DRAGON, 15).add(Aspect.DESIRE, 15).add(Aspect.PROTECT, 20).add(Aspect.AIR, 15).add(Aspect.FLIGHT, 10).add(Aspect.CRAFT, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_skull, 1, 0), new AspectList().add(Aspect.DEATH, 15).add(ThaumcraftCompat.DRAGON, 20).add(Aspect.MAGIC, 2).add(Aspect.FIRE, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_skull, 1, 1), new AspectList().add(Aspect.DEATH, 15).add(ThaumcraftCompat.DRAGON, 20).add(Aspect.MAGIC, 2).add(Aspect.COLD, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_skull, 1, 2), new AspectList().add(Aspect.DEATH, 15).add(ThaumcraftCompat.DRAGON, 20).add(Aspect.MAGIC, 2).add(Aspect.ENTROPY, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_meal), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 5).add(ThaumcraftCompat.DRAGON, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.fire_dragon_flesh), new AspectList().add(Aspect.DEATH, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.FIRE, 10).add(Aspect.LIFE, 3));
        registerItem(evt, new ItemStack(IafItemRegistry.ice_dragon_flesh), new AspectList().add(Aspect.DEATH, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.COLD, 10).add(Aspect.LIFE, 3));
        registerItem(evt, new ItemStack(IafItemRegistry.lightning_dragon_flesh), new AspectList().add(Aspect.DEATH, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.ENTROPY, 10).add(Aspect.LIFE, 3));
        registerItem(evt, new ItemStack(IafItemRegistry.fire_dragon_heart), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.FIRE, 10).add(Aspect.SOUL, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.ice_dragon_heart), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.COLD, 10).add(Aspect.SOUL, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.lightning_dragon_heart), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.ENTROPY, 10).add(Aspect.SOUL, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.fire_dragon_blood), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.FIRE, 10).add(Aspect.ALCHEMY, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.ice_dragon_blood), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.COLD, 10).add(Aspect.ALCHEMY, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.lightning_dragon_blood), new AspectList().add(Aspect.LIFE, 10).add(Aspect.BEAST, 10).add(ThaumcraftCompat.DRAGON, 15).add(Aspect.ENTROPY, 10).add(Aspect.ALCHEMY, 30));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_stick), new AspectList().add(Aspect.TOOL, 10).add(Aspect.BEAST, 20).add(ThaumcraftCompat.DRAGON, 35).add(Aspect.CRAFT, 5).add(Aspect.MAGIC, 15).add(Aspect.MOTION, 10).add(Aspect.ORDER, 40).add(Aspect.MIND, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_horn), new AspectList().add(Aspect.LIFE, 20).add(Aspect.BEAST, 25).add(ThaumcraftCompat.DRAGON, 50).add(Aspect.MAGIC, 20).add(Aspect.CRAFT, 15).add(Aspect.ORDER, 20).add(Aspect.EXCHANGE, 20).add(Aspect.SOUL, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.dragon_flute), new AspectList().add(Aspect.TOOL, 15).add(Aspect.BEAST, 25).add(ThaumcraftCompat.MYTHICAL, 15).add(ThaumcraftCompat.DRAGON, 25).add(Aspect.CRAFT, 5).add(Aspect.MAGIC, 15).add(Aspect.MOTION, 10).add(Aspect.ORDER, 30).add(Aspect.MIND, 15));
        registerItem(evt, new ItemStack(IafItemRegistry.iron_hippogryph_armor), new AspectList().add(Aspect.METAL, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DESIRE, 10).add(Aspect.PROTECT, 10).add(Aspect.FLIGHT, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.iron_hippogryph_armor), new AspectList().add(Aspect.METAL, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DESIRE, 15).add(Aspect.PROTECT, 15).add(Aspect.FLIGHT, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.iron_hippogryph_armor), new AspectList().add(Aspect.CRYSTAL, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DESIRE, 20).add(Aspect.PROTECT, 20).add(Aspect.FLIGHT, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.gorgon_head), new AspectList().add(Aspect.DEATH, 50).add(ThaumcraftCompat.MYTHICAL, 60).add(Aspect.DESIRE, 15).add(Aspect.MAN, 20).add(Aspect.EXCHANGE, 15).add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 15).add(Aspect.TRAP, 5).add(Aspect.DARKNESS, 10).add(Aspect.UNDEAD, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.pixie_dust), new AspectList().add(Aspect.MAGIC, 30).add(ThaumcraftCompat.MYTHICAL, 12).add(Aspect.DESIRE, 5).add(Aspect.MAN, 20).add(Aspect.LIFE, 15));
        registerItem(evt, new ItemStack(IafItemRegistry.ambrosia), new AspectList().add(Aspect.MAGIC, 40).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.DESIRE, 20).add(Aspect.LIFE, 35));
        registerItem(evt, new ItemStack(IafItemRegistry.shiny_scales), new AspectList().add(Aspect.WATER, 10).add(ThaumcraftCompat.MYTHICAL, 15).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.earplugs), new AspectList().add(Aspect.PLANT, 5).add(Aspect.SENSES, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_chitin, 1, 0), new AspectList().add(Aspect.BEAST, 5).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.PROTECT, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_chitin, 1, 1), new AspectList().add(Aspect.BEAST, 5).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.PROTECT, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_chitin, 1, 2), new AspectList().add(Aspect.BEAST, 5).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.PROTECT, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_egg, 1, 0), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 12).add(Aspect.LIFE, 7).add(Aspect.DESIRE, 7).add(Aspect.PROTECT, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_egg, 1, 1), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.LIFE, 10).add(Aspect.DESIRE, 20).add(Aspect.PROTECT, 20));
        for (int i = 0; i < EnumHippogryphTypes.values().length; i++) {
            registerItem(evt, new ItemStack(IafItemRegistry.hippogryph_egg, 1, i), new AspectList().add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.AIR, 5).add(Aspect.DESIRE, 20).add(Aspect.FLIGHT, 8).add(Aspect.BEAST, 6));
        }
        registerItem(evt, new ItemStack(IafItemRegistry.rotten_egg), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.LIFE, 5).add(Aspect.DEATH, 5).add(Aspect.UNDEAD, 5).add(Aspect.ENTROPY, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.stymphalian_bird_feather), new AspectList().add(Aspect.BEAST, 5).add(ThaumcraftCompat.MYTHICAL, 15).add(Aspect.DESIRE, 5).add(Aspect.METAL, 7).add(Aspect.FLIGHT, 3));
        registerItem(evt, new ItemStack(IafItemRegistry.stymphalian_arrow), new AspectList().add(Aspect.BEAST, 2).add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.DESIRE, 2).add(Aspect.PROTECT, 2).add(Aspect.METAL, 7).add(Aspect.FLIGHT, 3).add(Aspect.CRAFT, 3));
        registerItem(evt, new ItemStack(IafItemRegistry.troll_tusk), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DEATH, 5).add(Aspect.BEAST, 5).add(Aspect.AVERSION, 2).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.stone_statue), new AspectList().add(Aspect.BEAST, 20).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DEATH, 10).add(Aspect.MAGIC, 5).add(Aspect.AVERSION, 2).add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));

        for (EnumTroll.Weapon weapon : EnumTroll.Weapon.values()) {
            registerItem(evt, new ItemStack(weapon.item), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.DEATH, 5).add(Aspect.PROTECT, 15).add(Aspect.CRAFT, 2).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5));
        }
        registerItem(evt, new ItemStack(EnumTroll.MOUNTAIN.leather), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.PROTECT, 5).add(Aspect.DESIRE, 2).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5).add(Aspect.EARTH, 7));
        registerItem(evt, new ItemStack(EnumTroll.FROST.leather), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.PROTECT, 5).add(Aspect.DESIRE, 2).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5).add(Aspect.COLD, 7));
        registerItem(evt, new ItemStack(EnumTroll.FOREST.leather), new AspectList().add(Aspect.BEAST, 10).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.PROTECT, 5).add(Aspect.DESIRE, 2).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5).add(Aspect.PLANT, 7));
        registerItem(evt, new ItemStack(IafBlockRegistry.lectern), new AspectList().add(Aspect.PLANT, 8).add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.SENSES, 10).add(Aspect.MIND, 12));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 0), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 1), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 2), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 3), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 4), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.podium, 1, 5), new AspectList().add(Aspect.PLANT, 7).add(ThaumcraftCompat.MYTHICAL, 3).add(Aspect.SENSES, 4).add(Aspect.DESIRE, 20));
        registerItem(evt, new ItemStack(IafBlockRegistry.fire_lily), new AspectList().add(Aspect.FIRE, 5).add(Aspect.PLANT, 7).add(ThaumcraftCompat.DRAGON, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frost_lily), new AspectList().add(Aspect.COLD, 5).add(Aspect.PLANT, 7).add(ThaumcraftCompat.DRAGON, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.lightning_lily), new AspectList().add(Aspect.ENTROPY, 5).add(Aspect.PLANT, 7).add(ThaumcraftCompat.DRAGON, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.amythestOre), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10).add(Aspect.EARTH, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.sapphireOre), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10).add(Aspect.EARTH, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedDirt), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedGrass), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedGrassPath), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 2).add(Aspect.PLANT, 2).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedStone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedCobblestone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 1));
        registerItem(evt, new ItemStack(IafBlockRegistry.charedGravel), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.ash), new AspectList().add(Aspect.ENTROPY, 5).add(Aspect.FIRE, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenDirt), new AspectList().add(Aspect.EARTH, 5).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenGrass), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenGrassPath), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 2).add(Aspect.PLANT, 2).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenStone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenCobblestone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.COLD, 2).add(Aspect.ENTROPY, 1));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenGravel), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledDirt), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledGrass), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 2).add(Aspect.ENTROPY, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledGrassPath), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 2).add(Aspect.PLANT, 2).add(Aspect.ENTROPY, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledStone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledCobblestone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2).add(Aspect.ENTROPY, 1));
        registerItem(evt, new ItemStack(IafBlockRegistry.crackledGravel), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 2).add(Aspect.ENTROPY, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.dragon_ice), new AspectList().add(Aspect.ENTROPY, 2).add(Aspect.COLD, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.frozenCobblestone), new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 1));
        registerItem(evt, new ItemStack(IafBlockRegistry.dragon_ice_spikes), new AspectList().add(Aspect.DEATH, 2).add(Aspect.TRAP, 2).add(Aspect.ENTROPY, 5).add(Aspect.COLD, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.nest), new AspectList().add(Aspect.EARTH, 5).add(Aspect.LIFE, 2).add(ThaumcraftCompat.DRAGON, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 0), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 1), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 2), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 3), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 4), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.pixieHouse, 1, 5), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRAFT, 1).add(Aspect.PLANT, 3).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.MAGIC, 3));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_empty), new AspectList().add(Aspect.CRYSTAL, 3).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_pixie, 1, 0), new AspectList().add(Aspect.CRYSTAL, 3).add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.MAGIC, 10).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_pixie, 1, 1), new AspectList().add(Aspect.CRYSTAL, 3).add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.MAGIC, 10).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_pixie, 1, 2), new AspectList().add(Aspect.CRYSTAL, 3).add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.MAGIC, 10).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_pixie, 1, 3), new AspectList().add(Aspect.CRYSTAL, 3).add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.MAGIC, 10).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.jar_pixie, 1, 4), new AspectList().add(Aspect.CRYSTAL, 3).add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.MAGIC, 10).add(Aspect.AIR, 5));
        registerItem(evt, new ItemStack(IafBlockRegistry.eggInIce), new AspectList().add(ThaumcraftCompat.DRAGON, 5).add(Aspect.COLD, 20).add(Aspect.DESIRE, 20).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_egg, 1, 0), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_egg, 1, 1), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_egg, 1, 2), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_egg, 1, 3), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_egg, 1, 4), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 25).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_egg, 1, 0), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_egg, 1, 1), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_egg, 1, 2), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_egg, 1, 3), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_egg, 1, 4), new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 25).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_resin), new AspectList().add(Aspect.EXCHANGE, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 7).add(Aspect.BEAST, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_resin), new AspectList().add(Aspect.EXCHANGE, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 7).add(Aspect.BEAST, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_chitin), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_chitin), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_stinger), new AspectList().add(Aspect.BEAST, 15).add(Aspect.TRAP, 5).add(Aspect.DEATH, 5).add(ThaumcraftCompat.MYTHICAL, 7));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_desert_staff), new AspectList().add(Aspect.TOOL, 10).add(Aspect.BEAST, 20).add(ThaumcraftCompat.MYTHICAL, 15).add(Aspect.CRAFT, 5).add(Aspect.MAGIC, 15).add(Aspect.MOTION, 10).add(Aspect.ORDER, 40).add(Aspect.MIND, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.myrmex_jungle_staff), new AspectList().add(Aspect.TOOL, 10).add(Aspect.BEAST, 20).add(ThaumcraftCompat.MYTHICAL, 15).add(Aspect.CRAFT, 5).add(Aspect.MAGIC, 15).add(Aspect.MOTION, 10).add(Aspect.ORDER, 40).add(Aspect.MIND, 10));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_resin, 1, 0), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_resin, 1, 1), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_resin_sticky, 1, 0), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_resin_sticky, 1, 1), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(ThaumcraftCompat.MYTHICAL, 2).add(Aspect.TRAP, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.desert_myrmex_cocoon), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(Aspect.CRAFT, 5).add(Aspect.MECHANISM, 5).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.jungle_myrmex_cocoon), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5).add(Aspect.CRAFT, 5).add(Aspect.MECHANISM, 5).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_desert_biolight), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 5).add(Aspect.AURA, 2).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_jungle_biolight), new AspectList().add(Aspect.EARTH, 5).add(Aspect.PLANT, 5).add(Aspect.AURA, 2).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_desert_resin_block), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 2).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_jungle_resin_block), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ORDER, 2).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_desert_resin_glass), new AspectList().add(Aspect.EARTH, 5).add(Aspect.AIR, 2).add(ThaumcraftCompat.MYTHICAL, 2));
        registerItem(evt, new ItemStack(IafBlockRegistry.myrmex_jungle_resin_glass), new AspectList().add(Aspect.EARTH, 5).add(Aspect.AIR, 2).add(ThaumcraftCompat.MYTHICAL, 2));

        registerItem(evt, new ItemStack(IafItemRegistry.dragonsteel_fire_ingot), new AspectList().add(Aspect.METAL, 40).add(ThaumcraftCompat.MYTHICAL, 20).add(ThaumcraftCompat.DRAGON, 10).add(Aspect.FIRE, 10).add(Aspect.MAGIC, 25).add(Aspect.DESIRE, 45));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonsteel_ice_ingot), new AspectList().add(Aspect.METAL, 40).add(ThaumcraftCompat.MYTHICAL, 20).add(ThaumcraftCompat.DRAGON, 10).add(Aspect.WATER, 10).add(Aspect.MAGIC, 25).add(Aspect.DESIRE, 45));
        registerItem(evt, new ItemStack(IafItemRegistry.dragonsteel_lightning_ingot), new AspectList().add(Aspect.METAL, 40).add(ThaumcraftCompat.MYTHICAL, 20).add(ThaumcraftCompat.DRAGON, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAGIC, 25).add(Aspect.DESIRE, 45));
        registerItem(evt, new ItemStack(IafItemRegistry.hippogryph_talon), new AspectList().add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.AIR, 5).add(Aspect.DESIRE, 20).add(Aspect.FLIGHT, 8).add(Aspect.BEAST, 6));
        registerItem(evt, new ItemStack(IafItemRegistry.pixie_wings), new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.FLIGHT, 5).add(Aspect.MAN, 10).add(Aspect.AIR, 10).add(Aspect.MAGIC, 30).add(Aspect.DESIRE, 5).add(Aspect.EXCHANGE, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.cyclops_eye), new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.MAN, 10).add(Aspect.DEATH, 10).add(Aspect.MAGIC, 30).add(Aspect.DESIRE, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.siren_tear), new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.MAN, 10).add(Aspect.DESIRE, 25).add(Aspect.MAGIC, 30).add(Aspect.WATER, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.hippocampus_fin), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.WATER, 25));
        registerItem(evt, new ItemStack(IafItemRegistry.deathworm_tounge), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.TRAP, 5));
        registerItem(evt, new ItemStack(IafItemRegistry.cockatrice_eye), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.DEATH, 35));
        registerItem(evt, new ItemStack(IafItemRegistry.amphithere_feather), new AspectList().add(ThaumcraftCompat.MYTHICAL, 10).add(Aspect.BEAST, 10).add(Aspect.FLIGHT, 10));
        registerItem(evt, new ItemStack(IafItemRegistry.sea_serpent_fang), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.WATER, 10));
        for (EnumSeaSerpent serpent : EnumSeaSerpent.values()) {
            registerItem(evt, new ItemStack(serpent.scale), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.WATER, 10));
        }
        for (EnumSkullType skull : EnumSkullType.values()) {
            registerItem(evt, new ItemStack(skull.skull_item), new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.BEAST, 10).add(Aspect.DEATH, 10));
        }
        registerEntity("dragonegg", new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.DRAGON, 10).add(Aspect.MAGIC, 2));
        registerEntity("dragonskull", new AspectList().add(Aspect.DEATH, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.DRAGON, 10).add(Aspect.MAGIC, 2));
        registerEntity("firedragon", new AspectList().add(Aspect.FIRE, 25).add(Aspect.BEAST, 15).add(Aspect.FLIGHT, 25).add(Aspect.MIND, 5).add(ThaumcraftCompat.DRAGON, 50).add(Aspect.MAGIC, 10));
        registerEntity("icedragon", new AspectList().add(Aspect.COLD, 25).add(Aspect.BEAST, 15).add(Aspect.FLIGHT, 25).add(Aspect.MIND, 5).add(ThaumcraftCompat.DRAGON, 50).add(Aspect.MAGIC, 10));
        registerEntity("lightningdragon", new AspectList().add(Aspect.ENTROPY, 25).add(Aspect.BEAST, 15).add(Aspect.FLIGHT, 25).add(Aspect.MIND, 5).add(ThaumcraftCompat.DRAGON, 50).add(Aspect.MAGIC, 10));
        registerEntity("snowvillager", new AspectList().add(Aspect.COLD, 20).add(Aspect.MAN, 10).add(Aspect.MIND, 7));
        registerEntity("hippogryph", new AspectList().add(ThaumcraftCompat.MYTHICAL, 35).add(Aspect.FLIGHT, 15).add(Aspect.BEAST, 10).add(Aspect.AIR, 7));
        registerEntity("stonestatue", new AspectList().add(ThaumcraftCompat.MYTHICAL, 5).add(Aspect.EARTH, 10).add(Aspect.MAGIC, 5).add(Aspect.DEATH, 5).add(Aspect.ELDRITCH, 5));
        registerEntity("gorgon", new AspectList().add(ThaumcraftCompat.MYTHICAL, 50).add(Aspect.DEATH, 20).add(Aspect.MAN, 10).add(Aspect.BEAST, 10).add(Aspect.UNDEAD, 10).add(Aspect.DESIRE, 15).add(Aspect.EARTH, 5).add(Aspect.SENSES, 10));
        registerEntity("if_pixie", new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.FLIGHT, 5).add(Aspect.MAN, 10).add(Aspect.AIR, 10).add(Aspect.MAGIC, 30).add(Aspect.DESIRE, 5).add(Aspect.EXCHANGE, 5));
        registerEntity("cyclops", new AspectList().add(ThaumcraftCompat.MYTHICAL, 50).add(Aspect.MAN, 10).add(Aspect.MIND, 2).add(Aspect.ELDRITCH, 5).add(Aspect.LIFE, 5).add(Aspect.EARTH, 10).add(Aspect.DEATH, 2).add(Aspect.SENSES, 10));
        registerEntity("siren", new AspectList().add(ThaumcraftCompat.MYTHICAL, 50).add(Aspect.MAN, 10).add(Aspect.WATER, 20).add(Aspect.DESIRE, 150).add(Aspect.DEATH, 5).add(Aspect.SENSES, 40));
        registerEntity("hippocampus", new AspectList().add(ThaumcraftCompat.MYTHICAL, 35).add(Aspect.WATER, 15).add(Aspect.DESIRE, 5).add(Aspect.BEAST, 5));
        registerEntity("deathworm", new AspectList().add(ThaumcraftCompat.MYTHICAL, 20).add(Aspect.DEATH, 15).add(Aspect.MOTION, 5).add(Aspect.BEAST, 20).add(Aspect.PROTECT, 10).add(Aspect.ELDRITCH, 15));
        registerEntity("stymphalianbird", new AspectList().add(ThaumcraftCompat.MYTHICAL, 35).add(Aspect.FLIGHT, 20).add(Aspect.METAL, 20).add(Aspect.BEAST, 20).add(Aspect.PROTECT, 15).add(Aspect.AVERSION, 15));
        registerEntity("if_cockatrice", new AspectList().add(ThaumcraftCompat.MYTHICAL, 35).add(Aspect.FLIGHT, 20).add(Aspect.ALCHEMY, 20).add(Aspect.DARKNESS, 20).add(Aspect.AVERSION, 15));
        registerEntity("if_troll", new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.BEAST, 20).add(Aspect.AVERSION, 10).add(Aspect.DARKNESS, 20).add(Aspect.MAGIC, 15).add(Aspect.MAN, 5));
        registerEntity("myrmex_egg", new AspectList().add(Aspect.LIFE, 5).add(Aspect.DESIRE, 5).add(ThaumcraftCompat.MYTHICAL, 5));
        registerEntity("myrmex_worker", new AspectList().add(Aspect.BEAST, 15).add(Aspect.EXCHANGE, 5).add(ThaumcraftCompat.MYTHICAL, 10));
        registerEntity("myrmex_soldier", new AspectList().add(Aspect.BEAST, 15).add(Aspect.PROTECT, 5).add(ThaumcraftCompat.MYTHICAL, 15));
        registerEntity("myrmex_sentinel", new AspectList().add(Aspect.BEAST, 20).add(Aspect.PROTECT, 10).add(ThaumcraftCompat.MYTHICAL, 20));
        registerEntity("myrmex_royal", new AspectList().add(Aspect.BEAST, 20).add(Aspect.SENSES, 10).add(Aspect.FLIGHT, 10).add(ThaumcraftCompat.MYTHICAL, 20));
        registerEntity("myrmex_queen", new AspectList().add(Aspect.BEAST, 35).add(Aspect.EXCHANGE, 10).add(Aspect.LIFE, 10).add(ThaumcraftCompat.MYTHICAL, 30));
        registerEntity("myrmex_swarmer", new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 5).add(Aspect.FLIGHT, 5).add(ThaumcraftCompat.MYTHICAL, 5));
        registerEntity("seaserpent", new AspectList().add(ThaumcraftCompat.MYTHICAL, 40).add(Aspect.BEAST, 30).add(Aspect.WATER, 20).add(Aspect.DEATH, 25).add(Aspect.DARKNESS, 10));
        registerEntity("amphithere", new AspectList().add(ThaumcraftCompat.MYTHICAL, 30).add(Aspect.FLIGHT, 40).add(Aspect.BEAST, 10).add(Aspect.AIR, 20));

    }
}
