package com.github.alexthe666.iceandfire.client.model;

import java.util.List;
import java.util.Collections;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

/** Keeps the inventory sprite, selecting the projectile model only in hands. */
@Mod.EventBusSubscriber(modid = "iceandfire", value = Side.CLIENT)
public final class TideTridentItemModel implements IBakedModel {
    private final IBakedModel icon;
    private final IBakedModel held;
    private final boolean charging;

    private TideTridentItemModel(IBakedModel icon, IBakedModel held) {
        this(icon, held, false);
    }

    private TideTridentItemModel(IBakedModel icon, IBakedModel held, boolean charging) {
        this.icon = icon;
        this.held = held;
        this.charging = charging;
    }

    @SubscribeEvent
    public static void bake(ModelBakeEvent event) {
        ModelResourceLocation inventory = new ModelResourceLocation("iceandfire:tide_trident", "inventory");
        IBakedModel icon = event.getModelRegistry().getObject(inventory);
        IBakedModel held = event.getModelRegistry().getObject(new ModelResourceLocation("iceandfire:tide_trident_in_hand", "inventory"));
        if (icon != null && held != null) {
            event.getModelRegistry().putObject(inventory, new TideTridentItemModel(icon, held));
        }
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        switch (type) {
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                Pair<? extends IBakedModel, Matrix4f> perspective = held.handlePerspective(type);
                if (!charging) {
                    if (type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                            || type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                        // Apply in view space so the tilt points forward in either hand.
                        // Forge mirrors X for the left hand when applying this matrix.
                        Matrix4f resting = new Matrix4f();
                        resting.rotX((float) Math.toRadians(-25));
                        resting.m03 = 0.25F;
                        resting.m13 = -0.08F;
                        resting.m23 = -0.15F;
                        if (perspective.getRight() != null) resting.mul(perspective.getRight());
                        moveShaftBack(resting, 0.45F);
                        return Pair.of(perspective.getLeft(), resting);
                    }
                    return perspective;
                }
                // Reverse the shaft around the grip, after the hand's display transform.
                // Use the rendered entity's active stack so offhand and remote players work too.
                Matrix4f transform = new Matrix4f();
                if (perspective.getRight() == null) transform.setIdentity();
                else transform.set(perspective.getRight());
                Matrix4f reverse = new Matrix4f();
                reverse.rotZ((float) Math.PI);
                transform.mul(reverse);
                // Match the projectile's roll: keep the three prongs vertical
                // while aiming, without changing the shaft's throwing direction.
                Matrix4f roll = new Matrix4f();
                roll.rotY((float) (Math.PI / 2.0));
                transform.mul(roll);
                if (type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                        || type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                    moveShaftBack(transform, 0.65F);
                }
                return Pair.of(perspective.getLeft(), transform);
            default:
                return icon.handlePerspective(type);
        }
    }

    private static void moveShaftBack(Matrix4f transform, float distance) {
        // Slide along the shaft toward its butt, rather than shortening the model.
        // In first person this lets the rear end continue beyond the screen edge.
        Matrix4f offset = new Matrix4f();
        offset.setIdentity();
        offset.m13 = -distance;
        transform.mul(offset);
    }

    @Override public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) { return icon.getQuads(state, side, rand); }
    @Override public boolean isAmbientOcclusion() { return icon.isAmbientOcclusion(); }
    @Override public boolean isGui3d() { return icon.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleTexture() { return icon.getParticleTexture(); }
    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel original, ItemStack stack, World world, EntityLivingBase entity) {
                boolean using = entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack;
                IBakedModel resolvedIcon = icon.getOverrides().handleItemState(icon, stack, world, entity);
                return new TideTridentItemModel(resolvedIcon, held, using);
            }
        };
    }
}
