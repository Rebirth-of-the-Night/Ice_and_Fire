package com.github.alexthe666.iceandfire.entity.tile.keletu;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.block.keletu.BlockDreadPistonBase;
import com.github.alexthe666.iceandfire.block.keletu.BlockDreadPistonExtension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDreadPistonRenderer extends TileEntitySpecialRenderer<TileEntityDreadPiston> {

    private BlockRendererDispatcher blockRenderer;

    public void render(TileEntityDreadPiston te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); //Forge: Delay this from constructor to allow us to change it later
        BlockPos blockpos = te.getPos();
        IBlockState iblockstate = te.getPistonState();
        Block block = iblockstate.getBlock();

        if (iblockstate.getMaterial() != Material.AIR && te.getProgress(partialTicks) < 1.0F)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(7425);
            }
            else
            {
                GlStateManager.shadeModel(7424);
            }

            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
            bufferbuilder.setTranslation(x - (double)blockpos.getX() + (double)te.getOffsetX(partialTicks), y - (double)blockpos.getY() + (double)te.getOffsetY(partialTicks), z - (double)blockpos.getZ() + (double)te.getOffsetZ(partialTicks));
            World world = this.getWorld();

            if (block == IafBlockRegistry.dread_piston_head && te.getProgress(partialTicks) <= 0.25F)
            {
                iblockstate = iblockstate.withProperty(BlockDreadPistonExtension.SHORT, Boolean.TRUE);
                this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, true);
            }
            else if (te.shouldPistonHeadBeRendered() && !te.isExtending())
            {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = block == IafBlockRegistry.dread_sticky_piston ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate1 = IafBlockRegistry.dread_piston_head.getDefaultState().withProperty(BlockDreadPistonExtension.TYPE, blockpistonextension$enumpistontype).withProperty(BlockDreadPistonExtension.FACING, iblockstate.getValue(BlockDreadPistonExtension.FACING));
                iblockstate1 = iblockstate1.withProperty(BlockDreadPistonExtension.SHORT, te.getProgress(partialTicks) >= 0.5F);
                this.renderStateModel(blockpos, iblockstate1, bufferbuilder, world, true);
                bufferbuilder.setTranslation(x - (double)blockpos.getX(), y - (double)blockpos.getY(), z - (double)blockpos.getZ());
                iblockstate = iblockstate.withProperty(BlockDreadPistonBase.EXTENDED, Boolean.TRUE);
                this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, true);
            }
            else
            {
                this.renderStateModel(blockpos, iblockstate, bufferbuilder, world, false);
            }

            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    private boolean renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World p_188186_4_, boolean checkSides)
    {
        return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_, this.blockRenderer.getModelForState(state), state, pos, buffer, checkSides);
    }
}