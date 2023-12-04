package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleGhostAppearance extends Particle {
    private final ModelGhost model = new ModelGhost(0.0F);
    private EntityLivingBase entity;
    private boolean fromLeft = false;

    public ParticleGhostAppearance(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        if (this.entity == null)
        {
            this.entity = new EntityGhost(this.world);
        }
        fromLeft = worldIn.rand.nextBoolean();
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        if (this.entity != null) {
            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
            float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
            float f1 = 0.05F + 0.5F * MathHelper.sin(f * (float) Math.PI);
            EntityGhost ghostEntity = (EntityGhost) entity;
            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (fromLeft) {
                GlStateManager.rotate(150.0F * f - 60.0F + entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(150.0F * f - 60.0F + entityIn.rotationPitch, 0.0F, 0.0F, 1.0F);
            } else {
                GlStateManager.rotate(150.0F * f - 60.0F + entityIn.rotationYaw, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(150.0F * f - 60.0F + entityIn.rotationPitch, 0.0F, 0.0F, -1.0F);
            }
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            GlStateManager.translate(0.0D, 0.3F, 1.25D);

            this.model.animate(ghostEntity, 0, 0, entity.ticksExisted + partialTicks, 0, 0, 0);
            rendermanager.renderEntity(entity, 1.0F, 1.0F, 1.0F, f1, partialTicks, false);
            GlStateManager.popMatrix();
            GlStateManager.enableDepth();
            //this.model.render(matrixstack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f1);
        }
    }
}