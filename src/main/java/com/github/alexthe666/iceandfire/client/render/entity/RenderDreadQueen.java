package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDreadQueen;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderDreadQueen extends RenderLiving<EntityDreadQueen> {
    public static final ResourceLocation TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/dread/dread_knight_eyes.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/dread/dread_queen.png");
    public static final ResourceLocation TEXTURE_INACTIVE = new ResourceLocation("iceandfire:textures/models/dread/dread_queen_inactive.png");
    public static final ResourceLocation TEXTURE_INJURED = new ResourceLocation("iceandfire:textures/models/dread/dread_queen_injured.png");

    public RenderDreadQueen(RenderManager renderManager) {
        super(renderManager, new ModelDreadQueen(0.0F, false), 0.6F);
        this.addLayer(new LayerGenericGlowing<EntityDreadQueen>(this, TEXTURE_EYES) {
            @Override
            public void doRenderLayer(EntityDreadQueen queen, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (queen.isAwaken())
                    super.doRenderLayer(queen, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
        });
        this.addLayer(new LayerHeldItem(this) {
            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelDreadQueen) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, p_191361_1_);
                if (p_191361_1_ == EnumHandSide.LEFT) {
                    GL11.glTranslatef(-0.05F, 0, 0);
                } else {
                    GL11.glTranslatef(0.05F, 0, 0);
                }
            }
        });
    }

    @Override
    protected void preRenderCallback(EntityDreadQueen entity, float f) {
        GL11.glScalef(0.95F, 0.95F, 0.95F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityDreadQueen entity) {
        return entity.isAwaken() ? (entity.getHealth()<=entity.getMaxHealth()*0.2f?TEXTURE_INJURED:TEXTURE) : TEXTURE_INACTIVE;
    }
}
