package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Draws the small electric arcs of lightning-struck leaves using the same bolt renderer as the lightning dragon.
 * Particles can't draw these bolts themselves, so they ask this class, which renders them after the world.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = IceAndFire.MODID, value = Side.CLIENT)
public class LeafLightningRenderer {
    private static final int MAX_ARCS_PER_TICK = 40;
    private static final LightningRender RENDER = new LightningRender();
    private static long lastArcTime = Long.MIN_VALUE;
    private static long countedTime = Long.MIN_VALUE;
    private static int arcsThisTick;

    public static void arc(Vec3d from, Vec3d to) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) {
            return;
        }
        long time = mc.world.getTotalWorldTime();
        if (time != countedTime) {
            countedTime = time;
            arcsThisTick = 0;
        }
        if (arcsThisTick++ >= MAX_ARCS_PER_TICK) {
            return;
        }
        lastArcTime = time;
        LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, from, to, 5)
                .size(0.025F).lifespan(3).spawn(LightningBoltData.SpawnFunction.NO_DELAY);
        RENDER.onUpdate(new Object(), bolt, 0F);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity view = mc.getRenderViewEntity();
        if (mc.world == null || view == null || mc.world.getTotalWorldTime() - lastArcTime > 120) {
            return;
        }
        float pt = event.getPartialTicks();
        double x = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        double y = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        double z = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-x, -y, -z);
        RENDER.doRender(pt);
        GlStateManager.popMatrix();
    }
}
