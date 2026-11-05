package com.github.alexthe666.iceandfire.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

/** Vanilla salmon geometry and UV layout adapted to the 1.12 model renderer. */
final class FishModel extends ModelBase {
    private static final float SCALE = 0.043F;
    private final ModelRenderer front;
    private final ModelRenderer rear;
    private final ModelRenderer head;
    private final ModelRenderer leftFin;
    private final ModelRenderer rightFin;

    FishModel() {
        textureWidth = 32;
        textureHeight = 32;

        front = part(0, 0, 0, 0, -7.2F);
        front.addBox(-1.5F, -2.5F, 0, 3, 5, 8);
        ModelRenderer frontDorsal = part(2, 1, 0, -4.5F, 5);
        frontDorsal.addBox(0, 0, 0, 0, 2, 3);
        front.addChild(frontDorsal);

        rear = part(0, 13, 0, 0, 0.8F);
        rear.addBox(-1.5F, -2.5F, 0, 3, 5, 8);
        ModelRenderer rearDorsal = part(0, 2, 0, -4.5F, -1);
        rearDorsal.addBox(0, 0, 0, 0, 2, 4);
        rear.addChild(rearDorsal);
        ModelRenderer tail = part(20, 10, 0, 0, 8);
        tail.addBox(0, -2.5F, 0, 0, 5, 6);
        rear.addChild(tail);

        head = part(22, 0, 0, 0, -7.2F);
        head.addBox(-1, -2, -3, 2, 4, 3);

        rightFin = part(-4, 0, -1.5F, 1.5F, -7.2F);
        rightFin.addBox(-2, 0, 0, 2, 0, 2);
        rightFin.rotateAngleZ = (float) (-Math.PI / 4);
        leftFin = part(0, 0, 1.5F, 1.5F, -7.2F);
        leftFin.addBox(0, 0, 0, 2, 0, 2);
        leftFin.rotateAngleZ = (float) (Math.PI / 4);
    }

    private ModelRenderer part(int u, int v, float x, float y, float z) {
        ModelRenderer part = new ModelRenderer(this, u, v);
        part.setRotationPoint(x, y, z);
        return part;
    }

    void draw(double time) {
        rear.rotateAngleY = (float) (Math.sin(time * 0.6D) * 0.25D);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0, Math.sin(time * 0.22D) * 0.012D, 0);
            GlStateManager.rotate(180, 1, 0, 0);
            front.render(SCALE);
            rear.render(SCALE);
            head.render(SCALE);
            leftFin.render(SCALE);
            rightFin.render(SCALE);
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
