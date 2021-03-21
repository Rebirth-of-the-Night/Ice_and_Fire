package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ISyncMount;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IceAndFireCoreUtils {
    private static final int index = 0;

    public static double getMoveThreshold(NetHandlerPlayServer e) {
        return IceAndFire.CONFIG.dragonMovedWronglyFix && e.player.getLowestRidingEntity() instanceof ISyncMount ? 1.5D : 0.0625D;
    }

    public static double getFastestEntityMotionSpeed(NetHandlerPlayServer e) {
        return IceAndFire.CONFIG.dragonMovedWronglyFix && e.player.getLowestRidingEntity() instanceof ISyncMount ? 1000.0D : 100.0D;
    }
    
    public static boolean is(ItemStack stack, String name) {
    	if (Item.getByNameOrId(name) == stack.getItem()) return true;
	    int oreId = OreDictionary.getOreID(name);
	    for (int checkAgainst : OreDictionary.getOreIDs(stack))
		    if (oreId == checkAgainst)
		    	return true;
        return false;
    }

    public static Set<Item> all(String name) {
    	Set<Item> itemSet = new HashSet<>();
	    Item itemOrNull = Item.getByNameOrId(name);
	    if (itemOrNull != null)
	    	itemSet.add(itemOrNull);

	    itemSet.addAll(OreDictionary.getOres(name).stream().map(ItemStack::getItem).collect(Collectors.toSet()));

	    return itemSet;
    }

	public static boolean entityNameArrayContains(String[] names, Entity entity) {
		return Arrays.stream(names)
			.map(ResourceLocation::new)
			.filter((resourceLocation) -> {
				if (ForgeRegistries.ENTITIES.getValue(resourceLocation) == null) {
					IceAndFire.logger.warn("Couldn't find entity type " + resourceLocation.toString() + ", ignoring");
					return false;
				}
				return true;
			})
			.map(ForgeRegistries.ENTITIES::getValue)
			.map(EntityEntry::getEntityClass)
			.anyMatch((cls) -> cls.isInstance(entity));
	}
}
