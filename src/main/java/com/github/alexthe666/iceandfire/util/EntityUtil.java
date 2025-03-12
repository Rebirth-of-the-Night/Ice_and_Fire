package com.github.alexthe666.iceandfire.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntityUtil {

    public static Vec3d Y_AXIS = new Vec3d(0, 1, 0);

    private static Random rand = new Random();

    public static Vec3d getEntityVelocity(Entity entity) {
        return new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
    }

    public static Vec3d planeProject(Vec3d vec, Vec3d plane) {
        return EntityUtil.rotateVector2(vec.crossProduct(plane), plane, 90);
    }

    public static Vec3d rotateVector2(Vec3d v, Vec3d k, double degrees) {
        double theta = Math.toRadians(degrees);
        k = k.normalize();
        return v
                .scale(Math.cos(theta))
                .add(k.crossProduct(v)
                        .scale(Math.sin(theta)))
                .add(k.scale(k.dotProduct(v))
                        .scale(1 - Math.cos(theta)));
    }

    public static void addEntityVelocity(Entity entity, Vec3d vec) {
        entity.addVelocity(vec.x, vec.y, vec.z);
    }

    public static Vec3d direction(Vec3d from, Vec3d to) {
        return to.subtract(from).normalize();
    }

    public static void facePosition(Vec3d pos, Entity entity, float maxYawIncrease, float maxPitchIncrease) {
        double d0 = pos.x - entity.posX;
        double d2 = pos.z - entity.posZ;
        double d1 = pos.y - entity.posY;

        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
        entity.rotationPitch = updateRotation(entity.rotationPitch, f1, maxPitchIncrease);
        entity.rotationYaw = updateRotation(entity.rotationYaw, f, maxYawIncrease);
    }

    private static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease) {
            f = maxIncrease;
        }

        if (f < -maxIncrease) {
            f = -maxIncrease;
        }

        return angle + f;
    }

    public static List<Vec3d> getBoundingBoxCorners(AxisAlignedBB box) {
        return new ArrayList<>(Arrays.asList(
                new Vec3d(box.maxX, box.maxY, box.maxZ),
                new Vec3d(box.maxX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.maxZ),
                new Vec3d(box.maxX, box.minY, box.minZ),
                new Vec3d(box.minX, box.maxY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.minZ),
                new Vec3d(box.minX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.minY, box.minZ)));
    }

    /**
     * Pitch of a vector in degrees 90 is up, -90 is down.
     */
    public static double toPitch(Vec3d vec) {
        double angleBetweenYAxis = Math.toDegrees(unsignedAngle(vec, EntityUtil.Y_AXIS.scale(-1)));
        return angleBetweenYAxis - 90;
    }

    public static double toPitchDragon(Vec3d vec) {
        double angleBetweenYAxis = Math.toDegrees(unsignedAngle(vec, EntityUtil.Y_AXIS.scale(-1)));
        return angleBetweenYAxis - 30;
    }

    public static double unsignedAngle(Vec3d a, Vec3d b) {
        double dot = a.dotProduct(b);
        double cos = dot / (a.length() * b.length());
        return Math.acos(cos);
    }

    public static int randSign() {
        return rand.nextInt(2) == 0 ? 1 : -1;
    }

    /**
     * Gets a random float value with expected value 0 and the min and max range
     *
     * @param range The range of the min and max value
     * @return
     */
    public static float getFloat(float range) {
        return rand.nextFloat() * randSign() * range;
    }

    public static Vec3d randVec() {
        return new Vec3d(getFloat(0.5f), getFloat(0.5f), getFloat(0.5f));
    }

    public static Vec3d randFlatVec(Vec3d plane) {
        return randVec().crossProduct(plane).normalize();
    }
}
