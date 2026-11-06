package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.world.BiomeSpawnFilter;
import com.github.alexthe666.iceandfire.world.dimension.ChunkProviderDreadLands;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadCastleProcessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public final class WorldGenCastle {
    private static final int CASTLE_X = -126;
    private static final int CASTLE_Z = -330;
    private static final int WIDTH = 280;
    private static final int HEIGHT = 130;
    private static final int DEPTH = 280;
    private static final int PART_SIZE = 32;
    private static final int FOUNDATION_HEIGHT = 20;
    private static final DreadCastleProcessor PROCESSOR = new DreadCastleProcessor();

    private WorldGenCastle() {
    }

    public static void generateSlice(World world, ChunkProviderDreadLands generator, int chunkX, int chunkZ) {
        // As in vanilla decoration, offset the window by 8. All four population
        // chunks are loaded, with room for template neighbor updates on every side.
        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;
        int minX = Math.max(x, CASTLE_X);
        int minZ = Math.max(z, CASTLE_Z);
        int maxX = Math.min(x + 15, CASTLE_X + WIDTH - 1);
        int maxZ = Math.min(z + 15, CASTLE_Z + DEPTH - 1);
        if (minX > maxX || minZ > maxZ) {
            return;
        }

        if (!BiomeSpawnFilter.DREAD_CASTLE.allows(world.getBiomeProvider().getBiome(new BlockPos(CASTLE_X, 0, CASTLE_Z)), true)) {
            return;
        }

        int originY = generator.getCastleBaseY(CASTLE_X, CASTLE_Z);
        int foundationY = originY - 1;
        IBlockState foundation = IafBlockRegistry.dread_stone.getDefaultState();
        // Older versions placed the entire foundation at once, including in
        // chunks that could still be awaiting their normal population pass.
        if (world.getBlockState(new BlockPos(minX, foundationY, minZ)) == foundation
                && world.getBlockState(new BlockPos(maxX, foundationY, maxZ)) == foundation) {
            return;
        }

        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        PlacementSettings settings = new PlacementSettings().setBoundingBox(
                new StructureBoundingBox(minX, 0, minZ, maxX, world.getHeight() - 1, maxZ));
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int blockX = minX; blockX <= maxX; blockX++) {
            for (int blockZ = minZ; blockZ <= maxZ; blockZ++) {
                for (int y = foundationY; y < foundationY + FOUNDATION_HEIGHT; y++) {
                    world.setBlockState(pos.setPos(blockX, y, blockZ), foundation, 2);
                }
            }
        }

        for (int partY = 0; partY * PART_SIZE < HEIGHT; partY++) {
            for (int partX = (minX - CASTLE_X) / PART_SIZE; partX <= (maxX - CASTLE_X) / PART_SIZE; partX++) {
                for (int partZ = (minZ - CASTLE_Z) / PART_SIZE; partZ <= (maxZ - CASTLE_Z) / PART_SIZE; partZ++) {
                    ResourceLocation id = new ResourceLocation(IceAndFire.MODID, "castle/castle_" + partX + "_" + partY + "_" + partZ);
                    Template template = manager.getTemplate(null, id);
                    if (template != null) {
                        BlockPos partOrigin = new BlockPos(CASTLE_X + partX * PART_SIZE, originY + partY * PART_SIZE,
                                CASTLE_Z + partZ * PART_SIZE);
                        template.addBlocksToWorld(world, partOrigin, PROCESSOR, settings, 2);
                    }
                }
            }
        }
    }
}
