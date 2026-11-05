package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.entity.EntityFishingSalmon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class FishingRippleRenderer extends Render<EntityFishingSalmon> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(IceAndFire.MODID, "textures/models/fish/salmon.png");
    private final FishModel fish = new FishModel();

    public FishingRippleRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFishingSalmon entity, double x, double y, double z, float entityYaw, float partialTicks) {
        bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        try {
            GlStateManager.translate(x, y, z);
            float yaw = entity.prevRotationYaw + MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.color(1, 1, 1, 1);
            fish.draw(entity.ticksExisted + partialTicks);
        } finally {
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFishingSalmon entity) {
        return TEXTURE;
    }
}
