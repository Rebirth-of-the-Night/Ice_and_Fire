package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadCastleProcessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenCastle extends WorldGenerator {
    private static final BlockPos CASTLE_POSITION = new BlockPos(-126, 90, -330);
    private static final int CASTLE_WIDTH = 280;
    private static final int CASTLE_HEIGHT = 130;
    private static final int CASTLE_DEPTH = 280;
    private static final int PART_SIZE = 32;

    private static final DreadCastleProcessor CASTLE_PROCESSOR = new DreadCastleProcessor();
    private static final PlacementSettings PLACEMENT_SETTINGS = new PlacementSettings();

    public WorldGenCastle() {
        super(false);
    }

    private static BlockPos getGroundFast(World world, int x, int z) {
        Chunk chunk = world.getChunk(new BlockPos(x, 0, z));
        int y = chunk.getHeightValue(x & 15, z & 15);
        return new BlockPos(x, y, z);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        BlockPos origin = getGroundFast(worldIn, CASTLE_POSITION.getX(), CASTLE_POSITION.getZ());
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();

        // Preload all templates
        List<TemplatePlacement> allPlacements = createPlacements(origin);
        List<Template> templates = new ArrayList<>();
        for (TemplatePlacement placement : allPlacements) {
            Template template = templateManager.getTemplate(null, placement.resourceLocation);
            if (template != null) {
                templates.add(template);
            }
        }

        // Preload chunks
        preloadChunks(worldIn, origin);

        // Generate foundation
        generateFoundation(worldIn, origin);

        // Generate castle parts
        for (int i = 0; i < allPlacements.size(); i++) {
            TemplatePlacement placement = allPlacements.get(i);
            Template template = templates.get(i);
            template.addBlocksToWorld(worldIn, placement.pos, CASTLE_PROCESSOR, PLACEMENT_SETTINGS, 2);
        }

        return true;
    }

    private List<TemplatePlacement> createPlacements(BlockPos origin) {
        List<TemplatePlacement> allPlacements = new ArrayList<>();

        for (int y = 0, j = 0; y < CASTLE_HEIGHT; y = Math.min(CASTLE_HEIGHT, y + PART_SIZE), j++) {
            for (int x = 0, i = 0; x < CASTLE_WIDTH; x = Math.min(CASTLE_WIDTH, x + PART_SIZE), i++) {
                for (int z = 0, k = 0; z < CASTLE_DEPTH; z = Math.min(CASTLE_DEPTH, z + PART_SIZE), k++) {
                    ResourceLocation res = new ResourceLocation(
                            IceAndFire.MODID + ":castle/castle" + "_" + i + "_" + j + "_" + k
                    );
                    BlockPos pos = origin.add(x, y, z);
                    allPlacements.add(new TemplatePlacement(res, pos));
                }
            }
        }

        return allPlacements;
    }

    private void preloadChunks(World world, BlockPos origin) {
        int minChunkX = origin.getX() >> 4;
        int minChunkZ = origin.getZ() >> 4;
        int maxChunkX = (origin.getX() + CASTLE_WIDTH) >> 4;
        int maxChunkZ = (origin.getZ() + CASTLE_DEPTH) >> 4;

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                world.getChunkProvider().provideChunk(x, z);
            }
        }
    }

    private void generateFoundation(World world, BlockPos origin) {
        IBlockState foundation = IafBlockRegistry.dread_stone.getDefaultState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // Define the height range for the foundation
        int foundationHeight = 10; // Fill 10 blocks high (adjust as needed)
        int baseY = origin.getY() - 1; // Start from the ground level

        // Generate foundation in batches
        for (int x = 0; x < CASTLE_WIDTH; x += 16) {
            for (int z = 0; z < CASTLE_DEPTH; z += 16) {
                for (int ix = x; ix < Math.min(x + 16, CASTLE_WIDTH); ix++) {
                    for (int iz = z; iz < Math.min(z + 16, CASTLE_DEPTH); iz++) {
                        // Fill from baseY to baseY + foundationHeight
                        for (int y = baseY; y < baseY + foundationHeight; y++) {
                            mutablePos.setPos(origin.getX() + ix, y, origin.getZ() + iz);
                            world.setBlockState(mutablePos, foundation, 2); // Flag 2 to suppress block updates
                        }
                    }
                }
            }
        }
    }

    private static class TemplatePlacement {
        final ResourceLocation resourceLocation;
        final BlockPos pos;

        TemplatePlacement(ResourceLocation resourceLocation, BlockPos pos) {
            this.resourceLocation = resourceLocation;
            this.pos = pos;
        }
    }
}