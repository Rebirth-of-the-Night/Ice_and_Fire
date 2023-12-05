package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.entity.EntityBallistaArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

public class RenderIceCharge extends Render<EntityBallistaArrow>{

	public RenderIceCharge() {
		super(Minecraft.getMinecraft().getRenderManager());
	}

	@Override
	public void doRender(EntityBallistaArrow arrow, double a,
			double b, double c, float d,
			float e) {
		
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBallistaArrow a) {
		return null;
	}

}