package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadCastleProcessor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class WorldGenCastle extends WorldGenerator {

    static final TemplateManager RES_LOC_TEMPLATE_MANAGER = new TemplateManager("Alex is a wanker", DataFixesManager.createFixer());


    public WorldGenCastle() {
        super(false);
    }

    public static BlockPos getGround(BlockPos pos, World world) {
        return getGround(pos.getX(), pos.getZ(), world);
    }

    public static BlockPos getGround(int x, int z, World world) {
        BlockPos skyPos = new BlockPos(x, world.getHeight(), z);
        while ((!world.getBlockState(skyPos).isOpaqueCube() || canHeightSkipBlock(skyPos, world)) && skyPos.getY() > 1) {
            skyPos = skyPos.down();
        }
        return skyPos;
    }

    private static boolean canHeightSkipBlock(BlockPos pos, World world) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLeaves || state.getBlock() instanceof BlockLiquid;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {

        if(!checkIfCanGenAt(worldIn, position, 1000, 1000))
            return false;

        position = position.add(rand.nextInt(8) - 4, 1, rand.nextInt(8) - 4);
        int totalW = 274, totalH = 130, totalD = 274; // TODO: 27.06.2022 change
        int partW = 32, partH = 32, partD = 32;




        MinecraftServer server = worldIn.getMinecraftServer();
        BlockPos origin = getGround(position, worldIn);

        //IBlockState dirt = worldIn.getBlockState(origin.down(2));
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();


        for (int x = 0, i = 0; x < totalW; x = Math.min(totalW, x + partW), i++)
            for (int y = 0, j = 0; y < totalH; y = Math.min(totalH, y + partH), j++)
                for (int z = 0, k = 0; z < totalD; z = Math.min(totalD, z + partD), k++) {
                    ResourceLocation res = new ResourceLocation(IceAndFire.MODID+":castle/castle" + "_" + i + "_" + j + "_" + k);
                    BlockPos pos = origin.add(x, y, z);
                    Template template = RES_LOC_TEMPLATE_MANAGER.getTemplate(null, res);

                    for(int t = 0; t < totalW; t++)
                        for(int u = 0; u < totalW; u++)
                            worldIn.setBlockState(origin.add(t, -1, u), IafBlockRegistry.dread_stone.getDefaultState(), 2);

                    template.addBlocksToWorld(worldIn, pos, new DreadCastleProcessor(), new PlacementSettings(), 2);
                }

        return true;
    }

    // TODO: 27.06.2022 implement a proper check
    public static boolean checkIfCanGenAt(World world, BlockPos middle, int x, int z) {
        return middle.getX() % x == 0 && middle.getZ() % z == 0;
    }
}