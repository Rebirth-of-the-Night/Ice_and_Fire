package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.BlockEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DreadCastleProtection {
    private static final BlockPos CASTLE_CORNER = new BlockPos(-126, 0, -330);
    private static final int CASTLE_WIDTH = 280;
    private static final int CASTLE_HEIGHT = 250;
    private static final int CASTLE_DEPTH = 280;

    private static final Set<Block> BREAKABLE_BLOCKS = new HashSet<>();
    static {
        BREAKABLE_BLOCKS.add(IafBlockRegistry.dread_spawner);
        BREAKABLE_BLOCKS.add(Blocks.TORCH);
        BREAKABLE_BLOCKS.add(Blocks.SNOW);
        BREAKABLE_BLOCKS.add(Blocks.SNOW_LAYER);
        BREAKABLE_BLOCKS.add(Blocks.CHEST);
        BREAKABLE_BLOCKS.add(IafBlockRegistry.dragonsteel_ice_block);
        BREAKABLE_BLOCKS.add(IafBlockRegistry.dread_torch);
    }

    public static class CastleProtectionData extends WorldSavedData {
        private static final String DATA_NAME = IceAndFire.MODID + "_dread_castle_protection";
        private final Set<UUID> defeatedBosses = new HashSet<>();

        public CastleProtectionData() {
            super(DATA_NAME);
        }

        public CastleProtectionData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            defeatedBosses.clear();
            NBTTagList list = nbt.getTagList("DefeatedBosses", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                String uuidString = list.getStringTagAt(i);
                try {
                    defeatedBosses.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    IceAndFire.logger.error("Invalid UUID in dread castle protection data: " + uuidString);
                }
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (UUID uuid : defeatedBosses) {
                list.appendTag(new NBTTagString(uuid.toString()));
            }
            nbt.setTag("DefeatedBosses", list);
            return nbt;
        }

        public void markBossDefeated(UUID bossId) {
            defeatedBosses.add(bossId);
            markDirty();
            IceAndFire.logger.info("Marked dread boss as defeated: " + bossId);
        }

        public static CastleProtectionData get(World world) {
            if (world == null) return null;

            CastleProtectionData data = (CastleProtectionData) world.getPerWorldStorage()
                    .getOrLoadData(CastleProtectionData.class, DATA_NAME);

            if (data == null) {
                data = new CastleProtectionData();
                world.getPerWorldStorage().setData(DATA_NAME, data);
            }

            return data;
        }
    }

    public static boolean isInCastleArea(World world, BlockPos pos) {
        AxisAlignedBB castleBounds = new AxisAlignedBB(
                CASTLE_CORNER,
                CASTLE_CORNER.add(CASTLE_WIDTH, CASTLE_HEIGHT, CASTLE_DEPTH)
        );

        return world.provider.getDimension() == IceAndFire.CONFIG.dreadlandsDimensionId &&
                castleBounds.contains(
                new Vec3d(pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D)
        );
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!isInCastleArea(event.getWorld(), event.getPos())) {
            return;
        }

        EntityPlayer player = event.getPlayer();
        //if (player.capabilities.isCreativeMode) {
        //    return;
        //}

        Block block = event.getState().getBlock();
        if (BREAKABLE_BLOCKS.contains(block)) {
            return;
        }

        if (!hasBossBeenDefeated(event.getWorld())) {
            event.setCanceled(true);
            player.sendStatusMessage(
                    new TextComponentTranslation("message.iceandfire.dread_castle_protected"),
                    true
            );
        }
    }

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();
        BlockPos pos = event.getPos();

        if (isInCastleArea(event.getWorld(), pos) && !player.capabilities.isCreativeMode) {
            event.setCanceled(true);
            player.sendStatusMessage(
                    new TextComponentTranslation("message.iceandfire.dread_castle_protected"),
                    true
            );
        }
    }

    private static boolean hasBossBeenDefeated(World world) {
        CastleProtectionData data = CastleProtectionData.get(world);
        return !data.defeatedBosses.isEmpty();
    }

    public static void onBossDefeated(EntityDreadQueen boss) {
        World world = boss.world;
        if (!world.isRemote) {
            CastleProtectionData data = CastleProtectionData.get(world);
            data.markBossDefeated(boss.getUniqueID());
            for (EntityPlayer player : world.playerEntities) {
                player.sendMessage(new TextComponentTranslation("message.iceandfire.dread_castle_defeated"));
            }
        }
    }
}