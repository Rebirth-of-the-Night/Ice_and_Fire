package com.github.alexthe666.iceandfire.world.village;

import com.github.alexthe666.iceandfire.entity.IafVillagerRegistry;
import com.github.alexthe666.iceandfire.world.gen.WorldGenScriberHouse;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class ComponentScriberHouse extends StructureVillagePieces.Village {

    public ComponentScriberHouse(StructureVillagePieces.Start startPiece, int p2, Random random, StructureBoundingBox structureBox, EnumFacing facing) {
        super(startPiece, p2);
        this.setCoordBaseMode(facing);
        this.boundingBox = structureBox;
    }

    public static ComponentScriberHouse buildComponent(StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int p5) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, -1, 11, 12, 14, facing);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentScriberHouse(startPiece, p5, random, structureboundingbox, facing) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(world, sbb);
            if (this.averageGroundLvl < 0) {
                return false;
            }
            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 7, 0);
        }
        BlockPos blockpos = new BlockPos(this.getXWithOffset(11, -1), this.getYWithOffset(0), this.getZWithOffset(11, -1));
        EnumFacing facing = this.getCoordBaseMode().getOpposite();
        BlockPos genPos = blockpos.up();
        if (facing == EnumFacing.SOUTH) {
            genPos = genPos.offset(EnumFacing.WEST, 11).offset(EnumFacing.SOUTH, 2);
        }
        this.spawnVillagers(world, sbb, 7, 8, 1, 1);
        return new WorldGenScriberHouse(this, facing.rotateY()).generate(world, random, genPos);
    }

    @Override
    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof)
    {
        return IafVillagerRegistry.INSTANCE.scriber;
    }

}
