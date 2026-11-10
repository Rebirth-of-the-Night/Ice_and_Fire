package com.github.alexthe666.iceandfire.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.message.MessageTreeLeafDissolve;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects leaves that are destroyed by dragon breath during a tick and sends them to nearby clients in one
 * batched packet at the end of the tick, so they can be shown dissolving instead of vanishing. Dragon breath hits
 * many blocks per tick, so sending a packet per leaf would flood the network.
 */
@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public class LeafBurnEffects {
    /**
     * Upper bound of leaves shown per packet and per tick, to keep packets and particle counts sane.
     */
    private static final int MAX_LEAVES_PER_PACKET = 600;
    private static final int SEND_RADIUS = 96;

    private static final Map<Integer, List<Entry>> PENDING = new HashMap<>();

    private static class Entry {
        final BlockPos pos;
        final int stateId;
        final int distance;
        final int element;

        Entry(BlockPos pos, int stateId, int distance, int element) {
            this.pos = pos;
            this.stateId = stateId;
            this.distance = distance;
            this.element = element;
        }
    }

    /**
     * Queues a leaf that has just been removed.
     *
     * @param visualState the leaf state clients should draw (it must resolve to a normal block texture)
     * @param distance    1-4, how far the leaf is from the trunk or branches; leaves further out burn first
     */
    public static void queue(World world, BlockPos pos, IBlockState visualState, int distance, int element) {
        if (world.isRemote || !IceAndFire.CONFIG.leafBurnEffects) {
            return;
        }
        List<Entry> list = PENDING.computeIfAbsent(world.provider.getDimension(), dim -> new ArrayList<>());
        if (list.size() < MAX_LEAVES_PER_PACKET * 4) {
            list.add(new Entry(pos.toImmutable(), Block.getStateId(visualState), distance, element));
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) {
            return;
        }
        List<Entry> list = PENDING.remove(event.world.provider.getDimension());
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Integer, List<Entry>> byElement = new HashMap<>();
        for (Entry entry : list) {
            byElement.computeIfAbsent(entry.element, e -> new ArrayList<>()).add(entry);
        }
        for (Map.Entry<Integer, List<Entry>> group : byElement.entrySet()) {
            send(event.world, group.getKey(), group.getValue());
        }
    }

    private static void send(World world, int element, List<Entry> entries) {
        List<Entry> sample = entries;
        if (sample.size() > MAX_LEAVES_PER_PACKET) {
            // keep a random subset; the leaves themselves are all removed regardless
            sample = new ArrayList<>(entries);
            java.util.Collections.shuffle(sample, world.rand);
            sample = sample.subList(0, MAX_LEAVES_PER_PACKET);
        }
        BlockPos origin = sample.get(0).pos;
        List<Integer> palette = new ArrayList<>();
        List<int[]> leaves = new ArrayList<>();
        for (Entry entry : sample) {
            int index = palette.indexOf(entry.stateId);
            if (index < 0) {
                if (palette.size() >= 255) {
                    continue;
                }
                index = palette.size();
                palette.add(entry.stateId);
            }
            int dx = entry.pos.getX() - origin.getX();
            int dy = entry.pos.getY() - origin.getY();
            int dz = entry.pos.getZ() - origin.getZ();
            if (Math.abs(dx) > Short.MAX_VALUE || Math.abs(dy) > Short.MAX_VALUE || Math.abs(dz) > Short.MAX_VALUE) {
                continue;
            }
            leaves.add(new int[]{dx, dy, dz, index, entry.distance});
        }
        int[] paletteArray = palette.stream().mapToInt(Integer::intValue).toArray();
        MessageTreeLeafDissolve message = new MessageTreeLeafDissolve(origin, element, paletteArray, leaves);
        IceAndFire.NETWORK_WRAPPER.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), origin.getX(), origin.getY(), origin.getZ(), SEND_RADIUS));
    }
}
