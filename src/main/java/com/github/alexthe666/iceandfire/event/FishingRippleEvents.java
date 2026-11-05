package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.ItemFishingSpear;
import com.github.alexthe666.iceandfire.message.MessageFishingRipple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Server-owned fishing targets shared with nearby clients. */
@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public final class FishingRippleEvents {
    public static final double RADIUS = 0.65D;
    public static final int SYNC_INTERVAL = 2;
    private static final double PLAYER_RANGE = 3.5D;
    private static final double DRIFT_SPEED = 0.09D;
    private static final int MAX_RESPAWN_DELAY = 60;
    private static final Map<World, FishingSpots> SPOTS = new WeakHashMap<>();
    private static int nextRippleId;

    private FishingRippleEvents() { }

    public static boolean consumeRipple(World world, Vec3d hit) {
        FishingSpots spots = SPOTS.get(world);
        if (spots == null) {
            return false;
        }
        Iterator<Ripple> iterator = spots.active.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            double dx = hit.x - ripple.x;
            double dz = hit.z - ripple.z;
            if (Math.abs(hit.y - (ripple.y + 0.9D)) < 0.5D
                    && dx * dx + dz * dz <= RADIUS * RADIUS) {
                iterator.remove();
                spots.finish(world, ripple);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload event) {
        SPOTS.remove(event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) {
            return;
        }
        WorldServer world = (WorldServer) event.world;
        FishingSpots spots = SPOTS.computeIfAbsent(world, ignored -> new FishingSpots());
        List<Ripple> ripples = spots.active;
        long tick = world.getTotalWorldTime();
        Iterator<Ripple> iterator = ripples.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            if (--ripple.life <= 0 || !hasNearbyFisher(world, ripple)
                    || !ItemFishingSpear.isFishingWater(world, new BlockPos(ripple.x, ripple.y, ripple.z))) {
                iterator.remove();
                spots.finish(world, ripple);
                continue;
            }
            ripple.move(world);
            if (tick % SYNC_INTERVAL == 0) {
                ripple.sync(world, false);
            }
        }
        spots.nextSearch.values().removeIf(next -> next <= tick);
        for (EntityPlayer player : world.playerEntities) {
            UUID playerId = player.getUniqueID();
            if (!holdsSpear(player) || spots.nextSearch.containsKey(playerId)
                    || ripples.stream().anyMatch(r -> r.isNear(player))) {
                continue;
            }
            // Throttle water searches when there is nowhere suitable to spawn.
            spots.nextSearch.put(playerId, tick + 20);
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
                    Ripple ripple = new Ripple(pos, world.rand.nextDouble() * Math.PI * 2);
                    ripples.add(ripple);
                    ripple.sync(world, false);
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
            if (holdsSpear(player) && ripple.isNear(player)) {
                return true;
            }
        }
        return false;
    }

    private static final class FishingSpots {
        private final List<Ripple> active = new ArrayList<>();
        private final Map<UUID, Long> nextSearch = new HashMap<>();

        private void finish(World world, Ripple ripple) {
            ripple.sync(world, true);
            long next = world.getTotalWorldTime() + world.rand.nextInt(MAX_RESPAWN_DELAY + 1);
            for (EntityPlayer player : world.playerEntities) {
                if (ripple.isNear(player)) {
                    nextSearch.put(player.getUniqueID(), next);
                }
            }
        }
    }

    private static final class Ripple {
        private final int id = nextRippleId++;
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

        private boolean isNear(EntityPlayer player) {
            return player.getPositionEyes(1.0F).squareDistanceTo(x, y + 0.9D, z) < 25;
        }

        private void move(World world) {
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

        private void sync(World world, boolean removed) {
            int dimension = world.provider.getDimension();
            IceAndFire.NETWORK_WRAPPER.sendToAllAround(new MessageFishingRipple(dimension, id, x, y + 0.91D, z, removed),
                    new NetworkRegistry.TargetPoint(dimension, x, y, z, 16));
        }
    }
}
