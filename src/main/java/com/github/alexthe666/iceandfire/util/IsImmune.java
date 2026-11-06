package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class IsImmune {
	private static final ImmunityList DRAGON_ICE = new ImmunityList("Entities immune to dragon ice", () -> IceAndFire.CONFIG.dragonIceImmuneEntities);
	private static final ImmunityList DRAGON_FIRE = new ImmunityList("Entities immune to dragon fire", () -> IceAndFire.CONFIG.dragonFireImmuneEntities);
	private static final ImmunityList DRAGON_LIGHTNING = new ImmunityList("Entities immune to dragon lightning", () -> IceAndFire.CONFIG.dragonLightningImmuneEntities);
	private static final ImmunityList STONE = new ImmunityList("Entities immune to being turned to stone", () -> IceAndFire.CONFIG.stoneImmuneEntities);
	private static final ImmunityList CHAINS = new ImmunityList("Entities immune to being tied to a chain", () -> IceAndFire.CONFIG.chainImmuneEntities);

	public static boolean toDragonIce(Entity entity) {
		return DRAGON_ICE.matches(entity);
	}

	public static boolean toDragonFire(Entity entity) {
		return DRAGON_FIRE.matches(entity);
	}

	public static boolean toDragonLightning(Entity entity) {
		return DRAGON_LIGHTNING.matches(entity);
	}

	public static boolean toStone(Entity entity) {
		return STONE.matches(entity);
	}

	public static boolean toChains(Entity entity) {
		return CHAINS.matches(entity);
	}

	/**
	 * Resolves a configured list of entity registry names to classes. Unknown names are skipped
	 * and logged once instead of crashing the ticking entity. The result is cached until the
	 * config array instance changes.
	 */
	private static class ImmunityList {
		private final String configName;
		private final Supplier<String[]> source;
		private String[] resolvedFrom;
		private List<Class<? extends Entity>> classes = new ArrayList<>();

		ImmunityList(String configName, Supplier<String[]> source) {
			this.configName = configName;
			this.source = source;
		}

		boolean matches(Entity entity) {
			for (Class<? extends Entity> cls : resolve()) {
				if (cls.isInstance(entity)) {
					return true;
				}
			}
			return false;
		}

		private synchronized List<Class<? extends Entity>> resolve() {
			String[] names = source.get();
			if (names == resolvedFrom) {
				return classes;
			}
			List<Class<? extends Entity>> result = new ArrayList<>();
			for (String name : names) {
				EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(name));
				if (entry == null || entry.getEntityClass() == null) {
					IceAndFire.logger.warn("Ignoring unknown entity \"{}\" in config option \"{}\": no such entity is registered", name, configName);
				} else {
					result.add(entry.getEntityClass());
				}
			}
			classes = result;
			resolvedFrom = names;
			return result;
		}
	}
}
