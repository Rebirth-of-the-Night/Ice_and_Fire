package com.github.alexthe666.iceandfire.client.model.animator;

import java.util.List;

import com.github.alexthe666.iceandfire.client.model.util.EnumDragonPoses;
import com.github.alexthe666.iceandfire.client.model.util.IIceAndFireTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.client.model.util.LegArticulator;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

/*
 * Code directly taken and edited from 1.16 IaF, I own no rights over it.
 */
public abstract class DragonTabulaModelAnimator<T extends EntityDragonBase> extends IceAndFireTabulaModelAnimator implements IIceAndFireTabulaModelAnimator<T> {

	protected List<IceAndFireTabulaModel<? extends EntityDragonBase>> walkPoses;
    protected List<IceAndFireTabulaModel<? extends EntityDragonBase>> flyPoses;
    protected List<IceAndFireTabulaModel<? extends EntityDragonBase>> swimPoses;
    
    protected AdvancedModelRenderer[] neckParts;
    protected AdvancedModelRenderer[] tailParts;
    protected AdvancedModelRenderer[] tailPartsWBody;
    protected AdvancedModelRenderer[] toesPartsL;
    protected AdvancedModelRenderer[] toesPartsR;
    protected AdvancedModelRenderer[] clawL;
    protected AdvancedModelRenderer[] clawR;

    public DragonTabulaModelAnimator(IceAndFireTabulaModel baseModel) {
        super(baseModel);
    }

    public void init(IceAndFireTabulaModel model) {
        neckParts = new AdvancedModelRenderer[]{model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Neck3"), model.getCube("Head")};
        tailParts = new AdvancedModelRenderer[]{model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        tailPartsWBody = new AdvancedModelRenderer[]{model.getCube("BodyLower"), model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        toesPartsL = new AdvancedModelRenderer[]{model.getCube("ToeL1"), model.getCube("ToeL2"), model.getCube("ToeL3")};
        toesPartsR = new AdvancedModelRenderer[]{model.getCube("ToeR1"), model.getCube("ToeR2"), model.getCube("ToeR3")};
        clawL = new AdvancedModelRenderer[]{model.getCube("ClawL")};
        clawR = new AdvancedModelRenderer[]{model.getCube("ClawR")};
    }

    @Override
    public void setRotationAngles(IceAndFireTabulaModel<T> model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        if (neckParts == null) {
            init(model);
        }
        animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);

        boolean walking = !entity.isHovering() && !entity.isFlying() && entity.hoverProgress <= 0 && entity.flyProgress <= 0;
        boolean swimming = entity.isInWater() && entity.swimProgress > 0;

        int currentIndex = walking ? (entity.walkCycle / 10) : (entity.flightCycle / 10);
        if (swimming) {
            currentIndex = entity.swimCycle / 10;
        }
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = swimming ? 4 : walking ? 3 : 5;
        }

        IceAndFireTabulaModel currentPosition = swimming ? swimPoses.get(currentIndex) : walking ? walkPoses.get(currentIndex) : flyPoses.get(currentIndex);
        IceAndFireTabulaModel prevPosition = swimming ? swimPoses.get(prevIndex) : walking ? walkPoses.get(prevIndex) : flyPoses.get(prevIndex);
        float delta = ((walking ? entity.walkCycle : entity.flightCycle) / 10.0F) % 1.0F;
        if (swimming) {
            delta = ((entity.swimCycle) / 10.0F) % 1.0F;
        }
        
        float deltaTicks = delta + (LLibrary.PROXY.getPartialTicks() / 10.0F);
        if (delta == 0) {
            deltaTicks = 0;
        }

        for (AdvancedModelRenderer cube : model.getCubes().values()) {
            setRotationsLoop(model, entity, limbSwingAmount, walking, currentPosition, prevPosition, deltaTicks, cube);
        }

        float speed_walk = 0.2F;
        float speed_idle = entity.isSleeping() ? 0.025F : 0.05F;
        float speed_fly = 0.2F;
        float degree_walk = 0.5F;
        float degree_idle = entity.isSleeping() ? 0.25F : 0.5F;
        float degree_fly = 0.5F;
        if (!entity.isAIDisabled()) {
            if (!walking) {
                model.bob(model.getCube("BodyUpper"), -speed_fly, degree_fly * 5, false, ageInTicks, 1);
                model.walk(model.getCube("BodyUpper"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
                model.chainWave(tailPartsWBody, speed_fly, degree_fly * -0.1F, 0, ageInTicks, 1);
                model.chainWave(neckParts, speed_fly, degree_fly * 0.2F, -4, ageInTicks, 1);
                model.chainWave(toesPartsL, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
                model.chainWave(toesPartsR, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
                model.walk(model.getCube("ThighR"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
                model.walk(model.getCube("ThighL"), -speed_fly, degree_fly * 0.1F, true, 0, 0, ageInTicks, 1);
            } else {
                model.bob(model.getCube("BodyUpper"), speed_walk * 2, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.bob(model.getCube("ThighR"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.bob(model.getCube("ThighL"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.chainSwing(tailParts, speed_walk, degree_walk * 0.25F, -2, limbSwing, limbSwingAmount);
                model.chainWave(tailParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
                model.chainSwing(neckParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
                model.chainWave(neckParts, speed_walk, degree_walk * 0.05F, -2, limbSwing, limbSwingAmount);
                model.chainSwing(tailParts, speed_idle, degree_idle * 0.25F, -2, ageInTicks, 1);
                model.chainWave(tailParts, speed_idle, degree_idle * 0.15F, -2, ageInTicks, 1);
                model.chainWave(neckParts, speed_idle, degree_idle * -0.15F, -3, ageInTicks, 1);
                model.walk(model.getCube("Neck1"), speed_idle, degree_idle * 0.05F, false, 0, 0, ageInTicks, 1);
            }
            model.bob(model.getCube("BodyUpper"), speed_idle, degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("ThighR"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("ThighL"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("armR1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("armL1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            if (entity.getAnimation() != EntityDragonBase.ANIMATION_SHAKEPREY || entity.getAnimation() != EntityDragonBase.ANIMATION_ROAR) {
                model.faceTarget(rotationYaw, rotationPitch, 4, neckParts);
            }
            if (entity.isActuallyBreathingFire()) {
                float speed_shake = 0.7F;
                float degree_shake = 0.1F;
                model.chainFlap(neckParts, speed_shake, degree_shake, 2, ageInTicks, 1);
                model.chainSwing(neckParts, speed_shake * 0.65F, degree_shake * 0.1F, 1, ageInTicks, 1);
            }
        }
        if (!entity.isModelDead()) {
            if (entity.turn_buffer != null && !(entity.isBeingRidden() && !entity.isRiding() && entity.isBreathingFire())) {
                entity.turn_buffer.applyChainSwingBuffer(neckParts);
            }
            if (entity.tail_buffer != null && !entity.isRiding()) {
                entity.tail_buffer.applyChainSwingBuffer(tailPartsWBody);
            }
            if (entity.roll_buffer != null && entity.pitch_buffer_body != null && entity.pitch_buffer != null) {
                if (entity.flyProgress > 0 || entity.hoverProgress > 0) {
                    entity.roll_buffer.applyChainFlapBuffer(model.getCube("BodyUpper"));
                    entity.pitch_buffer_body.applyChainWaveBuffer(model.getCube("BodyUpper"));
                    entity.pitch_buffer.applyChainWaveBufferReverse(tailPartsWBody);
                }
            }
        }
        if (entity.width >= 2 && entity.flyProgress == 0 && entity.hoverProgress == 0) {
            LegArticulator.articulateQuadruped(entity, entity.legSolver, model.getCube("BodyUpper"), model.getCube("BodyLower"), model.getCube("Neck1"),
                    model.getCube("ThighL"), model.getCube("LegL"), toesPartsL,
                    model.getCube("ThighR"), model.getCube("LegR"), toesPartsR,
                    model.getCube("armL1"), model.getCube("armL2"), clawL,
                    model.getCube("armR1"), model.getCube("armR2"), clawR,
                    1.0F, 0.5F, 0.5F, -0.15F, -0.15F, 0F,
                    Minecraft.getMinecraft().getRenderPartialTicks()
                    );
                }
            }

    private void setRotationsLoop(IceAndFireTabulaModel<T> model, T entity, float limbSwingAmount, boolean walking, IceAndFireTabulaModel<T> currentPosition, IceAndFireTabulaModel prevPosition, float deltaTicks, AdvancedModelRenderer cube) {
        this.genderMob(entity, cube);
        if (walking && entity.flyProgress <= 0.0F && entity.hoverProgress <= 0.0F && entity.modelDeadProgress <= 0.0F) {
            AdvancedModelRenderer walkPart = getModel(EnumDragonPoses.GROUND_POSE).getCube(cube.boxName);
            AdvancedModelRenderer prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelRenderer currPositionCube = currentPosition.getCube(cube.boxName);

            if (prevPositionCube == null | currPositionCube == null)
                return;

            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            if (isHorn(cube) || isWing(model, cube) && (entity.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST || entity.getAnimation() == EntityDragonBase.ANIMATION_EPIC_ROAR)) {
                this.addToRotateAngle(cube, limbSwingAmount, walkPart.rotateAngleX, walkPart.rotateAngleY, walkPart.rotateAngleZ);
            } else {
                this.addToRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * distance(prevX, x), prevY + deltaTicks * distance(prevY, y), prevZ + deltaTicks * distance(prevZ, z));
            }
        }
        if (entity.modelDeadProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.DEAD).getCube(cube.boxName))) {
                transitionTo(cube, getModel(EnumDragonPoses.DEAD).getCube(cube.boxName), entity.modelDeadProgress, 20, cube.boxName.equals("ThighR") || cube.boxName.equals("ThighL"));
            }
        }
        if (entity.sleepProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.SLEEPING_POSE).getCube(cube.boxName))) {
                transitionTo(cube, getModel(EnumDragonPoses.SLEEPING_POSE).getCube(cube.boxName), entity.sleepProgress, 20, false);
            }
        }
        if (entity.hoverProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.HOVERING_POSE).getCube(cube.boxName)) && !isWing(model, cube) && !cube.boxName.contains("Tail")) {
                transitionTo(cube, getModel(EnumDragonPoses.HOVERING_POSE).getCube(cube.boxName), entity.hoverProgress, 20, false);
            }
        }
        if (entity.flyProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName))) {
                transitionTo(cube, getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName), entity.flyProgress - entity.diveProgress * 2, 20, false);
            }
        }
        if (entity.sitProgress > 0.0F) {
            if (!entity.isRiding()) {
                if (!isPartEqual(cube, getModel(EnumDragonPoses.SITTING_POSE).getCube(cube.boxName))) {
                    transitionTo(cube, getModel(EnumDragonPoses.SITTING_POSE).getCube(cube.boxName), entity.sitProgress, 20, false);
                }
            }
        }
        if (entity.ridingProgress > 0.0F) {
            if (!isHorn(cube) && getModel(EnumDragonPoses.SIT_ON_PLAYER_POSE).getCube(cube.boxName) != null && !isPartEqual(cube, getModel(EnumDragonPoses.SIT_ON_PLAYER_POSE).getCube(cube.boxName))) {
                transitionTo(cube, getModel(EnumDragonPoses.SIT_ON_PLAYER_POSE).getCube(cube.boxName), entity.ridingProgress, 20, false);
                if (cube.boxName.equals("BodyUpper")) {
                	cube.rotationPointZ += ((-12F - cube.rotationPointZ) / 20) * entity.ridingProgress;
                }
            }
        }
        if (entity.tackleProgress > 0.0F) {
            if (!isPartEqual(getModel(EnumDragonPoses.TACKLE).getCube(cube.boxName), getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName)) && !isWing(model, cube)) {
                transitionTo(cube, getModel(EnumDragonPoses.TACKLE).getCube(cube.boxName), entity.tackleProgress, 5, false);
            }
        }
        if (entity.diveProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.DIVING_POSE).getCube(cube.boxName))) {
                transitionTo(cube, getModel(EnumDragonPoses.DIVING_POSE).getCube(cube.boxName), entity.diveProgress, 10, false);
            }
        }
        if (entity.fireBreathProgress > 0.0F) {
            if (!isPartEqual(cube, getModel(EnumDragonPoses.STREAM_BREATH).getCube(cube.boxName)) && !isWing(model, cube) && !cube.boxName.contains("Finger")) {
                if (entity.prevFireBreathProgress <= entity.fireBreathProgress) {
                    transitionTo(cube, getModel(EnumDragonPoses.BLAST_CHARGE3).getCube(cube.boxName), MathHelper.clamp(entity.fireBreathProgress, 0, 5), 5, false);
                }
                transitionTo(cube, getModel(EnumDragonPoses.STREAM_BREATH).getCube(cube.boxName), MathHelper.clamp(entity.fireBreathProgress - 5, 0, 5), 5, false);
            }
        }
        if (!walking) {
            AdvancedModelRenderer flightPart = getModel(EnumDragonPoses.FLYING_POSE).getCube(cube.boxName);
            AdvancedModelRenderer prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelRenderer currPositionCube = currentPosition.getCube(cube.boxName);
            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            if (x != flightPart.rotateAngleX || y != flightPart.rotateAngleY || z != flightPart.rotateAngleZ) {
                this.setRotateAngle(cube, 1F, prevX + deltaTicks * distance(prevX, x), prevY + deltaTicks * distance(prevY, y), prevZ + deltaTicks * distance(prevZ, z));
            }
        }
    }

    protected boolean isWing(IceAndFireTabulaModel model, AdvancedModelRenderer modelRenderer) {
        return model.getCube("armL1") == modelRenderer || model.getCube("armR1") == modelRenderer || model.getCube("armL1").childModels.contains(modelRenderer) || model.getCube("armR1").childModels.contains(modelRenderer);
    }

    protected boolean isHorn(AdvancedModelRenderer modelRenderer) {
        return modelRenderer.boxName.contains("Horn");
    }

    protected void genderMob(T entity, AdvancedModelRenderer cube) {
        if (!entity.isMale()) {
        	IceAndFireTabulaModel maleModel = getModel(EnumDragonPoses.MALE);
        	IceAndFireTabulaModel femaleModel = getModel(EnumDragonPoses.FEMALE);
            AdvancedModelRenderer femaleModelCube = femaleModel.getCube(cube.boxName);
            AdvancedModelRenderer maleModelCube = maleModel.getCube(cube.boxName);
            if (maleModelCube == null || femaleModelCube == null)
                return;
            float x = femaleModelCube.rotateAngleX;
            float y = femaleModelCube.rotateAngleY;
            float z = femaleModelCube.rotateAngleZ;
            if (x != maleModelCube.rotateAngleX || y != maleModelCube.rotateAngleY || z != maleModelCube.rotateAngleZ) {
                this.setRotateAngle(cube, 1F, x, y, z);
            }
        }
    }

    protected abstract IceAndFireTabulaModel getModel(EnumDragonPoses pose);

    protected void animate(IceAndFireTabulaModel<T> model, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
    	AdvancedModelRenderer modelCubeJaw = model.getCube("Jaw");
        AdvancedModelRenderer modelCubeBodyUpper = model.getCube("BodyUpper");
        model.llibAnimator.update(entity);
        //Firecharge
        if (model.llibAnimator.setAnimation(T.ANIMATION_FIRECHARGE)) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.BLAST_CHARGE1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.BLAST_CHARGE2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.BLAST_CHARGE3));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(5);
        }
        //Speak
        if (model.llibAnimator.setAnimation(T.ANIMATION_SPEAK)) {
            model.llibAnimator.startKeyframe(5);
            this.rotate(model.llibAnimator, modelCubeJaw, 18, 0, 0);
            model.llibAnimator.move(modelCubeJaw, 0, 0, 0.2F);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.setStaticKeyframe(5);
            model.llibAnimator.startKeyframe(5);
            this.rotate(model.llibAnimator, modelCubeJaw, 18, 0, 0);
            model.llibAnimator.move(modelCubeJaw, 0, 0, 0.2F);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(5);
        }
        //Bite
        if (model.llibAnimator.setAnimation(T.ANIMATION_BITE)) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.BITE1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.BITE2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.BITE3));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
        //Shakeprey
        if (model.llibAnimator.setAnimation(T.ANIMATION_SHAKEPREY)) {
            model.llibAnimator.startKeyframe(15);
            moveToPose(model, getModel(EnumDragonPoses.GRAB1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.GRAB2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.GRAB_SHAKE1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.GRAB_SHAKE2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.GRAB_SHAKE3));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
        //Tailwhack
        if (model.llibAnimator.setAnimation(T.ANIMATION_TAILWHACK)) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.TAIL_WHIP1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.TAIL_WHIP2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.TAIL_WHIP3));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
        //Wingblast
        if (model.llibAnimator.setAnimation(T.ANIMATION_WINGBLAST)) {
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST3));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST4));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST3));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST4));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST5));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST6));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, getModel(EnumDragonPoses.WING_BLAST7));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -4F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
        //Roar
        if (model.llibAnimator.setAnimation(T.ANIMATION_ROAR)) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.ROAR1));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.ROAR2));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.ROAR3));
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
        //Epicroar
        if (model.llibAnimator.setAnimation(T.ANIMATION_EPIC_ROAR)) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.EPIC_ROAR1));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -6.8F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.EPIC_ROAR2));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -6.8F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.EPIC_ROAR3));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -6.8F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.EPIC_ROAR2));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -6.8F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, getModel(EnumDragonPoses.EPIC_ROAR3));
            model.llibAnimator.move(modelCubeBodyUpper, 0, -6.8F, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }
    }
}