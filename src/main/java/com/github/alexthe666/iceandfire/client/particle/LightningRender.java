package com.github.alexthe666.iceandfire.client.particle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Lightning bolt effect code is dragged from the 1.16 source code of IaF.
 * Original code is from Mekanism and belongs to Aidan C. Brady and PupNewfster. 
 * Mekanism is owned by Aidan C. Brady.
 */
@SideOnly(Side.CLIENT)
public class LightningRender {

    // Amount of times per tick we refresh. 3 implies 60 Hz.
    private static final float REFRESH_TIME = 3F;
    // We will keep track of an owner's render data for 100 ticks after there are no bolts remaining.
    private static final double MAX_OWNER_TRACK_TIME = 100;

    private Timestamp refreshTimestamp = new Timestamp();

    private final Random random = new Random();
    private final Minecraft minecraft = Minecraft.getMinecraft();

    private final Map<Object, BoltOwnerData> boltOwners = new Object2ObjectOpenHashMap<>();
    
    public void doRender(float partialTicks) {
        Timestamp timestamp = new Timestamp(minecraft.world.getWorldTime(), partialTicks);
        
        boolean refresh = timestamp.isPassed(refreshTimestamp, (1 / REFRESH_TIME));
        if (refresh) {
            refreshTimestamp = timestamp;
        }
        synchronized (boltOwners) {
            for (Iterator<Map.Entry<Object, BoltOwnerData>> iter = boltOwners.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<Object, BoltOwnerData> entry = iter.next();
                BoltOwnerData data = entry.getValue();
                // tick our bolts based on the refresh rate, removing if they're now finished
                if (refresh) {
                    data.bolts.removeIf(bolt -> bolt.onUpdate(timestamp));
                }
                if (data.bolts.isEmpty() && data.lastBolt != null && data.lastBolt.getSpawnFunction().isConsecutive()) {
                    data.addBolt(new BoltInstance(data.lastBolt, timestamp), timestamp);
                }
                data.bolts.forEach(bolt -> bolt.doRender(timestamp));

                if (data.bolts.isEmpty() && timestamp.isPassed(data.lastUpdateTimestamp, MAX_OWNER_TRACK_TIME)) {
                    iter.remove();
                }
            }
        }
    }

    public void onUpdate(Object owner, LightningBoltData newBoltData, float partialTicks) {
        if (minecraft.world == null) {
            return;
        }
        synchronized (boltOwners) {
            BoltOwnerData data = boltOwners.computeIfAbsent(owner, o -> new BoltOwnerData());
            data.lastBolt = newBoltData;
            Timestamp timestamp = new Timestamp(minecraft.world.getWorldTime(), partialTicks);
            if ((!data.lastBolt.getSpawnFunction().isConsecutive() || data.bolts.isEmpty()) && timestamp.isPassed(data.lastBoltTimestamp, data.lastBoltDelay)) {
                data.addBolt(new BoltInstance(newBoltData, timestamp), timestamp);
            }
            data.lastUpdateTimestamp = timestamp;
        }
    }

    public class BoltOwnerData {

        private final Set<BoltInstance> bolts = new ObjectOpenHashSet<>();
        private LightningBoltData lastBolt;
        private Timestamp lastBoltTimestamp = new Timestamp();
        private Timestamp lastUpdateTimestamp = new Timestamp();
        private double lastBoltDelay;

        private void addBolt(BoltInstance instance, Timestamp timestamp) {
            bolts.add(instance);
            lastBoltDelay = instance.bolt.getSpawnFunction().getSpawnDelay(random);
            lastBoltTimestamp = timestamp;
        }
    }

    public class BoltInstance {

        private final LightningBoltData bolt;
        private final List<LightningBoltData.BoltQuads> renderQuads;
        private Timestamp createdTimestamp;

        public BoltInstance(LightningBoltData bolt, Timestamp timestamp) {
            this.bolt = bolt;
            this.renderQuads = bolt.generate();
            this.createdTimestamp = timestamp;
        }

        public void doRender(Timestamp timestamp) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            float lifeScale = timestamp.subtract(createdTimestamp).value() / bolt.getLifespan();
            Pair<Integer, Integer> bounds = bolt.getFadeFunction().getRenderBounds(renderQuads.size(), lifeScale);
            
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);
            for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
                buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                renderQuads.get(i).getVecs().forEach(v -> buffer.pos((float) v.x, (float) v.y, (float) v.z).color(bolt.getColor().getX(), bolt.getColor().getY(), bolt.getColor().getZ(), bolt.getColor().getW()).endVertex());
                tessellator.draw();
            }
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }

        public boolean onUpdate(Timestamp timestamp) {
            return timestamp.isPassed(createdTimestamp, bolt.getLifespan());
        }
    }

    public static class Timestamp {

        private final long ticks;
        private final float partial;

        public Timestamp() {
            this(0, 0);
        }

        public Timestamp(long ticks, float partial) {
            this.ticks = ticks;
            this.partial = partial;
        }

        public Timestamp subtract(Timestamp other) {
            long newTicks = ticks - other.ticks;
            float newPartial = partial - other.partial;
            if (newPartial < 0) {
                newPartial += 1;
                newTicks -= 1;
            }
            return new Timestamp(newTicks, newPartial);
        }

        public float value() {
            return ticks + partial;
        }

        public boolean isPassed(Timestamp prev, double duration) {
            long ticksPassed = ticks - prev.ticks;
            if (ticksPassed > duration) {
                return true;
            }
            duration -= ticksPassed;
            if (duration >= 1) {
                return false;
            }
            return (partial - prev.partial) >= duration;
        }
    }
}