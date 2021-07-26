package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.particle.LightningBoltData;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class RenderLightningDragon extends RenderDragonBase {

    private LightningRender lightningRender = new LightningRender();

    public RenderLightningDragon(RenderManager renderManager, ModelBase model, int dragonType) {
        super(renderManager, model, dragonType);
    }

    public boolean shouldRender(EntityDragonBase dragon, ICamera camera, double camX, double camY, double camZ) {
        if (super.shouldRender(dragon, camera, camX, camY, camZ)) {
            return true;
        } else {
            EntityLightningDragon lightningDragon = (EntityLightningDragon)dragon;
            if (lightningDragon.hasLightningTarget()) {
                Vec3d Vector3d1 = lightningDragon.getHeadPosition();
                Vec3d Vector3d = new Vec3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                return camera.isBoundingBoxInFrustum(new AxisAlignedBB(Vector3d1.x, Vector3d1.y, Vector3d1.z, Vector3d.x, Vector3d.y, Vector3d.z));
            }
            return false;
        }
    }
    /*
    public void doRender(EntityDragonBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        EntityLightningDragon lightningDragon = (EntityLightningDragon)entity;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        if (lightningDragon.hasLightningTarget()) {
            double dist = Minecraft.getMinecraft().player.getDistance(lightningDragon);
            if(dist <= Math.max(256, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16F)){
                Vec3d Vector3d1 = lightningDragon.getHeadPosition();
                Vec3d Vector3d = new Vec3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                float energyScale = 0.4F * lightningDragon.getRenderSize();
                LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, Vector3d1, Vector3d, 15);
                bolt.size(0.05F * getBoundedScale(energyScale, 0.5F, 2));
                bolt.lifespan(4);
                bolt.spawn(LightningBoltData.SpawnFunction.NO_DELAY);
                lightningRender.doUpdate(null, bolt, partialTicks);
                GlStateManager.translate(-lightningDragon.posX, -lightningDragon.posY, -lightningDragon.posZ);
                lightningRender.doRender(partialTicks, buffer);
            }
        }
        GlStateManager.popMatrix();
    }
    */
    private static float getBoundedScale(float scale, float min, float max) {
        return min + scale * (max - min);
    }
}