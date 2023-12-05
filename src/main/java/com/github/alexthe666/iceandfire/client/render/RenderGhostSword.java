package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.entity.EntityGhostSword;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeModContainer;
import org.lwjgl.opengl.GL11;

public class RenderGhostSword
		extends RenderSnowball<EntityGhostSword>
{
	RenderItem renderItem;

	public RenderGhostSword(RenderManager renderManagerIn)
	{
		super(renderManagerIn, IafItemRegistry.ghost_sword, Minecraft.getMinecraft().getRenderItem());
		renderItem = Minecraft.getMinecraft().getRenderItem();
	}

	public static int packARGB(float a, float r, float g, float b)
	{
		return (((int) (a * 255F)) << 24) | (((int) (r * 255F)) << 16) | (((int) (g * 255F)) << 8) | ((int) (b * 255F));
	}

	@Override
	public void doRender(EntityGhostSword entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y + 0.5, (float) z);
		GlStateManager.enableRescaleNormal();

		//GlStateManager.translate(0.125, 0.125, 0.125);

		GlStateManager.rotate(entity.prevRotationYaw + (entity.prevRotationYaw - entity.rotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);

		GlStateManager.rotate((entity.ticksExisted + partialTicks) * 40F, 0.0F, 0.0F, 1.0F);

		//GlStateManager.translate(-0.125, -0.125, -0.125);

		GlStateManager.rotate(-180, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(2, 2, 2);

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if(this.renderOutlines)
		{
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		ItemStack stack = getStackToRender(entity);
		IBakedModel model = renderItem.getItemModelWithOverrides(stack, entity.world, null);

		{
			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.0F);
			GlStateManager.enableRescaleNormal();
//			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.DST_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			GlStateManager.pushMatrix();

			model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GROUND, false);
			renderModel(model, packARGB(entity.getAlpha(partialTicks), 1, 1, 1), stack);

			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			GlStateManager.cullFace(CullFace.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		}

		if(this.renderOutlines)
		{
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	private void renderModel(IBakedModel model, int color, ItemStack stack)
	{
		if(ForgeModContainer.allowEmissiveItems)
		{
			ForgeHooksClient.renderLitItem(renderItem, model, color, stack);
			return;
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		for(EnumFacing enumfacing : EnumFacing.values())
			renderItem.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, stack);
		renderItem.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, stack);
		tessellator.draw();
	}

	@Override
	public ItemStack getStackToRender(EntityGhostSword entityIn)
	{
		ItemStack stack = new ItemStack(IafItemRegistry.ghost_sword);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("AsProjectile", true);
		stack.setTagCompound(tag);
		return stack;
	}
}