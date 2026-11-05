package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.client.entity.EntityFishingSalmon;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageFishingRipple extends AbstractMessage<MessageFishingRipple> {
    private int dimension;
    private int id;
    private double x;
    private double y;
    private double z;
    private boolean removed;

    public MessageFishingRipple() {}

    public MessageFishingRipple(int dimension, int id, double x, double y, double z, boolean removed) {
        this.dimension = dimension;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.removed = removed;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageFishingRipple message, EntityPlayer player, MessageContext context) {
        client.addScheduledTask(() -> {
            if (client.world != null && client.player != null && client.world.provider.getDimension() == message.dimension) {
                EntityFishingSalmon.updateTarget(client.world, message.id, message.x, message.y, message.z, message.removed);
            }
        });
    }

    @Override
    public void onServerReceived(MinecraftServer server, MessageFishingRipple message, EntityPlayer player, MessageContext context) {}

    @Override
    public void fromBytes(ByteBuf buffer) {
        dimension = buffer.readInt();
        id = buffer.readInt();
        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
        removed = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(dimension);
        buffer.writeInt(id);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeBoolean(removed);
    }
}
