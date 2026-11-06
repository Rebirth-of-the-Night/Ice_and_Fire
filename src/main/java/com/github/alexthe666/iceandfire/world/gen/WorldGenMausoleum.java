package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.world.gen.processor.DreadRuinProcessor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class WorldGenMausoleum extends WorldGenerator {

    public EnumFacing facing;
    private static final ResourceLocation STRUCTURE = new ResourceLocation(IceAndFire.MODID, "dread_mausoleum");
    private static final ResourceLocation STRUCTURE_OLD = new ResourceLocation(IceAndFire.MODID, "dread_mausoleum_forge");
    private static final int SNOW_MOUND_CHANCE = 3;
    private static final int SNOW_MOUND_MARGIN = 12;

    public WorldGenMausoleum(EnumFacing facing) {
        super(false);
        this.facing = facing;
    }

    public static boolean isPartOfRuin(IBlockState state) {
        return DragonUtils.isDreadBlock(state);
    }

    public static Rotation getRotationFromFacing(EnumFacing facing) {
        switch (facing) {
            case EAST:
                return Rotation.CLOCKWISE_90;
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                return Rotation.NONE;
        }
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
        position = position.add(rand.nextInt(8) - 4, 1, rand.nextInt(8) - 4);
        MinecraftServer server = worldIn.getMinecraftServer();
        BlockPos height = getGround(position, worldIn);
        IBlockState dirt = worldIn.getBlockState(height.down(2));
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(server, IceAndFire.CONFIG.enableDreadlands ? STRUCTURE : STRUCTURE_OLD);
        PlacementSettings settings = new PlacementSettings().setRotation(getRotationFromFacing(facing));
        BlockPos pos = height.offset(facing, template.getSize().getZ() / 2).offset(facing.rotateYCCW(), template.getSize().getX() / 2);
        if (checkIfCanGenAt(worldIn, pos, template.getSize().getX(), template.getSize().getZ(), facing)) {
            template.addBlocksToWorld(worldIn, pos, new DreadRuinProcessor(position, settings, null), settings, 2);
            for (BlockPos underPos : BlockPos.getAllInBox(
                    height.down().offset(facing, -template.getSize().getZ() / 2).offset(facing.rotateYCCW(), -template.getSize().getX() / 2),
                    height.down(3).offset(facing, template.getSize().getZ() / 2).offset(facing.rotateYCCW(), template.getSize().getX() / 2)
            )) {
                if (!worldIn.getBlockState(underPos).isOpaqueCube() && !worldIn.isAirBlock(underPos.up())) {
                    worldIn.setBlockState(underPos, dirt);
                    worldIn.setBlockState(underPos.down(), dirt);
                    worldIn.setBlockState(underPos.down(2), dirt);
                    worldIn.setBlockState(underPos.down(3), dirt);
                }
            }
            Random snowRandom = new Random(worldIn.getSeed() ^ pos.toLong());
            if (pos.getY() + template.getSize().getY() + 8 < worldIn.getHeight()
                    && snowRandom.nextInt(SNOW_MOUND_CHANCE) == 0) {
                addSnowMound(worldIn, pos, template.getSize(), settings, snowRandom);
            } else {
                addSnowCover(worldIn, pos, template, settings);
            }
        }
        return true;
    }

    private void addSnowMound(World world, BlockPos origin, BlockPos size, PlacementSettings settings, Random random) {
        int centerX = size.getX() / 2;
        int centerZ = size.getZ() / 2;
        int roofY = origin.getY() + size.getY();
        NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin(random, 1);
        IBlockState snow = Blocks.SNOW.getDefaultState();
        IBlockState ice = Blocks.PACKED_ICE.getDefaultState();
        BlockPos.MutableBlockPos fill = new BlockPos.MutableBlockPos();

        for (int x = -SNOW_MOUND_MARGIN; x < size.getX() + SNOW_MOUND_MARGIN; x++) {
            for (int z = -SNOW_MOUND_MARGIN; z < size.getZ() + SNOW_MOUND_MARGIN; z++) {
                // In both templates the skull projects from the front at Z=0 and the gate sits at Z=2.
                if (z < 2 && Math.abs(x - centerX) <= 4 + Math.max(0, -z) / 4) {
                    continue;
                }

                int outsideX = Math.max(0, Math.max(-x, x - size.getX() + 1));
                int outsideZ = Math.max(0, Math.max(-z, z - size.getZ() + 1));
                double taper = 1.0 - Math.hypot(outsideX, outsideZ) / SNOW_MOUND_MARGIN;
                if (taper <= 0.0) {
                    continue;
                }

                BlockPos column = origin.add(Template.transformedBlockPos(settings, new BlockPos(x, 0, z)));
                int surfaceY = world.getPrecipitationHeight(column).getY();
                double variation = noise.getValue(x * 0.18, z * 0.18);
                double crown = Math.max(0.0, 1.0 - Math.hypot((x - centerX) / (double) centerX, (z - centerZ) / (double) centerZ));
                double crestY = roofY + 3.0 + 4.0 * crown + variation;
                int topY = (int) Math.floor(surfaceY + (crestY - surfaceY) * taper);
                int iceY = origin.getY() + 4 + (int) Math.floor(variation * 3.0);

                for (int y = surfaceY; y < topY; y++) {
                    fill.setPos(column.getX(), y, column.getZ());
                    IBlockState existing = world.getBlockState(fill);
                    if (existing.getMaterial().isReplaceable()) {
                        world.setBlockState(fill, y >= iceY && y < iceY + 4 && y < topY - 1 ? ice : snow, 2);
                    }
                }
            }
        }
    }

    private void addSnowCover(World world, BlockPos origin, Template template, PlacementSettings settings) {
        // Rotation can extend the template into negative X/Z relative to its origin.
        BlockPos corner = origin.add(Template.transformedBlockPos(settings, template.getSize().add(-1, -1, -1)));
        int minX = Math.min(origin.getX(), corner.getX());
        int maxX = Math.max(origin.getX(), corner.getX());
        int minZ = Math.min(origin.getZ(), corner.getZ());
        int maxZ = Math.max(origin.getZ(), corner.getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos snowPos = world.getPrecipitationHeight(new BlockPos(x, 0, z));
                // Only cover exposed structure blocks, leaving surrounding terrain alone.
                if (snowPos.getY() > origin.getY() && snowPos.getY() <= corner.getY() + 1
                        && world.getBiome(snowPos).getEnableSnow()
                        && isPartOfRuin(world.getBlockState(snowPos.down()))
                        && world.canSnowAt(snowPos, true)) {
                    world.setBlockState(snowPos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }
    }

    public boolean checkIfCanGenAt(World world, BlockPos middle, int x, int z, EnumFacing facing) {
        return !isPartOfRuin(world.getBlockState(middle.offset(facing, z / 2))) && !isPartOfRuin(world.getBlockState(middle.offset(facing.getOpposite(), z / 2))) &&
                !isPartOfRuin(world.getBlockState(middle.offset(facing.rotateY(), x / 2))) && !isPartOfRuin(world.getBlockState(middle.offset(facing.rotateYCCW(), x / 2)));
    }
}
