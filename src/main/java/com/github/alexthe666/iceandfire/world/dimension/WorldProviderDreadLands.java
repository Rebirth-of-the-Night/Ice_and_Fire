package com.github.alexthe666.iceandfire.world.dimension;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class WorldProviderDreadLands extends WorldProvider {
    private boolean queenDefeated;

    @SideOnly(Side.CLIENT)
    public void setQueenDefeated(boolean queenDefeated) {
        this.queenDefeated = queenDefeated;
    }

    @SideOnly(Side.CLIENT)
    public float getSnowfallIntensity() {
        return queenDefeated ? 0.45F : 1.0F;
    }

    @Override
    public void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderSingle(IafWorldRegistry.DREADLANDS_BIOME);
        this.nether = false;
    }

    @Override
    public DimensionType getDimensionType() {
        return IafWorldRegistry.DREADLANDS_DIM;
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks) {
        return new Vec3d(0.004D, 0.05D, 0.072D);
    }

    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return new Vec3d(0.02D, 0.25D, 0.36D);
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5F; // Midnight
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkProviderDreadLands(this.world, this.world.getSeed());
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public net.minecraftforge.client.IRenderHandler getWeatherRenderer() {
        return (net.minecraftforge.client.IRenderHandler) IceAndFire.PROXY.getDreadlandsRender(1);
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
        return (net.minecraftforge.client.IRenderHandler) IceAndFire.PROXY.getDreadlandsRender(0);
    }

}
