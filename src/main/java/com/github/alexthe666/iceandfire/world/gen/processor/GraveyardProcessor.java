package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.keletu.BlockGhostChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;

public class GraveyardProcessor implements ITemplateProcessor {

    private float integrity = 1.0F;
    public static final ResourceLocation GHOST_CHEST_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "graveyard_chest"));

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        if (worldIn.rand.nextFloat() <= integrity) {
            if (blockInfoIn.blockState.getBlock() instanceof BlockGhostChest) {
                Random rand = new Random(worldIn.getSeed() + pos.toLong());
                NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
                tag.setString("LootTable", GHOST_CHEST_LOOT.toString());
                tag.setLong("LootTableSeed", rand.nextLong());
                return new Template.BlockInfo(pos, blockInfoIn.blockState, tag);
            }
        }
        return blockInfoIn;
    }
}