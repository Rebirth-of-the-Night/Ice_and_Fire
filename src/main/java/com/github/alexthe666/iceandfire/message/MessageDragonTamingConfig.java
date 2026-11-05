package com.github.alexthe666.iceandfire.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDragonTamingConfig extends AbstractMessage<MessageDragonTamingConfig> {
    public static boolean serverDisablesTaming;
    public static boolean serverDisablesAmphithereTaming;
    public static boolean serverDisablesHippogryphTaming;

    private boolean disabled;
    private boolean amphithereDisabled;
    private boolean hippogryphDisabled;
    public MessageDragonTamingConfig() {}

    public MessageDragonTamingConfig(boolean disabled, boolean amphithereDisabled, boolean hippogryphDisabled) {
        this.disabled = disabled;
        this.amphithereDisabled = amphithereDisabled;
        this.hippogryphDisabled = hippogryphDisabled;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        disabled = buf.readBoolean();
        amphithereDisabled = buf.readBoolean();
        hippogryphDisabled = buf.readBoolean();
    }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(disabled);
        buf.writeBoolean(amphithereDisabled);
        buf.writeBoolean(hippogryphDisabled);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageDragonTamingConfig message, EntityPlayer player, MessageContext context) {
        serverDisablesTaming = message.disabled;
        serverDisablesAmphithereTaming = message.amphithereDisabled;
        serverDisablesHippogryphTaming = message.hippogryphDisabled;
    }
    @Override
    public void onServerReceived(MinecraftServer server, MessageDragonTamingConfig message, EntityPlayer player, MessageContext context) {}
}
