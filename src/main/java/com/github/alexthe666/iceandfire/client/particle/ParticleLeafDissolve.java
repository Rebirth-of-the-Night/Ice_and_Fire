package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.message.MessageTreeLeafDissolve;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A block-sized ghost of a leaf that is destroyed by dragon breath.
 * Fire: it chars and burns away in patches with glowing edges.
 * Ice: it frosts over tile by tile until it is frozen solid, holds for a moment, then shatters into falling shards.
 * Lightning: it is struck white-blue, throws electric arcs and sparks, then scorches and burns away with a little fire.
 * Leaves far from the branches start first, so the effect seems to creep towards the trunk.
 */
@SideOnly(Side.CLIENT)
public class ParticleLeafDissolve extends Particle {
    private static final int GRID = 4;
    private static final int FIRE_TICKS = 22;
    private static final int ICE_TICKS = 48;
    private static final int LIGHTNING_TICKS = 16;
    private static final float EMBER_WINDOW = 0.18F;
    // ice timeline, as fractions of the whole effect
    private static final float ICE_FREEZE_END = 0.30F;
    private static final float ICE_SHATTER_START = 0.72F;
    private static final float[] ICE_COLOR = {0.74F, 0.91F, 1.0F};

    private final int element;
    private final int duration;
    private final int delay;
    private final float[] thresholds = new float[GRID * GRID];
    // ice only: when each tile breaks off, and its sideways drift
    private final float[] shatterDelays = new float[GRID * GRID];
    private final float[] driftA = new float[GRID * GRID];
    private final float[] driftB = new float[GRID * GRID];
    private final float tintRed;
    private final float tintGreen;
    private final float tintBlue;
    private boolean shattered;

    public ParticleLeafDissolve(World world, BlockPos pos, IBlockState state, int distanceFromBranches, int element) {
        super(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        this.element = element;
        this.duration = element == MessageTreeLeafDissolve.ELEMENT_ICE ? ICE_TICKS
                : element == MessageTreeLeafDissolve.ELEMENT_LIGHTNING ? LIGHTNING_TICKS : FIRE_TICKS;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.canCollide = false;
        this.particleGravity = 0;
        this.setParticleTexture(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        int color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, pos, 0);
        this.tintRed = (color >> 16 & 255) / 255F;
        this.tintGreen = (color >> 8 & 255) / 255F;
        this.tintBlue = (color & 255) / 255F;
        this.delay = Math.max(0, 4 - distanceFromBranches) * 5 + this.rand.nextInt(4);
        this.particleMaxAge = delay + duration + 2;
        for (int i = 0; i < thresholds.length; i++) {
            thresholds[i] = 0.1F + this.rand.nextFloat() * 0.9F;
            shatterDelays[i] = this.rand.nextFloat() * 0.5F;
            driftA[i] = (this.rand.nextFloat() - 0.5F) * 1.2F;
            driftB[i] = (this.rand.nextFloat() - 0.5F) * 0.6F;
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    private float progress(float partialTicks) {
        return MathHelper.clamp((this.particleAge + partialTicks - delay) / duration, 0F, 1F);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        float p = progress(0F);
        if (element == MessageTreeLeafDissolve.ELEMENT_ICE) {
            updateIce(p);
        } else if (element == MessageTreeLeafDissolve.ELEMENT_LIGHTNING) {
            updateLightning(p);
        } else {
            updateFire(p);
        }
    }

    private void updateFire(float p) {
        if (p > 0.1F) {
            this.posY += 0.01D * p;
            if (this.rand.nextFloat() < 0.06F) {
                this.world.spawnParticle(EnumParticleTypes.FLAME, randomX(), randomY(), randomZ(), 0, 0.02D, 0);
            }
            if (p > 0.4F && this.rand.nextFloat() < 0.05F) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, randomX(), randomY(), randomZ(), 0, 0.03D, 0);
            }
        }
    }

    private void updateLightning(float p) {
        if (p <= 0F || p >= 0.95F) {
            return;
        }
        if (p < 0.5F && this.rand.nextFloat() < 0.05F) {
            // a small arc jumping from this leaf to somewhere nearby, drawn with the lightning dragon's bolt renderer
            Vec3d from = new Vec3d(randomX(), randomY(), randomZ());
            Vec3d to = from.add((this.rand.nextFloat() - 0.5D) * 2.0D, (this.rand.nextFloat() - 0.5D) * 2.0D, (this.rand.nextFloat() - 0.5D) * 2.0D);
            LeafLightningRenderer.arc(from, to);
        }
        if (this.rand.nextFloat() < 0.08F) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, randomX(), randomY(), randomZ(),
                    (this.rand.nextFloat() - 0.5D) * 0.4D, (this.rand.nextFloat() - 0.5D) * 0.4D, (this.rand.nextFloat() - 0.5D) * 0.4D);
        }
        if (p > 0.3F && this.rand.nextFloat() < 0.05F) {
            // the strike sets the leaves alight
            this.world.spawnParticle(EnumParticleTypes.FLAME, randomX(), randomY(), randomZ(), 0, 0.02D, 0);
        }
    }

    private void updateIce(float p) {
        if (p > 0.05F && p < ICE_FREEZE_END && this.rand.nextFloat() < 0.03F) {
            // cold mist while it freezes
            this.world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, randomX(), randomY(), randomZ(), 0, -0.01D, 0);
        }
        if (!shattered && p >= ICE_SHATTER_START) {
            shattered = true;
            // the moment it breaks: a few ice shards, snow puffs and, rarely, a glass-break sound
            if (this.rand.nextFloat() < 0.5F) {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX, this.posY, this.posZ,
                        (this.rand.nextFloat() - 0.5D) * 0.2D, 0.05D, (this.rand.nextFloat() - 0.5D) * 0.2D,
                        Block.getStateId(Blocks.ICE.getDefaultState()));
            }
            if (this.rand.nextFloat() < 0.5F) {
                this.world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, randomX(), randomY(), randomZ(),
                        (this.rand.nextFloat() - 0.5D) * 0.1D, 0.02D, (this.rand.nextFloat() - 0.5D) * 0.1D);
            }
            if (this.rand.nextFloat() < 0.015F) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.4F, 1.2F + this.rand.nextFloat() * 0.5F, false);
            }
        }
    }

    private double randomX() {
        return this.posX + this.rand.nextFloat() - 0.5D;
    }

    private double randomY() {
        return this.posY + this.rand.nextFloat() - 0.5D;
    }

    private double randomZ() {
        return this.posZ + this.rand.nextFloat() - 0.5D;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        if (this.particleTexture == null) {
            return;
        }
        float p = progress(partialTicks);
        double cx = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
        double cy = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
        double cz = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
        int baseLight = this.getBrightnessForRender(partialTicks);
        float minU = this.particleTexture.getMinU();
        float maxU = this.particleTexture.getMaxU();
        float minV = this.particleTexture.getMinV();
        float maxV = this.particleTexture.getMaxV();
        boolean ice = element == MessageTreeLeafDissolve.ELEMENT_ICE;
        boolean lightning = element == MessageTreeLeafDissolve.ELEMENT_LIGHTNING;
        int age = this.particleAge;

        // fire: leaf colour fades to char as it burns
        float charred = MathHelper.clamp(p * 2.5F, 0F, 1F);
        float baseR = tintRed + (0.12F - tintRed) * charred;
        float baseG = tintGreen + (0.09F - tintGreen) * charred;
        float baseB = tintBlue + (0.08F - tintBlue) * charred;

        double half = 0.5D;
        double step = 2.0D / GRID;
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                int tile = i * GRID + j;
                float threshold = thresholds[tile];
                float r;
                float g;
                float b;
                float alpha = 1.0F;
                int light;
                // tile centre and half-extent in the quad's [-1, 1] space
                double a0 = -1.0D + i * step;
                double b0 = -1.0D + j * step;
                double centerA = a0 + step / 2.0D;
                double centerB = b0 + step / 2.0D;
                double extent = step / 2.0D;

                if (ice) {
                    // frost spreads tile by tile, each tile flashing white as it freezes solid
                    float freezeAt = threshold * ICE_FREEZE_END;
                    float frost = MathHelper.clamp((p - (freezeAt - 0.08F)) / 0.08F, 0F, 1F);
                    float flash = MathHelper.clamp(1F - (p - freezeAt) / 0.06F, 0F, 1F) * (p >= freezeAt ? 1F : 0F);
                    float variation = (threshold - 0.5F) * 0.12F;
                    r = tintRed + (MathHelper.clamp(ICE_COLOR[0] + variation, 0F, 1F) - tintRed) * frost;
                    g = tintGreen + (MathHelper.clamp(ICE_COLOR[1] + variation, 0F, 1F) - tintGreen) * frost;
                    b = tintBlue + (MathHelper.clamp(ICE_COLOR[2] + variation, 0F, 1F) - tintBlue) * frost;
                    r += (1F - r) * flash;
                    g += (1F - g) * flash;
                    b += (1F - b) * flash;
                    // frozen tiles glow faintly, fresh ones flash at full brightness
                    int sky = baseLight >> 16 & 65535;
                    int blk = baseLight & 65535;
                    int boost = flash > 0F ? 240 : 150;
                    float mix = flash > 0F ? 1F : frost;
                    sky += (int) ((Math.max(sky, boost) - sky) * mix);
                    blk += (int) ((Math.max(blk, boost) - blk) * mix);
                    light = sky << 16 | blk;

                    float s = MathHelper.clamp((p - ICE_SHATTER_START) / (1F - ICE_SHATTER_START), 0F, 1F);
                    float si = MathHelper.clamp((s * 1.5F - shatterDelays[tile]), 0F, 1F);
                    if (si >= 1F) {
                        continue;
                    }
                    if (si > 0F) {
                        // the tile breaks off: drifts outward, falls, shrinks and fades
                        centerA += centerA * si * 0.8D + driftA[tile] * si;
                        centerB += centerB * si * 0.4D + driftB[tile] * si - 2.2D * si * si;
                        extent *= 1.0D - 0.6D * si;
                        alpha = 1F - si * si;
                    }
                } else if (lightning) {
                    if (p >= threshold) {
                        continue;
                    }
                    // struck leaves light up white-blue once, fade to a warm scorched brown and glow orange as they burn away
                    float flashL = MathHelper.clamp(1F - p / 0.25F, 0F, 1F);
                    boolean ember = p > threshold - EMBER_WINDOW;
                    float scorch = MathHelper.clamp(p * 3F, 0F, 0.65F);
                    float baseLr = tintRed + (0.35F - tintRed) * scorch;
                    float baseLg = tintGreen + (0.27F - tintGreen) * scorch;
                    float baseLb = tintBlue + (0.2F - tintBlue) * scorch;
                    r = ember ? 1.0F : baseLr + (0.85F - baseLr) * flashL;
                    g = ember ? 0.5F : baseLg + (0.93F - baseLg) * flashL;
                    b = ember ? 0.12F : baseLb + (1.0F - baseLb) * flashL;
                    light = ember || flashL > 0.3F ? 0xF000F0 : baseLight;
                } else {
                    if (p >= threshold) {
                        continue;
                    }
                    boolean ember = p > threshold - EMBER_WINDOW;
                    r = ember ? 1.0F : baseR;
                    g = ember ? 0.45F + 0.2F * (threshold - p) / EMBER_WINDOW : baseG;
                    b = ember ? 0.08F : baseB;
                    light = ember ? 0xF000F0 : baseLight;
                }
                int lightU = light >> 16 & 65535;
                int lightV = light & 65535;

                double ta0 = centerA - extent;
                double ta1 = centerA + extent;
                double tb0 = centerB - extent;
                double tb1 = centerB + extent;
                // texture coordinates always come from the tile's original place on the leaf
                double ua0 = a0;
                double ua1 = a0 + step;
                double vb0 = b0;
                double vb1 = b0 + step;
                float u0 = maxU + (minU - maxU) * (float) ((ua0 + 1.0D) / 2.0D);
                float u1 = maxU + (minU - maxU) * (float) ((ua1 + 1.0D) / 2.0D);
                float v0 = maxV + (minV - maxV) * (float) ((vb0 + 1.0D) / 2.0D);
                float v1 = maxV + (minV - maxV) * (float) ((vb1 + 1.0D) / 2.0D);

                vertex(buffer, cx, cy, cz, half, ta0, tb0, rotX, rotZ, rotYZ, rotXY, rotXZ, u0, v0, r, g, b, alpha, lightU, lightV);
                vertex(buffer, cx, cy, cz, half, ta0, tb1, rotX, rotZ, rotYZ, rotXY, rotXZ, u0, v1, r, g, b, alpha, lightU, lightV);
                vertex(buffer, cx, cy, cz, half, ta1, tb1, rotX, rotZ, rotYZ, rotXY, rotXZ, u1, v1, r, g, b, alpha, lightU, lightV);
                vertex(buffer, cx, cy, cz, half, ta1, tb0, rotX, rotZ, rotYZ, rotXY, rotXZ, u1, v0, r, g, b, alpha, lightU, lightV);
            }
        }
    }

    // a runs along the camera's right vector, b along its up vector, both in [-1, 1] across the block-sized quad.
    private static void vertex(BufferBuilder buffer, double cx, double cy, double cz, double half, double a, double b, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ, float u, float v, float r, float g, float bl, float alpha, int lightU, int lightV) {
        double x = cx + half * (a * rotX + b * rotXY);
        double y = cy + half * (b * rotZ);
        double z = cz + half * (a * rotYZ + b * rotXZ);
        buffer.pos(x, y, z).tex(u, v).color(r, g, bl, alpha).lightmap(lightU, lightV).endVertex();
    }
}
