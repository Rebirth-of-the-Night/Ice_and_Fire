package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.ItemFishingSpear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** Server-owned fishing targets; particles show the same moving target to all players. */
@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public final class FishingRippleEvents {
    private static final double RADIUS = 0.65D;
    private static final double PLAYER_RANGE = 3.5D;
    private static final double DRIFT_SPEED = 0.075D;
    private static final Map<World, List<Ripple>> RIPPLES = new WeakHashMap<>();

    private FishingRippleEvents() { }

    public static boolean consumeRipple(World world, Vec3d hit) {
        List<Ripple> ripples = RIPPLES.get(world);
        if (ripples == null) {
            return false;
        }
        Iterator<Ripple> iterator = ripples.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            double dx = hit.x - ripple.x;
            double dz = hit.z - ripple.z;
            if (Math.abs(hit.y - (ripple.y + 0.9D)) < 0.5D
                    && dx * dx + dz * dz <= RADIUS * RADIUS) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload event) {
        RIPPLES.remove(event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) {
            return;
        }
        WorldServer world = (WorldServer) event.world;
        List<Ripple> ripples = RIPPLES.computeIfAbsent(world, ignored -> new ArrayList<>());
        Iterator<Ripple> iterator = ripples.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            if (--ripple.life <= 0 || !hasNearbyFisher(world, ripple)
                    || !ItemFishingSpear.isFishingWater(world, new BlockPos(ripple.x, ripple.y, ripple.z))) {
                iterator.remove();
                continue;
            }
            ripple.move(world);
            if (world.getTotalWorldTime() % 4 == 0) {
                ripple.show(world);
            }
        }
        if (world.getTotalWorldTime() % 20 != 0) {
            return;
        }
        for (EntityPlayer player : world.playerEntities) {
            if (!holdsSpear(player) || ripples.stream().anyMatch(r ->
                    player.getPositionEyes(1.0F).squareDistanceTo(r.x, r.y + 0.9D, r.z) < 25)) {
                continue;
            }
            // Search a bounded area near the fisher without loading any new chunks.
            for (int attempt = 0; attempt < 96; attempt++) {
                BlockPos pos = player.getPosition().add(world.rand.nextInt(7) - 3,
                        world.rand.nextInt(5) - 3, world.rand.nextInt(7) - 3);
                Vec3d target = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.9D, pos.getZ() + 0.5D);
                if (player.getPositionEyes(1.0F).squareDistanceTo(target) > PLAYER_RANGE * PLAYER_RANGE
                        || !ItemFishingSpear.isFishingWater(world, pos)) {
                    continue;
                }
                RayTraceResult sight = world.rayTraceBlocks(player.getPositionEyes(1.0F), target, true);
                if (sight == null || pos.equals(sight.getBlockPos())) {
                    ripples.add(new Ripple(pos, world.rand.nextDouble() * Math.PI * 2));
                    break;
                }
            }
        }
    }

    private static boolean holdsSpear(EntityPlayer player) {
        return !player.isSpectator() && (player.getHeldItemMainhand().getItem() instanceof ItemFishingSpear
                || player.getHeldItemOffhand().getItem() instanceof ItemFishingSpear);
    }

    private static boolean hasNearbyFisher(World world, Ripple ripple) {
        for (EntityPlayer player : world.playerEntities) {
            if (holdsSpear(player)
                    && player.getPositionEyes(1.0F).squareDistanceTo(ripple.x, ripple.y + 0.9D, ripple.z) < 25) {
                return true;
            }
        }
        return false;
    }

    private static final class Ripple {
        private double x;
        private final int y;
        private double z;
        private double heading;
        private int life = 600;

        private Ripple(BlockPos pos, double heading) {
            x = pos.getX() + 0.5D;
            y = pos.getY();
            z = pos.getZ() + 0.5D;
            this.heading = heading;
        }

        private void move(World world) {
            // Drift at 1.5 blocks per second, staying within easy spear reach of a fisher.
            EntityPlayer nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (EntityPlayer player : world.playerEntities) {
                double distance = player.getPositionEyes(1.0F).squareDistanceTo(x, y + 0.9D, z);
                if (holdsSpear(player) && distance < nearestDistance) {
                    nearest = player;
                    nearestDistance = distance;
                }
            }
            if (nearest == null) {
                return;
            }
            heading += (world.rand.nextDouble() - 0.5D) * 0.12D;
            for (int attempt = 0; attempt < 8; attempt++) {
                double nextX = x + Math.cos(heading) * DRIFT_SPEED;
                double nextZ = z + Math.sin(heading) * DRIFT_SPEED;
                double distance = nearest.getPositionEyes(1.0F).squareDistanceTo(nextX, y + 0.9D, nextZ);
                if ((distance <= PLAYER_RANGE * PLAYER_RANGE || distance < nearestDistance)
                        && ItemFishingSpear.isFishingWater(world, new BlockPos(nextX, y, nextZ))) {
                    x = nextX;
                    z = nextZ;
                    return;
                }
                heading = Math.atan2(nearest.posZ - z, nearest.posX - x)
                        + (world.rand.nextDouble() - 0.5D) * Math.PI;
            }
        }

        private void show(WorldServer world) {
            for (int i = 0; i < 12; i++) {
                double angle = i * Math.PI * 2 / 12;
                world.spawnParticle(EnumParticleTypes.WATER_WAKE,
                        x + Math.cos(angle) * RADIUS, y + 0.9D, z + Math.sin(angle) * RADIUS,
                        0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
