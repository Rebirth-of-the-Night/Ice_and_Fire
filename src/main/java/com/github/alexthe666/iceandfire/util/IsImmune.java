package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.Objects;

public class IsImmune {
	public static boolean toDragonIce(Entity entity) {
		return Arrays
				.stream(IceAndFire.CONFIG.dragonIceImmuneEntities)
				.map(resourceName ->
						Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceName)), "Couldn't find registry entry for " + resourceName)
								.getEntityClass()
				)
				.anyMatch(cls ->
						cls.isInstance(entity)
				);
	}
	
	public static boolean toDragonFire(Entity entity) {
		return Arrays
				.stream(IceAndFire.CONFIG.dragonFireImmuneEntities)
				.map(resourceName ->
						Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceName)), "Couldn't find registry entry for " + resourceName)
								.getEntityClass()
				)
				.anyMatch(cls ->
						cls.isInstance(entity)
				);
	}
	
	public static boolean toDragonLightning(Entity entity) {
		return Arrays
				.stream(IceAndFire.CONFIG.dragonLightningImmuneEntities)
				.map(resourceName ->
						Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceName)), "Couldn't find registry entry for " + resourceName)
								.getEntityClass()
				)
				.anyMatch(cls ->
						cls.isInstance(entity)
				);
	}
	
	public static boolean toStone(Entity entity) {
		return Arrays
				.stream(IceAndFire.CONFIG.stoneImmuneEntities)
				.map(resourceName ->
						Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceName)), "Couldn't find registry entry for " + resourceName)
								.getEntityClass()
				)
				.anyMatch(cls ->
						cls.isInstance(entity)
				);
	}
	
	public static boolean toChains(Entity entity) {
		return Arrays
				.stream(IceAndFire.CONFIG.chainImmuneEntities)
				.map(resourceName ->
						Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(resourceName)), "Couldn't find registry entry for " + resourceName)
								.getEntityClass()
				)
				.anyMatch(cls ->
						cls.isInstance(entity)
				);
	}
}
