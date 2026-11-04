package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.ItemFishingSpear;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

/** Animates the held model through its perspective matrix, without a render/tick event. */
@Mod.EventBusSubscriber(modid = IceAndFire.MODID, value = Side.CLIENT)
public final class FishingSpearItemModel implements IBakedModel {
    private final IBakedModel model;
    private final float offset;
    private final float pull;

    private FishingSpearItemModel(IBakedModel model, float offset, float pull) {
        this.model = model;
        this.offset = offset;
        this.pull = pull;
    }

    @SubscribeEvent
    public static void bake(ModelBakeEvent event) {
        ModelResourceLocation location = new ModelResourceLocation("iceandfire:fishing_spear", "inventory");
        IBakedModel model = event.getModelRegistry().getObject(location);
        if (model != null) {
            event.getModelRegistry().putObject(location, new FishingSpearItemModel(model, 0, 0));
        }
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        Pair<? extends IBakedModel, Matrix4f> perspective = model.handlePerspective(type);
        if (type != ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                && type != ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            return perspective;
        }
        // View-space movement: positive Z draws back, negative Z thrusts toward the target.
        // Forge mirrors X for the left hand automatically.
        Matrix4f animation = new Matrix4f();
        animation.rotX((float) Math.toRadians(-8 * pull));
        animation.m13 = -0.04F * pull;
        animation.m23 = offset;
        if (perspective.getRight() != null) {
            animation.mul(perspective.getRight());
        }
        return Pair.of(perspective.getLeft(), animation);
    }

    private static float smooth(float value) {
        float t = MathHelper.clamp(value, 0, 1);
        return t * t * (3 - 2 * t);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel original, ItemStack stack, World world, EntityLivingBase entity) {
                IBakedModel resolved = model.getOverrides().handleItemState(model, stack, world, entity);
                float offset = 0;
                float pull = 0;
                float partial = Minecraft.getMinecraft().getRenderPartialTicks();
                if (entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack) {
                    pull = smooth((entity.getItemInUseMaxCount() + partial) / ItemFishingSpear.CHARGE_TICKS);
                    offset = 0.22F * pull;
                } else if (entity instanceof EntityPlayer) {
                    // The server's existing cooldown packet starts the release animation only
                    // for a valid fishing attempt, including a failed catch. No extra packets.
                    float cooldown = ((EntityPlayer) entity).getCooldownTracker().getCooldown(stack.getItem(), partial);
                    if (cooldown > 0) {
                        float elapsed = (1 - cooldown) * ItemFishingSpear.COOLDOWN_TICKS;
                        if (elapsed < 3) {
                            float thrust = smooth(elapsed / 3);
                            offset = 0.22F - 0.87F * thrust;
                            pull = 1 - thrust;
                        } else if (elapsed < 8) {
                            offset = -0.65F * (1 - smooth((elapsed - 3) / 5));
                        }
                    }
                }
                return new FishingSpearItemModel(resolved, offset, pull);
            }
        };
    }

    @Override public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) { return model.getQuads(state, side, rand); }
    @Override public boolean isAmbientOcclusion() { return model.isAmbientOcclusion(); }
    @Override public boolean isGui3d() { return model.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return model.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return model.getParticleTexture(); }
    @Override public ItemCameraTransforms getItemCameraTransforms() { return model.getItemCameraTransforms(); }
}
