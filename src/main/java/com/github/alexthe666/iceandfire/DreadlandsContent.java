package com.github.alexthe666.iceandfire;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Identifies content that only exists for the Dreadlands dimension, so it can be skipped
 * when the dimension is disabled in the config.
 * <p>
 * Mausoleums generate in the overworld and are built from dread blocks and dread spawners, so the
 * content they need stays registered while mausoleum generation is enabled.
 */
public final class DreadlandsContent {

    private static final Set<String> MAUSOLEUM_BLOCKS = new HashSet<>(Arrays.asList(
            "dread_stone", "dread_stone_bricks", "dread_stone_bricks_chiseled", "dread_stone_bricks_cracked",
            "dread_stone_bricks_mossy", "dread_stone_tile", "dread_stone_face", "dread_torch", "dread_stone_stairs",
            "dread_stone_slab", "dread_stone_slab_double", "dreadwood_planks", "dreadwood_planks_lock", "dread_spawner"));
    private static final Set<String> MAUSOLEUM_ENTITIES = new HashSet<>(Arrays.asList(
            "dread_thrall", "dread_ghoul", "dread_beast", "dread_scuttler", "dread_knight"));

    private DreadlandsContent() {
    }

    private static boolean neededByMausoleums(Set<String> names, String path) {
        return IceAndFire.CONFIG.generateMausoleums && names.contains(path);
    }

    public static boolean isEnabled() {
        return IceAndFire.CONFIG.enableDreadlands;
    }

    /** True if registering this block should be skipped. */
    public static boolean shouldSkip(Block block) {
        return !isEnabled() && block.getRegistryName() != null && isDreadName(block.getRegistryName().getPath())
                && !neededByMausoleums(MAUSOLEUM_BLOCKS, block.getRegistryName().getPath());
    }

    /** True if registering this item should be skipped. */
    public static boolean shouldSkip(Item item) {
        return !isEnabled() && item.getRegistryName() != null && isDreadName(item.getRegistryName().getPath())
                && !neededByMausoleums(MAUSOLEUM_BLOCKS, item.getRegistryName().getPath());
    }

    /** True if registering the entity with this registry name should be skipped. */
    public static boolean shouldSkipEntity(String name) {
        return !isEnabled() && ((isDreadName(name) && !neededByMausoleums(MAUSOLEUM_ENTITIES, name)) || name.equals("ballista") || name.equals("final_ballista") || name.equals("ballista_arrow"));
    }

    private static boolean isDreadName(String path) {
        return path.startsWith("dread") || path.equals("cracked_dreadstone") || path.equals("lich_staff");
    }
}
