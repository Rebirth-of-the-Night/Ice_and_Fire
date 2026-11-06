package com.github.alexthe666.iceandfire.compat;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityTideTrident;
import com.github.alexthe666.iceandfire.item.ItemTideTrident;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID)
public final class OceanicExpanseCompat {
    public static final ResourceLocation LOYALTY = enchantment("loyalty");
    private static final ResourceLocation RIPTIDE = enchantment("riptide");
    private static final ResourceLocation CHANNELING = enchantment("channeling");
    private static final ResourceLocation IMPALING = enchantment("is_watermob");
    private static final ResourceLocation WATER_JET = enchantment("is_wet");
    private static final String OE_PACKAGE = "com.sirsquidly.oe.";

    private OceanicExpanseCompat() {
    }

    public static boolean isTideTrident(ItemStack stack) {
        return stack.getItem() instanceof ItemTideTrident;
    }

    public static int getLevel(ResourceLocation id, ItemStack stack) {
        Enchantment enchantment = Enchantment.REGISTRY.getObject(id);
        return enchantment == null ? 0 : EnchantmentHelper.getEnchantmentLevel(enchantment, stack);
    }

    public static boolean canCharge(ItemStack stack, World world, EntityPlayer player) {
        if (getRiptideLevel(stack) == 0) {
            return true;
        }
        try {
            Class<?> trident = Class.forName(OE_PACKAGE + "items.ItemTrident");
            return (boolean) trident.getMethod("canLetItRip", World.class, EntityLivingBase.class).invoke(null, world, player);
        } catch (ReflectiveOperationException e) {
            IceAndFire.logger.warn("Could not check Oceanic Expanse Riptide conditions", e);
            return false;
        }
    }

    public static int getRiptideLevel(ItemStack stack) {
        return getLevel(RIPTIDE, stack);
    }

    public static boolean handleRiptideRelease(ItemStack stack, World world, EntityLivingBase user, int timeLeft) {
        if (!(user instanceof EntityPlayer) || getRiptideLevel(stack) == 0) {
            return false;
        }
        try {
            Object config = config("riptide");
            if (user.isSneaking() && (boolean) field(config, "riptideSneakThrowing")) {
                return !canCharge(stack, world, (EntityPlayer) user);
            }
            Item trident = (Item) Class.forName(OE_PACKAGE + "init.OEItems").getField("TRIDENT_ORIG").get(null);
            trident.onPlayerStoppedUsing(stack, world, user, timeLeft);
        } catch (ReflectiveOperationException e) {
            IceAndFire.logger.warn("Could not activate Oceanic Expanse Riptide", e);
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTridentHit(LivingHurtEvent event) {
        Entity projectile = event.getSource().getImmediateSource();
        if (!(projectile instanceof EntityTideTrident)) {
            return;
        }
        Entity owner = event.getSource().getTrueSource();
        ItemStack mainHand = owner instanceof EntityLivingBase
                ? ((EntityLivingBase) owner).getHeldItemMainhand() : ItemStack.EMPTY;
        ItemStack thrown = ((EntityTideTrident) projectile).getArrowStack();
        EntityLivingBase target = event.getEntityLiving();
        int impaling = getLevel(IMPALING, thrown) - getLevel(IMPALING, mainHand);
        int waterJet = getLevel(WATER_JET, thrown) - getLevel(WATER_JET, mainHand);
        if (impaling == 0 && waterJet == 0) {
            return;
        }
        try {
            // OE adds the main-hand bonus in LivingHurtEvent. Adjust it to use the thrown stack.
            Object config = config("impaling");
            boolean wet = target.isWet();
            boolean impalingApplies = (int) field(config, "enableWaterJet") == 2
                    ? wet : !(target instanceof EntityPlayer) && configList("aquaticMobs").contains(EntityList.getKey(target));
            float damage = impalingApplies ? impaling * (float) field(config, "impalingDamage") : 0.0F;
            if (wet) {
                damage += waterJet * (float) field(config, "waterJetDamage");
            }
            event.setAmount(Math.max(0.0F, event.getAmount() + damage));
        } catch (ReflectiveOperationException e) {
            IceAndFire.logger.warn("Could not apply Oceanic Expanse trident damage bonuses", e);
        }
    }

    public static boolean channelLightning(ItemStack stack, World world, BlockPos pos, EntityLivingBase target, BlockPos hitBlock) {
        if (world.isRemote || getLevel(CHANNELING, stack) == 0 || !world.isThundering() || !world.canSeeSky(pos)) {
            return false;
        }
        try {
            Object config = config("channeling");
            if (target != null) {
                if ((boolean) field(config, "waterCheck") && target.isInWater()
                        || (boolean) field(config, "lavaCheck") && target.isInLava()
                        || configList("ridingBlacklist").contains(EntityList.getKey(target.getLowestRidingEntity()))) {
                    return false;
                }
            } else if (hitBlock == null || Arrays.asList((String[]) field(config, "lightningRodWhitelist"))
                    .contains(String.valueOf(world.getBlockState(hitBlock).getBlock().getRegistryName()))
                    == (boolean) field(config, "invertLightning")) {
                return false;
            }
            SoundEvent thunder = tridentSound("thunder");
            if (thunder != null) {
                world.playSound(null, pos, thunder, SoundCategory.WEATHER, 1.0F, 1.0F);
            }
            return world.addWeatherEffect(new EntityLightningBolt(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, false));
        } catch (ReflectiveOperationException e) {
            IceAndFire.logger.warn("Could not apply Oceanic Expanse Channeling", e);
            return false;
        }
    }

    public static SoundEvent tridentSound(String sound) {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("oe", "item.trident." + sound));
    }

    private static ResourceLocation enchantment(String name) {
        return new ResourceLocation("oe", name);
    }

    private static Object config(String category) throws ReflectiveOperationException {
        Object enchants = Class.forName(OE_PACKAGE + "util.handlers.ConfigHandler").getField("enchant").get(null);
        return field(enchants, category);
    }

    private static Object field(Object instance, String name) throws ReflectiveOperationException {
        return instance.getClass().getField(name).get(instance);
    }

    private static List<?> configList(String name) throws ReflectiveOperationException {
        return (List<?>) Class.forName(OE_PACKAGE + "util.handlers.ConfigArrayHandler").getField(name).get(null);
    }
}
