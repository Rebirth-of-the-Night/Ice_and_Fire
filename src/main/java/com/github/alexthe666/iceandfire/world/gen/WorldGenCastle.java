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
import java.util.concurrent.*;

public class WorldGenCastle extends WorldGenerator {
    private static final BlockPos CASTLE_POSITION = new BlockPos(-126, 90, -330);
    private static final int CASTLE_WIDTH = 280;
    private static final int CASTLE_HEIGHT = 130;
    private static final int CASTLE_DEPTH = 280;
    private static final int PART_SIZE = 32;
    private static final int BATCH_SIZE = 8;

    private static final ExecutorService GENERATION_EXECUTOR =
            new ThreadPoolExecutor(
                    4,
                    8,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private int count = 1;
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("Castle-Gen-Thread-" + count++);
                            thread.setPriority(Thread.NORM_PRIORITY);
                            return thread;
                        }
                    }
            );

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

        CompletableFuture.runAsync(() -> {
            preloadChunks(worldIn, origin);
            generateFoundation(worldIn, origin);
        }, GENERATION_EXECUTOR).join();

        List<List<TemplatePlacement>> batches = createBatches(origin);

        CompletableFuture<?>[] futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() ->
                        processBatch(worldIn, templateManager, batch), GENERATION_EXECUTOR))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        return true;
    }

    private List<List<TemplatePlacement>> createBatches(BlockPos origin) {
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

        List<List<TemplatePlacement>> batches = new ArrayList<>();
        for (int i = 0; i < allPlacements.size(); i += BATCH_SIZE) {
            batches.add(allPlacements.subList(i,
                    Math.min(i + BATCH_SIZE, allPlacements.size())));
        }
        return batches;
    }

    private void preloadChunks(World world, BlockPos origin) {
        int minChunkX = origin.getX() >> 4;
        int minChunkZ = origin.getZ() >> 4;
        int maxChunkX = (origin.getX() + CASTLE_WIDTH) >> 4;
        int maxChunkZ = (origin.getZ() + CASTLE_DEPTH) >> 4;

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                final int fx = x, fz = z;
                futures.add(CompletableFuture.runAsync(() ->
                        world.getChunkProvider().provideChunk(fx, fz), GENERATION_EXECUTOR));
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void generateFoundation(World world, BlockPos origin) {
        IBlockState foundation = IafBlockRegistry.dread_stone.getDefaultState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int x = 0; x < CASTLE_WIDTH; x += 16) {
            final int fx = x;
            futures.add(CompletableFuture.runAsync(() -> {
                for (int ix = fx; ix < Math.min(fx + 16, CASTLE_WIDTH); ix++) {
                    for (int z = 0; z < CASTLE_DEPTH; z++) {
                        mutablePos.setPos(origin.getX() + ix, origin.getY() - 1, origin.getZ() + z);
                        world.setBlockState(mutablePos, foundation, 2);
                    }
                }
            }, GENERATION_EXECUTOR));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processBatch(World world, TemplateManager templateManager,
                              List<TemplatePlacement> batch) {
        for (TemplatePlacement placement : batch) {
            Template template = templateManager.getTemplate(null, placement.resourceLocation);
            if (template != null) {
                template.addBlocksToWorld(world, placement.pos,
                        CASTLE_PROCESSOR, PLACEMENT_SETTINGS, 2);
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