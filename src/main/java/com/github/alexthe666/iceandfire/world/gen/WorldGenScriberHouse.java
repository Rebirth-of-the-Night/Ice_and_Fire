package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.gen.processor.ScriberVillageProcessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class WorldGenScriberHouse extends WorldGenerator {

    private static final ResourceLocation HOUSE = new ResourceLocation(IceAndFire.MODID, "scriber_house");
    private static final ResourceLocation HOUSE_DESERT = new ResourceLocation(IceAndFire.MODID, "scriber_house_3");
    private Rotation rotation;

    public WorldGenScriberHouse(EnumFacing facing) {
        switch (facing) {
            case SOUTH:
                rotation = Rotation.CLOCKWISE_180;
                break;
            case EAST:
                rotation = Rotation.CLOCKWISE_90;
                break;
            case WEST:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            default:
                rotation = Rotation.NONE;
                break;
        }
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (worldIn == null) {
            return false;
        }
        MinecraftServer server = worldIn.getMinecraftServer();
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
        PlacementSettings settings = new PlacementSettings().setRotation(rotation).setMirror(Mirror.NONE);
        ResourceLocation location;
        Biome biome = worldIn.getBiome(position);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)) {
            location = HOUSE_DESERT;
        } else {
            location = HOUSE;
        }
        Template template = templateManager.getTemplate(server, location);

        int xSize = template.getSize().getX() / 2;
        int zSize = template.getSize().getZ() / 2;

        template.addBlocksToWorld(worldIn, position.up(3).offset(EnumFacing.NORTH, xSize).offset(EnumFacing.SOUTH, zSize), new ScriberVillageProcessor(), settings, 2);
        return true;
    }
}