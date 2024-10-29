package com.github.alexthe666.iceandfire.client.model;
// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBallistaArrow extends ModelBase {
	private final ModelRenderer head_main;

	public ModelBallistaArrow() {
		textureWidth = 64;
		textureHeight = 64;

		head_main = new ModelRenderer(this);
		head_main.setRotationPoint(0.0F, 26.0F, -1.0F);
		head_main.cubeList.add(new ModelBox(head_main, 0, 0, -8.0F, -19.0F, -7.0F, 16, 12, 16, 0.0F, false));

		ModelRenderer head_down = new ModelRenderer(this);
		head_down.setRotationPoint(-1.0F, -1.6934F, 1.4588F);
		head_main.addChild(head_down);
		setRotationAngle(head_down, 0.3927F, 0.0F, 0.0F);
		head_down.cubeList.add(new ModelBox(head_down, 0, 28, -7.0F, -2.0066F, -7.0588F, 16, 4, 16, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head_main.render(f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float headpitch, float headyaw, float p_78087_6_, Entity entity) {
		this.head_main.rotateAngleY = headpitch / 57.295776F;
		this.head_main.rotateAngleX = headyaw / 57.295776F;

	}
}