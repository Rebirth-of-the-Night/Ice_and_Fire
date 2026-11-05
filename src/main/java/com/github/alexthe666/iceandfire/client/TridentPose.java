package com.github.alexthe666.iceandfire.client;

import com.github.alexthe666.iceandfire.item.ItemTideTrident;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public final class TridentPose {
    private TridentPose() {}

    public static void apply(ModelBiped model, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;
        if (!living.isHandActive() || !(living.getActiveItemStack().getItem() instanceof ItemTideTrident)) return;
        EnumHandSide side = living.getPrimaryHand();
        if (living.getActiveHand() == EnumHand.OFF_HAND) side = side.opposite();
        ModelRenderer arm = side == EnumHandSide.RIGHT ? model.bipedRightArm : model.bipedLeftArm;
        // Vanilla 1.13 THROW_SPEAR pose; retain half of the walking/swing animation.
        arm.rotateAngleX = arm.rotateAngleX * 0.5F - (float) Math.PI;
        arm.rotateAngleY = 0.0F;
    }
}
