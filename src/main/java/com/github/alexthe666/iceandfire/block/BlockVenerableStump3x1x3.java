package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

public class BlockVenerableStump3x1x3 extends Block {
    public BlockVenerableStump3x1x3() {
        super(Material.WOOD);
        this.setTranslationKey("iceandfire.venerable_stump");
        this.setHarvestLevel("axe", 0);
        this.setHardness(12F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(IceAndFire.TAB_BLOCKS);
        this.setLightOpacity(0);
        this.setLightLevel(1f);
        translucent = true;
        this.fullBlock = false;
        setRegistryName(IceAndFire.MODID, "venerable_stump");
    }

    enum StumpPart implements IStringSerializable {
        // --
        NORTH_WEST(new Vec3i(-1, 0, -1)),
        // -0
        WEST(new Vec3i(-1, 0, 0)),
        // -+
        SOUTH_WEST(new Vec3i(-1, 0, +1)),
        // 0-
        NORTH(new Vec3i(0, 0, -1)),
        // 00
        CENTER(new Vec3i(0, 0, 0)),
        // 0+
        SOUTH(new Vec3i(0, 0, +1)),
        // +-
        NORTH_EAST(new Vec3i(+1, 0, -1)),
        // +0
        EAST(new Vec3i(+1, 0, 0)),
        // ++
        SOUTH_EAST(new Vec3i(+1, 0, +1));

        Vec3i fromCenter;

        StumpPart(Vec3i fromCenter) {
            this.fromCenter = fromCenter;
        }

        @Override
        @Nonnull
        public String getName() {
            return this.toString().toLowerCase();
        }
    }
}
