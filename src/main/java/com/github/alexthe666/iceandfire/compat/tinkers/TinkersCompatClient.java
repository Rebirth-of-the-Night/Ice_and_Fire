package com.github.alexthe666.iceandfire.compat.tinkers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;

public class TinkersCompatClient {

    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(TinkersCompatClient.class);
        TinkerBook.INSTANCE.addRepository(new FileRepository("iceandfire:tinkers/book"));
        MaterialRenderInfo boneInfo = new MaterialRenderInfo.Default(0XB2AD98);
        boneInfo.setTextureSuffix("bone_base");
        TinkersCompat.MATERIAL_DRAGONBONE.setRenderInfo(boneInfo);
        MaterialRenderInfo desertChitinInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/desert_myrmex"));
        TinkersCompat.MATERIAL_DESERT_MYRMEX.setRenderInfo(desertChitinInfo);
        MaterialRenderInfo jungleChitinInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/jungle_myrmex"));
        TinkersCompat.MATERIAL_JUNGLE_MYRMEX.setRenderInfo(jungleChitinInfo);
        MaterialRenderInfo dragonsteelFireInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/dragonsteel_fire"));
        TinkersCompat.MATERIAL_DRAGONSTEEL_FIRE.setRenderInfo(dragonsteelFireInfo);
        MaterialRenderInfo dragonsteelIceInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/dragonsteel_ice"));
        TinkersCompat.MATERIAL_DRAGONSTEEL_ICE.setRenderInfo(dragonsteelIceInfo);
        MaterialRenderInfo dragonsteelLightningInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/dragonsteel_lightning"));
        TinkersCompat.MATERIAL_DRAGONSTEEL_LIGHTNING.setRenderInfo(dragonsteelLightningInfo);
        MaterialRenderInfo weezerInfo = new MaterialRenderInfo.BlockTexture(new ResourceLocation("iceandfire:tinkers/weezer"));
        TinkersCompat.MATERIAL_WEEZER.setRenderInfo(weezerInfo);
        ModelRegisterUtil.registerModifierModel(TinkersCompat.BURN_I, new ResourceLocation("iceandfire:models/item/tinkers/flame"));
        ModelRegisterUtil.registerModifierModel(TinkersCompat.FREEZE_I, new ResourceLocation("iceandfire:models/item/tinkers/frost"));
        ModelRegisterUtil.registerModifierModel(TinkersCompat.LIGHTNING_I, new ResourceLocation("iceandfire:models/item/tinkers/thunder"));
        
    }

    // Tinkers installs its generic mapper at normal priority; replace it afterwards.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerModels(ModelRegistryEvent event) {
        registerFluidModel(TinkersCompat.MOLTEN_FIRE_DRAGONSTEEL);
        registerFluidModel(TinkersCompat.MOLTEN_ICE_DRAGONSTEEL);
        registerFluidModel(TinkersCompat.MOLTEN_LIGHTNING_DRAGONSTEEL);
    }

    private static void registerFluidModel(Fluid fluid) {
        if (fluid.getBlock() == null) {
            return;
        }
        // Fluid levels share one Forge fluid model; they are not JSON blockstate variants.
        final ModelResourceLocation model = new ModelResourceLocation(
                new ResourceLocation("iceandfire:tinkers_fluid"), fluid.getName());
        ModelLoader.setCustomStateMapper(fluid.getBlock(), new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return model;
            }
        });
    }
}
