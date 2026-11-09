package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A charred or frozen remnant of a Dynamic Trees branch. It keeps the radius of the original branch and connects to
 * neighbouring remnants, so a tree hit by dragon breath keeps its shape. Connections are computed from the neighbours.
 * Remnants that lose their connection to the ground fall and vanish when they land, and they slowly decay over time.
 */
public class BlockElementalBranch extends BlockFalling {
    public static final PropertyInteger RADIUS = PropertyInteger.create("radius", 1, 8);
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    private static final int MAX_SUPPORT_SEARCH = 256;
    private static final float CHARCOAL_DROP_CHANCE = 0.05F;

    private static final Random RANDOM = new Random();
    private final boolean charred;

    public BlockElementalBranch(String gameName, String name, SoundType sound, float hardness, boolean charred) {
        super(Material.WOOD);
        this.charred = charred;
        this.setTranslationKey(name);
        this.setHarvestLevel("axe", 0);
        this.setHardness(hardness);
        this.setResistance(hardness * 3F);
        this.setSoundType(sound);
        this.setCreativeTab(null);
        this.setTickRandomly(true);
        this.setRegistryName(IceAndFire.MODID, gameName);
        this.setDefaultState(this.blockState.getBaseState().withProperty(RADIUS, 8)
                .withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false)
                .withProperty(WEST, false).withProperty(UP, false).withProperty(DOWN, false));
    }

    public static PropertyBool property(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case UP:
                return UP;
            default:
                return DOWN;
        }
    }

    public IBlockState withRadius(int radius) {
        return getDefaultState().withProperty(RADIUS, Math.max(1, Math.min(8, radius)));
    }

    /**
     * A remnant is supported if the connected mass of remnants it belongs to rests on the ground somewhere.
     */
    private boolean isSupported(World world, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            if (visited.size() > MAX_SUPPORT_SEARCH) {
                return true;
            }
            BlockPos current = queue.poll();
            BlockPos below = current.down();
            IBlockState belowState = world.getBlockState(below);
            if (belowState.getBlock() != this && belowState.isSideSolid(world, below, EnumFacing.UP)) {
                return true;
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos next = current.offset(facing);
                if (!visited.contains(next) && world.getBlockState(next).getBlock() == this) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote || isSupported(world, pos)) {
            return;
        }
        EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state.getActualState(world, pos));
        falling.shouldDropItem = false;
        world.spawnEntity(falling);
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (world.isRemote || random.nextDouble() >= IceAndFire.CONFIG.burntBranchDecayChance) {
            return;
        }
        // Decay starts at the tips: only blocks with at most one connection (counting the ground) wither,
        // so the tree wastes away from its outer twigs towards the trunk.
        IBlockState actual = state.getActualState(world, pos);
        int connections = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (actual.getValue(property(facing))) {
                connections++;
            }
        }
        if (connections > 1) {
            return;
        }
        // Each decay step thins the tip, and the last one removes it.
        // Removing a block notifies its neighbours, so anything left unsupported falls.
        int radius = state.getValue(RADIUS);
        if (radius <= 1) {
            world.setBlockToAir(pos);
        } else {
            world.setBlockState(pos, state.withProperty(RADIUS, radius - 1), 3);
        }
    }

    @Override
    public void onEndFalling(World world, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
        world.playEvent(2001, pos, Block.getStateId(fallingState));
        world.setBlockToAir(pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, RADIUS, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(RADIUS) - 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(RADIUS, (meta & 7) + 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            state = state.withProperty(property(facing), connectsTo(world, pos, facing));
        }
        return state;
    }

    private boolean connectsTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.offset(facing);
        IBlockState otherState = world.getBlockState(other);
        if (otherState.getBlock() == this) {
            return true;
        }
        // reach the ground so trunks don't hover above the soil
        return facing == EnumFacing.DOWN && otherState.isSideSolid(world, other, EnumFacing.UP);
    }

    private AxisAlignedBB coreBox(IBlockState state) {
        double r = state.getValue(RADIUS) / 16.0D;
        return new AxisAlignedBB(0.5D - r, 0.5D - r, 0.5D - r, 0.5D + r, 0.5D + r, 0.5D + r);
    }

    private AxisAlignedBB armBox(IBlockState state, EnumFacing facing) {
        double r = state.getValue(RADIUS) / 16.0D;
        double min = 0.5D - r;
        double max = 0.5D + r;
        double minX = min, minY = min, minZ = min, maxX = max, maxY = max, maxZ = max;
        switch (facing) {
            case NORTH:
                minZ = 0;
                maxZ = min;
                break;
            case SOUTH:
                minZ = max;
                maxZ = 1;
                break;
            case WEST:
                minX = 0;
                maxX = min;
                break;
            case EAST:
                minX = max;
                maxX = 1;
                break;
            case DOWN:
                minY = 0;
                maxY = min;
                break;
            default:
                minY = max;
                maxY = 1;
                break;
        }
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actual = state.getActualState(world, pos);
        AxisAlignedBB box = coreBox(actual);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (actual.getValue(property(facing))) {
                box = box.union(armBox(actual, facing));
            }
        }
        return box;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState actual = isActualState ? state : state.getActualState(world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, coreBox(actual));
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (actual.getValue(property(facing))) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, armBox(actual, facing));
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int sticks = state.getValue(RADIUS) / 2;
        if (sticks > 0) {
            drops.add(new ItemStack(Items.STICK, sticks));
        }
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        if (charred && rand.nextFloat() < CHARCOAL_DROP_CHANCE) {
            drops.add(new ItemStack(Items.COAL, 1, 1));
        }
    }
}
