package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import net.ilexiconn.llibrary.client.util.ItemTESRContext;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class RenderJar extends TileEntitySpecialRenderer<TileEntityJar> {

    private static final ModelPixie MODEL_PIXIE = new ModelPixie();
    private static final ResourceLocation SLIME_TEXTURE = new ResourceLocation("textures/entity/slime/slime.png");
    private static final ResourceLocation MAGMA_CUBE_TEXTURE = new ResourceLocation("textures/entity/slime/magmacube.png");
    private static final ModelMagmaCube MAGMA_CUBE_MODEL = new ModelMagmaCube();
    private static final ModelSlime SLIME_INNER = new ModelSlime(16);
    private static final ModelSlime SLIME_OUTER = new ModelSlime(0);

    @Override
    public void render(TileEntityJar entity, double x, double y, double z, float f, int f1, float alpha) {
        int meta = 0;
        boolean hasPixie = false;

        if (entity != null && entity.getWorld() != null) {
            meta = entity.pixieType;
            hasPixie = entity.hasPixie;
        } else if (ItemTESRContext.INSTANCE.getCurrentStack() != null) {
            hasPixie = ItemTESRContext.INSTANCE.getCurrentStack().getItemDamage() != 0;
            meta = ItemTESRContext.INSTANCE.getCurrentStack().getItemDamage() - 1;
        }
        if (entity != null && entity.getWorld() != null && entity.hasSlime) {
            renderSlime(entity, x, y, z, f);
            return;
        }
        if (hasPixie) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.501F, (float) z + 0.5F);
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glPushMatrix();
            switch (meta) {
                default:
                    this.bindTexture(RenderPixie.TEXTURE_0);
                    break;
                case 1:
                    this.bindTexture(RenderPixie.TEXTURE_1);
                    break;
                case 2:
                    this.bindTexture(RenderPixie.TEXTURE_2);
                    break;
                case 3:
                    this.bindTexture(RenderPixie.TEXTURE_3);
                    break;
                case 4:
                    this.bindTexture(RenderPixie.TEXTURE_4);
                    break;
                case 5:
                    this.bindTexture(RenderPixie.TEXTURE_5);
                    break;
            }
            if (entity != null && entity.getWorld() != null) {

                GL11.glTranslatef(0F, 0.9625F, 0F);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GlStateManager.rotate(this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, f), 0.0F, 1.0F, 0.0F);
                GL11.glScalef(0.50F, 0.50F, 0.50F);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0F);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                MODEL_PIXIE.animateInJar(true, entity, f);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void renderSlime(TileEntityJar entity, double x, double y, double z, float partialTicks) {
        float age = entity.ticksExisted + partialTicks;
        // Hop roughly once a second: |sin| gives a bounce that touches the floor between hops.
        float hop = MathHelper.abs(MathHelper.sin(age * 0.15F));
        float bounce = hop * 0.3F;
        float squash = 1.0F - hop;
        squash = squash * squash * squash * squash;
        float scaleY = 0.7F * (1.0F - 0.3F * squash + 0.1F * hop);
        float scaleXZ = 0.7F * (1.0F + 0.2F * squash);

        this.bindTexture(entity.magmaCube ? MAGMA_CUBE_TEXTURE : SLIME_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.501F, (float) z + 0.5F);
        GlStateManager.rotate(180, 1, 0, 0);
        // Model space is upside down here; anchor the slime's feet 0.1 blocks above the jar floor.
        GlStateManager.translate(0F, 1.401F - bounce, 0F);
        GlStateManager.rotate(this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(scaleXZ, scaleY, scaleXZ);
        GlStateManager.translate(0F, -1.5F, 0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (entity.magmaCube) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0F);
            MAGMA_CUBE_MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);
            GlStateManager.popMatrix();
            return;
        }
        SLIME_INNER.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
        SLIME_OUTER.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f;

        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }


}
