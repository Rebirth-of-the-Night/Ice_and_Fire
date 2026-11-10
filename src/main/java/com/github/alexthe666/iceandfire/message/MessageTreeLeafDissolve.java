package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.client.particle.ParticleLeafDissolve;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Tells clients which leaves of a converted tree just vanished, so they can show them dissolving
 * instead of popping out of existence. Positions are sent relative to an origin to keep the packet small.
 */
public class MessageTreeLeafDissolve extends AbstractMessage<MessageTreeLeafDissolve> {
    public static final int ELEMENT_FIRE = 0;
    public static final int ELEMENT_ICE = 1;
    public static final int ELEMENT_LIGHTNING = 2;

    private BlockPos origin = BlockPos.ORIGIN;
    private int element;
    private int[] palette = new int[0];
    private int[] dx = new int[0];
    private int[] dy = new int[0];
    private int[] dz = new int[0];
    private int[] paletteIndex = new int[0];
    private int[] distance = new int[0];

    public MessageTreeLeafDissolve() {
    }

    /**
     * @param leaves one entry per leaf: {dx, dy, dz, paletteIndex, distanceFromBranches}
     */
    public MessageTreeLeafDissolve(BlockPos origin, int element, int[] palette, List<int[]> leaves) {
        this.origin = origin;
        this.element = element;
        this.palette = palette;
        int count = leaves.size();
        dx = new int[count];
        dy = new int[count];
        dz = new int[count];
        paletteIndex = new int[count];
        distance = new int[count];
        for (int i = 0; i < count; i++) {
            int[] leaf = leaves.get(i);
            dx[i] = leaf[0];
            dy[i] = leaf[1];
            dz[i] = leaf[2];
            paletteIndex[i] = leaf[3];
            distance[i] = leaf[4];
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        origin = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        element = buf.readByte();
        palette = new int[buf.readUnsignedByte()];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = buf.readInt();
        }
        int count = buf.readUnsignedShort();
        dx = new int[count];
        dy = new int[count];
        dz = new int[count];
        paletteIndex = new int[count];
        distance = new int[count];
        for (int i = 0; i < count; i++) {
            dx[i] = buf.readShort();
            dy[i] = buf.readShort();
            dz[i] = buf.readShort();
            paletteIndex[i] = buf.readUnsignedByte();
            distance[i] = buf.readUnsignedByte();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(origin.getX());
        buf.writeInt(origin.getY());
        buf.writeInt(origin.getZ());
        buf.writeByte(element);
        buf.writeByte(palette.length);
        for (int state : palette) {
            buf.writeInt(state);
        }
        buf.writeShort(dx.length);
        for (int i = 0; i < dx.length; i++) {
            buf.writeShort(dx[i]);
            buf.writeShort(dy[i]);
            buf.writeShort(dz[i]);
            buf.writeByte(paletteIndex[i]);
            buf.writeByte(distance[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageTreeLeafDissolve message, EntityPlayer player, MessageContext messageContext) {
        if (client.world == null || message.element != ELEMENT_FIRE && message.element != ELEMENT_ICE && message.element != ELEMENT_LIGHTNING) {
            return;
        }
        IBlockState[] states = new IBlockState[message.palette.length];
        for (int i = 0; i < states.length; i++) {
            states[i] = Block.getStateById(message.palette[i]);
        }
        for (int i = 0; i < message.dx.length; i++) {
            BlockPos pos = message.origin.add(message.dx[i], message.dy[i], message.dz[i]);
            IBlockState state = states[message.paletteIndex[i]];
            client.effectRenderer.addEffect(new ParticleLeafDissolve(client.world, pos, state, message.distance[i], message.element));
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, MessageTreeLeafDissolve message, EntityPlayer player, MessageContext messageContext) {
    }
}
