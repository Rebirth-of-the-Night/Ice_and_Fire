package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.entity.EntityCastleBallista;
import com.lycanitesmobs.client.obj.Model;
import com.lycanitesmobs.client.obj.ObjModel;
import com.lycanitesmobs.client.obj.TessellatorModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;


public class RenderCastleBallista extends RenderLiving<EntityCastleBallista>
{
    private ObjModel model;
    private static ResourceLocation TURMODEL;

    public static Model loadModel(ResourceLocation resourceLocation) {
        return new TessellatorModel(resourceLocation);
    }

    @Override
    public void doRender(EntityCastleBallista entity, double x, double y, double z, float entityYaw, float partialTicks) {
        bindTexture(getEntityTexture(entity));
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y, z);
        GlStateManager.pushMatrix();

        model.renderGroups("BASE1");
        model.renderGroups("BASE2");
        model.renderGroups("BASE3");
        model.renderGroups("BASE4");
        model.renderGroups("BASE5");
        model.renderGroups("BASE6");

        GlStateManager.popMatrix();
        GlStateManager.translate(0, 0, 0);
        GlStateManager.rotate(entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks,
                0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
                1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, 0, 0);
        for(int i = 0; i < 55; i++)
            model.renderGroups("MAIMBALLISTAE" + i);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    public RenderCastleBallista(RenderManager rm) {
        super(rm, null, 1.0f);
        model = (ObjModel) loadModel(RenderCastleBallista.TURMODEL);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCastleBallista entityCastleBallista) {
        return new ResourceLocation("iceandfire", "textures/models/ballista.png");
    }

    static {
        TURMODEL = new ResourceLocation("iceandfire", "models/obj/ballista.obj");
    }
}