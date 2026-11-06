package com.github.alexthe666.iceandfire.compat.bauble.common;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * Handles capability based integration with the optional <i>Baubles</i> mod for the legendary
 * items (cyclops eye, hydra heart). Items must call {@link #enabled()} before touching anything here,
 * so that {@code baubles.api} is never loaded when the mod is absent.
 */
public final class IafBaublesIntegration {

    private static final String BAUBLES_MOD_ID = "baubles";

    private IafBaublesIntegration() {
    }

    public static boolean enabled() {
        return IceAndFire.CONFIG.baublesIntegration && Loader.isModLoaded(BAUBLES_MOD_ID);
    }

    /**
     * Creates a provider that lets the stack be worn in a charm slot and runs {@code wornTick} every tick while worn.
     * Takes no Baubles types so callers never reference {@code baubles.api}.
     */
    public static ICapabilityProvider createCharmProvider(BiConsumer<ItemStack, EntityLivingBase> wornTick) {
        return new Provider(BaubleType.CHARM, wornTick);
    }

    @SuppressWarnings("unchecked")
    private static final class Provider implements ICapabilityProvider {

        private final IBauble bauble;

        private Provider(BaubleType type, BiConsumer<ItemStack, EntityLivingBase> wornTick) {
            this.bauble = new IBauble() {
                @Override
                public BaubleType getBaubleType(ItemStack itemStack) {
                    return type;
                }

                @Override
                public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
                    wornTick.accept(itemstack, player);
                }
            };
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
        }

        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (T) bauble : null;
        }
    }
}
