package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public final class PathNavigateDread extends PathNavigateGround {
    private final LoadedTerrain terrain;
    private PathFinder finder;
    private BlockPos target;
    private long lastRepath;

    public PathNavigateDread(EntityLiving entity, WorldServer world) {
        super(entity, world);
        terrain = new LoadedTerrain(world);
    }

    @Override
    protected PathFinder getPathFinder() {
        finder = super.getPathFinder();
        return finder;
    }

    @Nullable
    @Override
    public Path getPathToPos(BlockPos pos) {
        if (!canNavigate() || terrain.getChunk(pos) == null) {
            return null;
        }

        // Snap the destination to the walkable surface as PathNavigateGround does.
        if (terrain.getBlockState(pos).getMaterial() == Material.AIR) {
            do {
                pos = pos.down();
            } while (pos.getY() > 0 && terrain.getBlockState(pos).getMaterial() == Material.AIR);
            if (pos.getY() > 0) {
                pos = pos.up();
            } else {
                while (pos.getY() < world.getHeight() && terrain.getBlockState(pos).getMaterial() == Material.AIR) {
                    pos = pos.up();
                }
            }
        }
        while (pos.getY() < world.getHeight() && terrain.getBlockState(pos).getMaterial().isSolid()) {
            pos = pos.up();
        }

        if (currentPath != null && !currentPath.isFinished() && pos.equals(target)) {
            return currentPath;
        }
        target = pos;
        world.profiler.startSection("pathfind");
        try {
            return finder.findPath(terrain, entity, pos, getPathSearchRange());
        } finally {
            world.profiler.endSection();
        }
    }

    @Override
    public void updatePath() {
        long time = world.getTotalWorldTime();
        if (time - lastRepath <= 20L) {
            tryUpdatePath = true;
        } else if (target != null) {
            currentPath = null;
            currentPath = getPathToPos(target);
            lastRepath = time;
            tryUpdatePath = false;
        }
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d start, Vec3d end, int sizeX, int sizeY, int sizeZ) {
        // Vanilla's shortcut checks read the world directly, including the area around the mob.
        int margin = Math.max(sizeX, sizeZ) * 2 + 2;
        BlockPos min = new BlockPos(Math.min(start.x, end.x) - margin, 0, Math.min(start.z, end.z) - margin);
        BlockPos max = new BlockPos(Math.max(start.x, end.x) + margin, 0, Math.max(start.z, end.z) + margin);
        return world.isAreaLoaded(min, max) && super.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ);
    }

    private static final class LoadedTerrain implements IBlockAccess {
        private final WorldServer world;

        private LoadedTerrain(WorldServer world) {
            this.world = world;
        }

        @Nullable
        private Chunk getChunk(BlockPos pos) {
            // getLoadedChunk clears the pending unload flag as a side effect.
            return world.getChunkProvider().loadedChunks.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            Chunk chunk = getChunk(pos);
            // An unloaded column is an obstacle, including for partial paths near chunk edges.
            return chunk == null ? Blocks.BARRIER.getDefaultState() : chunk.getBlockState(pos);
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            Chunk chunk = getChunk(pos);
            return chunk == null ? null : chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            IBlockState state = getBlockState(pos);
            return state.getBlock().isAir(state, this, pos);
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return getBlockState(pos).getStrongPower(this, pos, direction);
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
            return getBlockState(pos).isSideSolid(this, pos, side);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return world.getCombinedLight(pos, lightValue);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Biome getBiome(BlockPos pos) {
            return world.getBiomeProvider().getBiome(pos);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public WorldType getWorldType() {
            return world.getWorldType();
        }
    }
}
