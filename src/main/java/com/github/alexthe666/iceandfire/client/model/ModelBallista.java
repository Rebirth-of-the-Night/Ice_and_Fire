package com.github.alexthe666.iceandfire.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBallista extends ModelBase {
	private final ModelRenderer STANDING_BASE;
	private final ModelRenderer BASE_SIDE4_r1;
	private final ModelRenderer BASE_SIDE3_r1;
	private final ModelRenderer BASE_SIDE2_r1;
	private final ModelRenderer BALLISTAEBASE1;
	private final ModelRenderer MAINBALLISTAE;
	private final ModelRenderer MAIMBALLISTAE17_r1;
	private final ModelRenderer MAIMBALLISTAE5_r1;
	private final ModelRenderer MAIMBALLISTAE4_r1;
	private final ModelRenderer skull;
	private final ModelRenderer MAIMBALLISTAE43_1_r1;
	private final ModelRenderer handle;

	public ModelBallista() {
		textureWidth = 256;
		textureHeight = 256;

		STANDING_BASE = new ModelRenderer(this);
		STANDING_BASE.setRotationPoint(0.0F, 24.0F, 0.0F);
		STANDING_BASE.cubeList.add(new ModelBox(STANDING_BASE, 0, 183, -10.0F, -6.0F, -17.0F, 21, 6, 6, 0.0F, false));
		STANDING_BASE.cubeList.add(new ModelBox(STANDING_BASE, 27, 173, -10.0F, -6.0F, -11.0F, 21, 6, 22, 0.0F, false));

		BASE_SIDE4_r1 = new ModelRenderer(this);
		BASE_SIDE4_r1.setRotationPoint(-24.0F, 0.0F, 10.0F);
		STANDING_BASE.addChild(BASE_SIDE4_r1);
		setRotationAngle(BASE_SIDE4_r1, 0.0F, -1.5708F, 0.0F);
		BASE_SIDE4_r1.cubeList.add(new ModelBox(BASE_SIDE4_r1, 0, 183, -21.0F, -6.0F, -41.0F, 22, 6, 6, 0.0F, false));

		BASE_SIDE3_r1 = new ModelRenderer(this);
		BASE_SIDE3_r1.setRotationPoint(-9.0F, 0.0F, -24.0F);
		STANDING_BASE.addChild(BASE_SIDE3_r1);
		setRotationAngle(BASE_SIDE3_r1, -3.1416F, 0.0F, 3.1416F);
		BASE_SIDE3_r1.cubeList.add(new ModelBox(BASE_SIDE3_r1, 0, 183, -20.0F, -6.0F, -41.0F, 21, 6, 6, 0.0F, false));

		BASE_SIDE2_r1 = new ModelRenderer(this);
		BASE_SIDE2_r1.setRotationPoint(25.0F, 0.0F, -10.0F);
		STANDING_BASE.addChild(BASE_SIDE2_r1);
		setRotationAngle(BASE_SIDE2_r1, 0.0F, 1.5708F, 0.0F);
		BASE_SIDE2_r1.cubeList.add(new ModelBox(BASE_SIDE2_r1, 0, 183, -21.0F, -6.0F, -41.0F, 22, 6, 6, 0.0F, false));

		BALLISTAEBASE1 = new ModelRenderer(this);
		BALLISTAEBASE1.setRotationPoint(0.0F, 24.0F, 0.0F);
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 26, 173, -10.0F, -8.0F, -11.0F, 22, 2, 22, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 165, -9.0F, -26.0F, -6.0F, 4, 18, 12, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 165, 6.0F, -26.0F, -6.0F, 5, 18, 12, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 157, -11.0F, -24.0F, -2.0F, 24, 4, 4, 0.0F, false));

		MAINBALLISTAE = new ModelRenderer(this);
		MAINBALLISTAE.setRotationPoint(0.0F, 2.0887F, 0.0015F);
		setRotationAngle(MAINBALLISTAE, 0.0F, 3.1416F, 0.0F);
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -4.0F, -5.5F, -16.25F, 10, 9, 52, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 68, -21.0F, -12.5F, -22.25F, 6, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 69, 17.0F, -12.5F, -22.25F, 6, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -4.0F, -12.5F, -22.25F, 2, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 70, 33, 4.0F, -12.5F, -22.25F, 2, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 22, 69, -2.0F, -5.5F, -28.25F, 6, 8, 12, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -2.0F, -12.5F, -22.25F, 6, 2, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 77, 6.0F, -12.5F, -22.25F, 11, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 76, -15.0F, -12.5F, -22.25F, 11, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 76, -15.0F, -4.5F, -22.25F, 11, 8, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 77, 6.0F, -4.5F, -22.25F, 11, 8, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, 9.0F, -8.5F, -22.25F, 3, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -10.0F, -8.5F, -22.25F, 3, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 0, -28.0F, -19.5F, -24.25F, 26, 29, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 0, 4.0F, -19.5F, -24.25F, 26, 29, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 56, 0, -2.0F, 2.5F, -24.25F, 6, 7, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, -1.0F, -8.5F, -4.25F, 4, 3, 4, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -4.0F, 3.5F, -7.25F, 10, 3, 15, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, -5.0F, -6.5F, -6.25F, 1, 11, 21, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 68, -18.0F, -0.5F, -1.25F, 3, 3, 13, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 77, -18.0F, -0.5F, 11.75F, 13, 3, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 68, -7.0F, -7.5F, 35.75F, 16, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 146, 101, 6.0F, -4.5F, 10.75F, 8, 7, 15, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 144, 64, 3.5F, -6.5F, -16.25F, 2, 1, 36, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 153, 112, 5.5F, -6.5F, 17.75F, 2, 2, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 174, 92, 3.5F, -13.5F, -16.25F, 2, 7, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 174, 94, 3.5F, -13.5F, -22.25F, 2, 1, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 19, 47, 2.5F, -19.5F, -29.25F, 2, 14, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 19, 47, -2.5F, -19.5F, -29.25F, 2, 14, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 2, 32, -2.5F, -5.5F, -29.25F, 7, 2, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 1, 47, -0.5F, -19.5F, -29.25F, 3, 9, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 70, -2.0F, -23.0887F, -16.0015F, 6, 14, 44, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 35, 53, 4.0F, -20.0887F, 17.9985F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 35, 53, 4.0F, -20.0887F, 3.9985F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 35, 53, 4.0F, -20.0887F, -10.0015F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, 2.0F, -24.0887F, -15.0015F, 5, 1, 42, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, 4.0F, -10.0887F, -15.0015F, 5, 1, 42, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 67, 4.0F, -23.0887F, 26.9985F, 4, 14, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, 4.0F, -9.0887F, 22.9985F, 4, 4, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, -5.0F, -13.0887F, 22.9985F, 4, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, -6.0F, -13.0887F, 5.9985F, 5, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, 4.0F, -9.0887F, 5.9985F, 4, 4, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, -5.0F, -13.0887F, -12.0015F, 4, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 43, 56, 4.0F, -9.0887F, -12.0015F, 4, 4, 3, 0.0F, true));

		MAIMBALLISTAE17_r1 = new ModelRenderer(this);
		MAIMBALLISTAE17_r1.setRotationPoint(-15.0F, -23.7426F, -18.5931F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE17_r1);
		setRotationAngle(MAIMBALLISTAE17_r1, -0.7854F, 0.0F, 0.0F);
		MAIMBALLISTAE17_r1.cubeList.add(new ModelBox(MAIMBALLISTAE17_r1, 0, 31, -12.99F, -7.0F, -1.0F, 58, 14, 2, 0.0F, false));

		MAIMBALLISTAE5_r1 = new ModelRenderer(this);
		MAIMBALLISTAE5_r1.setRotationPoint(-15.5F, -4.5F, -17.75F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE5_r1);
		setRotationAngle(MAIMBALLISTAE5_r1, 0.0F, 0.5672F, 0.0F);
		MAIMBALLISTAE5_r1.cubeList.add(new ModelBox(MAIMBALLISTAE5_r1, 0, 67, -18.5F, -4.0F, -2.5F, 23, 4, 5, 0.0F, false));

		MAIMBALLISTAE4_r1 = new ModelRenderer(this);
		MAIMBALLISTAE4_r1.setRotationPoint(16.5F, -4.5F, -16.75F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE4_r1);
		setRotationAngle(MAIMBALLISTAE4_r1, 0.0F, -0.5672F, 0.0F);
		MAIMBALLISTAE4_r1.cubeList.add(new ModelBox(MAIMBALLISTAE4_r1, 0, 68, -4.5F, -4.0F, -2.5F, 23, 4, 5, 0.0F, false));

		skull = new ModelRenderer(this);
		skull.setRotationPoint(0.0F, 0.0F, 0.0F);
		MAINBALLISTAE.addChild(skull);
		skull.cubeList.add(new ModelBox(skull, 120, 23, -3.0F, -19.0887F, -37.25F, 8, 6, 8, 0.0F, false));

		MAIMBALLISTAE43_1_r1 = new ModelRenderer(this);
		MAIMBALLISTAE43_1_r1.setRotationPoint(1.0F, -5.215F, 3.5584F);
		skull.addChild(MAIMBALLISTAE43_1_r1);
		setRotationAngle(MAIMBALLISTAE43_1_r1, 0.1309F, 0.0F, 0.0F);
		MAIMBALLISTAE43_1_r1.cubeList.add(new ModelBox(MAIMBALLISTAE43_1_r1, 120, 37, -4.0F, -12.0887F, -39.5F, 8, 2, 8, 0.0F, false));

		handle = new ModelRenderer(this);
		handle.setRotationPoint(0.0F, 0.0F, 0.0F);
		MAINBALLISTAE.addChild(handle);
		handle.cubeList.add(new ModelBox(handle, 40, 56, 6.0F, -4.0887F, 27.9985F, 2, 4, 6, 0.0F, false));
		handle.cubeList.add(new ModelBox(handle, 59, 63, -7.0F, -3.0887F, 29.9985F, 16, 2, 2, 0.0F, false));
		handle.cubeList.add(new ModelBox(handle, 40, 56, -6.0F, -4.0887F, 27.9985F, 2, 4, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		STANDING_BASE.render(f5);
		BALLISTAEBASE1.render(f5);
		MAINBALLISTAE.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float headpitch, float headyaw, float p_78087_6_, Entity entity) {
		this.MAINBALLISTAE.rotateAngleY = headpitch / 57.295776F;
		this.MAINBALLISTAE.rotateAngleX = headyaw / 57.295776F;

	}
}