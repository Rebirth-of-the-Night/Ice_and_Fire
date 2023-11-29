package com.github.alexthe666.iceandfire.client.model;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

import net.ilexiconn.llibrary.client.model.ModelAnimator;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.Entity;

public class ModelBallista extends ModelDragonBase {
	private final AdvancedModelRenderer STANDING_BASE;
	private final AdvancedModelRenderer BALLISTAEBASE1;
	private final AdvancedModelRenderer MAINBALLISTAE;
	private final AdvancedModelRenderer MAIMBALLISTAE17_r1;
	private final AdvancedModelRenderer MAIMBALLISTAE5_r1;
	private final AdvancedModelRenderer MAIMBALLISTAE4_r1;
	private final ModelAnimator animator;
	public ModelBallista() {
		textureWidth = 128;
		textureHeight = 128;

		STANDING_BASE = new AdvancedModelRenderer(this);
		STANDING_BASE.setRotationPoint(0.0F, 24.0F, 0.0F);
		STANDING_BASE.cubeList.add(new ModelBox(STANDING_BASE, 0, 35, -15.0F, -6.0F, -11.0F, 32, 6, 22, 0.0F, false));
		STANDING_BASE.cubeList.add(new ModelBox(STANDING_BASE, 0, 16, -10.0F, -6.0F, -16.0F, 22, 6, 32, 0.0F, false));

		BALLISTAEBASE1 = new AdvancedModelRenderer(this);
		BALLISTAEBASE1.setRotationPoint(0.0F, 24.0F, 0.0F);
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 16, -10.0F, -8.0F, -11.0F, 22, 2, 22, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 12, -9.0F, -26.0F, -6.0F, 4, 18, 12, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 12, 6.0F, -26.0F, -6.0F, 5, 18, 12, 0.0F, false));
		BALLISTAEBASE1.cubeList.add(new ModelBox(BALLISTAEBASE1, 0, 3, -11.0F, -24.0F, -2.0F, 24, 4, 4, 0.0F, false));

		MAINBALLISTAE = new AdvancedModelRenderer(this);
		MAINBALLISTAE.setRotationPoint(8.0F, 2.0887F, 8.0015F);
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -12.0F, -5.5F, -24.25F, 10, 9, 52, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -29.0F, -12.5F, -30.25F, 6, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, 9.0F, -12.5F, -30.25F, 6, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -12.0F, -12.5F, -30.25F, 2, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -4.0F, -12.5F, -30.25F, 2, 16, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 12, -10.0F, -5.5F, -36.25F, 6, 8, 12, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -10.0F, -12.5F, -30.25F, 6, 2, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 5, -2.0F, -12.5F, -30.25F, 11, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 5, -23.0F, -12.5F, -30.25F, 11, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 5, -23.0F, -4.5F, -30.25F, 11, 8, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 5, -2.0F, -4.5F, -30.25F, 11, 8, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, 1.0F, -8.5F, -30.25F, 3, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -18.0F, -8.5F, -30.25F, 3, 4, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 114, 53, -36.0F, -19.5F, -32.25F, 26, 29, 2, 0.0F, true));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 114, 53, -4.0F, -19.5F, -32.25F, 26, 29, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 1, 1, -10.0F, 2.5F, -32.25F, 6, 7, 2, 0.0F, true));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 7, -2.0F, -3.5F, 18.75F, 2, 3, 7, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 2, -15.0F, -3.5F, 20.75F, 16, 3, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 7, -14.0F, -3.5F, 18.75F, 2, 3, 7, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 4, -9.0F, -8.5F, -12.25F, 4, 3, 4, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 15, -12.0F, 3.5F, -15.25F, 10, 3, 15, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -13.0F, -6.5F, -14.25F, 1, 11, 21, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 2, -26.0F, -0.5F, 3.75F, 13, 3, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 13, -26.0F, -0.5F, -9.25F, 3, 3, 13, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 2, -15.0F, -7.5F, 27.75F, 16, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 14, -2.0F, -4.5F, 2.75F, 8, 7, 15, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -4.5F, -6.5F, -24.25F, 2, 1, 36, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 2, -2.5F, -6.5F, 9.75F, 2, 2, 2, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 1, -4.5F, -13.5F, -24.25F, 2, 7, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -4.5F, -13.5F, -30.25F, 2, 1, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -5.5F, -19.5F, -37.25F, 2, 14, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -10.5F, -19.5F, -37.25F, 2, 14, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 1, -10.5F, -5.5F, -37.25F, 7, 2, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 6, -8.5F, -19.5F, -37.25F, 3, 9, 6, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 3, 7, -11.0F, -19.0887F, -45.25F, 8, 8, 8, 0.0F, true));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -10.0F, -23.0887F, -24.0015F, 6, 14, 44, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -20.0887F, 9.9985F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -20.0887F, -4.0015F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -20.0887F, -18.0015F, 1, 11, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -6.0F, -24.0887F, -23.0015F, 5, 1, 42, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 16, -4.0F, -10.0887F, -23.0015F, 5, 1, 42, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 1, -4.0F, -23.0887F, 18.9985F, 4, 14, 1, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -9.0887F, 14.9985F, 4, 4, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -13.0F, -13.0887F, 14.9985F, 4, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -14.0F, -13.0887F, -2.0015F, 5, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -9.0887F, -2.0015F, 4, 4, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -13.0F, -13.0887F, -20.0015F, 4, 8, 3, 0.0F, false));
		MAINBALLISTAE.cubeList.add(new ModelBox(MAINBALLISTAE, 0, 3, -4.0F, -9.0887F, -20.0015F, 4, 4, 3, 0.0F, false));

		MAIMBALLISTAE17_r1 = new AdvancedModelRenderer(this);
		MAIMBALLISTAE17_r1.setRotationPoint(-23.0F, -23.7426F, -26.5931F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE17_r1);
		setRotationAngle(MAIMBALLISTAE17_r1, -0.7854F, 0.0F, 0.0F);
		MAIMBALLISTAE17_r1.cubeList.add(new ModelBox(MAIMBALLISTAE17_r1, 8, 112, -12.99F, -7.0F, -1.0F, 58, 14, 2, 0.0F, false));

		MAIMBALLISTAE5_r1 = new AdvancedModelRenderer(this);
		MAIMBALLISTAE5_r1.setRotationPoint(-23.5F, -4.5F, -25.75F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE5_r1);
		setRotationAngle(MAIMBALLISTAE5_r1, 0.0F, 0.5672F, 0.0F);
		MAIMBALLISTAE5_r1.cubeList.add(new ModelBox(MAIMBALLISTAE5_r1, 0, 4, -18.5F, -4.0F, -2.5F, 23, 4, 5, 0.0F, false));

		MAIMBALLISTAE4_r1 = new AdvancedModelRenderer(this);
		MAIMBALLISTAE4_r1.setRotationPoint(8.5F, -4.5F, -24.75F);
		MAINBALLISTAE.addChild(MAIMBALLISTAE4_r1);
		setRotationAngle(MAIMBALLISTAE4_r1, 0.0F, -0.5672F, 0.0F);
		MAIMBALLISTAE4_r1.cubeList.add(new ModelBox(MAIMBALLISTAE4_r1, 0, 4, -4.5F, -4.0F, -2.5F, 23, 4, 5, 0.0F, false));
		animator = ModelAnimator.create();
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		STANDING_BASE.render(f5);
		BALLISTAEBASE1.render(f5);
		MAINBALLISTAE.render(f5);
	}

	public void setRotationAngle(AdvancedModelRenderer AdvancedModelRenderer, float x, float y, float z) {
		AdvancedModelRenderer.rotateAngleX = x;
		AdvancedModelRenderer.rotateAngleY = y;
		AdvancedModelRenderer.rotateAngleZ = z;
	}

	@Override
	public void renderStatue() {
		this.resetToDefaultPose();
	}
}