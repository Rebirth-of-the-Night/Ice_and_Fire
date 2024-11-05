package com.github.alexthe666.iceandfire.world.gen.processor;

import net.minecraft.block.BlockChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;

public class ScriberVillageProcessor implements ITemplateProcessor {

    private float integrity = 1.0F;
    public static final ResourceLocation SCRIBE_CHEST_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "village_scribe"));

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        if (worldIn.rand.nextFloat() <= integrity) {
            if (blockInfoIn.blockState.getBlock() instanceof BlockChest) {
                Random rand = new Random(worldIn.getSeed() + pos.toLong());
                NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
                tag.setString("LootTable", SCRIBE_CHEST_LOOT.toString());
                tag.setLong("LootTableSeed", rand.nextLong());
                return new Template.BlockInfo(pos, blockInfoIn.blockState, tag);
            }
        }
        return blockInfoIn;
    }
}