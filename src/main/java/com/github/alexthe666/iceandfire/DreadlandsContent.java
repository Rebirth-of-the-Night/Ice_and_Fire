package com.github.alexthe666.iceandfire;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Identifies content that only exists for the Dreadlands dimension, so it can be skipped
 * when the dimension is disabled in the config.
 */
public final class DreadlandsContent {

    private DreadlandsContent() {
    }

    public static boolean isEnabled() {
        return IceAndFire.CONFIG.enableDreadlands;
    }

    /** True if registering this block should be skipped. */
    public static boolean shouldSkip(Block block) {
        return !isEnabled() && block.getRegistryName() != null && isDreadName(block.getRegistryName().getPath());
    }

    /** True if registering this item should be skipped. */
    public static boolean shouldSkip(Item item) {
        return !isEnabled() && item.getRegistryName() != null && isDreadName(item.getRegistryName().getPath());
    }

    /** True if registering the entity with this registry name should be skipped. */
    public static boolean shouldSkipEntity(String name) {
        return !isEnabled() && (isDreadName(name) || name.equals("ballista") || name.equals("final_ballista") || name.equals("ballista_arrow"));
    }

    private static boolean isDreadName(String path) {
        return path.startsWith("dread") || path.equals("cracked_dreadstone") || path.equals("lich_staff");
    }
}
