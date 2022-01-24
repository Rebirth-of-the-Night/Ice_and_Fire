package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.alexthe666.iceandfire.client.particle.LightningBoltData;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;

/*
 * Lightning bolt effect code is dragged from the 1.16 source code of IaF.
 * Original code is from Mekanism and belongs to Aidan C. Brady and PupNewfster. 
 * Mekanism is owned by Aidan C. Brady.
 */
@SideOnly(Side.CLIENT)
public class RenderLightningDragon extends RenderDragonBase {

    private final LightningRender lightningRender = new LightningRender();

    public RenderLightningDragon(RenderManager renderManager, ModelBase model, int dragonType) {
        super(renderManager, model, dragonType);
    }

    public boolean shouldRender(EntityDragonBase dragon, ICamera camera, double camX, double camY, double camZ) {
        if (super.shouldRender(dragon, camera, camX, camY, camZ)) {
            return true;
        } else {
            EntityLightningDragon lightningDragon = (EntityLightningDragon)dragon;
            if (lightningDragon.hasLightningTarget()) {
                Vec3d headPos = lightningDragon.getHeadPosition();
                Vec3d vec3d = new Vec3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                return camera.isBoundingBoxInFrustum(new AxisAlignedBB(headPos.x, headPos.y, headPos.z, vec3d.x, vec3d.y, vec3d.z));
            }
            return false;
        }
    }
    
    public void doRender(EntityDragonBase dragon, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(dragon, x, y, z, entityYaw, partialTicks);
        EntityLightningDragon lightningDragon = (EntityLightningDragon)dragon;
        GlStateManager.pushMatrix();
        if (lightningDragon.hasLightningTarget()) {
            double dist = Minecraft.getMinecraft().player.getDistance(lightningDragon);
            if(dist <= Math.max(256, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16F)) {
                Vec3d headPos = lightningDragon.getHeadPosition();
                Vec3d vec3d = new Vec3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                float energyScale = 0.4F * lightningDragon.getRenderSize();
                LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, headPos, vec3d, 15).size(0.05F * getBoundedScale(energyScale, 0.5F, 2)).lifespan(4).spawn(LightningBoltData.SpawnFunction.NO_DELAY);
                lightningRender.onUpdate(null, bolt, partialTicks);
                GlStateManager.translate(-lightningDragon.posX, -lightningDragon.posY, -lightningDragon.posZ);
                lightningRender.doRender(partialTicks);
            }
        }
        GlStateManager.popMatrix();
    }
    
    private static float getBoundedScale(float scale, float min, float max) {
        return min + scale * (max - min);
    }
}