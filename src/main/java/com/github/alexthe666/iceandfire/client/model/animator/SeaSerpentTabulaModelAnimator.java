package com.github.alexthe666.iceandfire.client.model.animator;

import java.util.Arrays;
import java.util.List;

import com.github.alexthe666.iceandfire.client.model.util.EnumSeaSerpentAnimations;
import com.github.alexthe666.iceandfire.client.model.util.IIceAndFireTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;

public class SeaSerpentTabulaModelAnimator extends IceAndFireTabulaModelAnimator implements IIceAndFireTabulaModelAnimator<EntitySeaSerpent> {

    public List<IceAndFireTabulaModel<EntitySeaSerpent>> swimPose = Arrays.asList(
            EnumSeaSerpentAnimations.SWIM1.seaserpent_model,
            EnumSeaSerpentAnimations.SWIM3.seaserpent_model,
            EnumSeaSerpentAnimations.SWIM4.seaserpent_model,
            EnumSeaSerpentAnimations.SWIM6.seaserpent_model );

    public SeaSerpentTabulaModelAnimator() {
        super(EnumSeaSerpentAnimations.T_POSE.seaserpent_model);
    }

    @Override
    public void init(IceAndFireTabulaModel<EntitySeaSerpent> model) {

    }

    @Override
    public void setRotationAngles(IceAndFireTabulaModel<EntitySeaSerpent> model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        model.getCube("BodyUpper").rotationPointY += 9;//model was made too high
        StoneEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, StoneEntityProperties.class);
        if (properties != null && properties.isStone) {
            return;
        }
        animate(model, entity);
        int currentIndex = entity.swimCycle / 10;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = 3;
        }
        IceAndFireTabulaModel<EntitySeaSerpent> prevPosition = swimPose.get(prevIndex);
        IceAndFireTabulaModel<EntitySeaSerpent> currentPosition = swimPose.get(currentIndex);
        float delta = ((entity.swimCycle) / 10.0F) % 1.0F + (LLibrary.PROXY.getPartialTicks() / 10.0F);
        AdvancedModelRenderer[] tailParts = {model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4"), model.getCube("Tail5"), model.getCube("Tail6")};
        AdvancedModelRenderer[] neckParts = {model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Head")};

        for (AdvancedModelRenderer cube : model.getCubes().values()) {
            if (entity.jumpProgress > 0.0F) {
                if (!isPartEqual(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName), entity.jumpProgress, 5, false);
                }
            }
            if (entity.wantJumpProgress > 0.0F) {
                if (!isPartEqual(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName), entity.wantJumpProgress, 10, false);
                }
            }
            float prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
            float prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
            float prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
            float x = currentPosition.getCube(cube.boxName).rotateAngleX;
            float y = currentPosition.getCube(cube.boxName).rotateAngleY;
            float z = currentPosition.getCube(cube.boxName).rotateAngleZ;
            this.addToRotateAngle(cube, limbSwingAmount, prevX + delta * distance(prevX, x), prevY + delta * distance(prevY, y), prevZ + delta * distance(prevZ, z));

        }
        if (entity.breathProgress > 0.0F) {
            progressRotation(model.getCube("Head"), entity.breathProgress, (float) Math.toRadians(-15F), 0, 0);
            progressRotation(model.getCube("HeadFront"), entity.breathProgress, (float) Math.toRadians(-20F), 0, 0);
            progressRotation(model.getCube("Jaw"), entity.breathProgress, (float) Math.toRadians(60F), 0, 0);
        }
        if (entity.jumpRot > 0.0F) {

            float turn = (float) entity.motionY * -4F;
            model.getCube("BodyUpper").rotateAngleX += (float) Math.toRadians(22.5F * turn) * entity.jumpRot;
            model.getCube("Tail1").rotateAngleX -= (float) Math.toRadians(turn) * entity.jumpRot;
            model.getCube("Tail2").rotateAngleX -= (float) Math.toRadians(turn) * entity.jumpRot;
            model.getCube("Tail3").rotateAngleX -= (float) Math.toRadians(turn) * entity.jumpRot;
            model.getCube("Tail4").rotateAngleX -= (float) Math.toRadians(turn) * entity.jumpRot;
        }
        entity.tail_buffer.applyChainSwingBuffer(tailParts);
        entity.roll_buffer.applyChainFlapBuffer(model.getCube("BodyUpper"));
        entity.pitch_buffer.applyChainWaveBuffer(model.getCube("BodyUpper"));
        entity.head_buffer.applyChainSwingBufferReverse(neckParts);

    }

    public void progressRotation(AdvancedModelRenderer model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    private void animate(IceAndFireTabulaModel<EntitySeaSerpent> model, EntitySeaSerpent entity) {
        model.llibAnimator.update(entity);
        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_SPEAK);
        model.llibAnimator.startKeyframe(5);
        this.rotate(model.llibAnimator, model.getCube("Jaw"), 25, 0, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.setStaticKeyframe(5);
        model.llibAnimator.resetKeyframe(5);
        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_BITE);
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumSeaSerpentAnimations.BITE1.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumSeaSerpentAnimations.BITE2.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.setStaticKeyframe(2);
        model.llibAnimator.resetKeyframe(3);

        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_ROAR);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR1.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR2.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR3.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);
        model.reset();
    }
}
