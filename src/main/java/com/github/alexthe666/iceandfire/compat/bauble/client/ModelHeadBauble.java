package com.github.alexthe666.iceandfire.compat.bauble.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelHeadBauble extends ModelBase {
	private final ModelRenderer bb_main;

	public ModelHeadBauble() {
		textureWidth = 64;
		textureHeight = 32;

		bb_main = new ModelRenderer(this, 0, 0);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.addBox(-4.0F, -32.0F, -4.0F, 8, 8, 8, 0.75F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
	}
}