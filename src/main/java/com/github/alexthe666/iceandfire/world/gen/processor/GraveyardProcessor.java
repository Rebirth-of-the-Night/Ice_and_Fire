package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.block.keletu.BlockGhostChest;
import com.github.alexthe666.iceandfire.entity.*;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Random;

public class GraveyardProcessor implements ITemplateProcessor {

    private float integrity = 1.0F;
    public static final ResourceLocation DREAD_CHEST_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "graveyard_chest"));

    public GraveyardProcessor() {
    }

    @Nullable
    @Override
    public Template.BlockInfo processBlock(World worldIn, BlockPos pos, Template.BlockInfo blockInfoIn) {
        if (worldIn.rand.nextFloat() <= integrity) {
            if (blockInfoIn.blockState.getBlock() instanceof BlockGhostChest) {
                ResourceLocation loot = DREAD_CHEST_LOOT;
                Random rand = new Random(worldIn.getSeed() + pos.toLong());
                NBTTagCompound tag = blockInfoIn.tileentityData == null ? new NBTTagCompound() : blockInfoIn.tileentityData;
                tag.setString("LootTable", loot.toString());
                tag.setLong("LootTableSeed", rand.nextLong());
                Template.BlockInfo newInfo = new Template.BlockInfo(pos, IafBlockRegistry.ghost_chest.getDefaultState(), tag);
                return newInfo;
            }
            return blockInfoIn;
        }
        return blockInfoIn;

    }
}
