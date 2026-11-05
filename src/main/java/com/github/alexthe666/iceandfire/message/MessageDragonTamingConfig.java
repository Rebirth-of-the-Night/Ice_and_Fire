package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFireConfig;
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
    public static boolean serverDisablesCockatriceTaming;
    public static boolean serverDisablesDeathWormTaming;
    public static boolean serverDisablesHippocampusTaming;
    public static boolean serverDisablesPixieTaming;

    private boolean disabled;
    private boolean amphithereDisabled;
    private boolean hippogryphDisabled;
    private boolean cockatriceDisabled;
    private boolean deathWormDisabled;
    private boolean hippocampusDisabled;
    private boolean pixieDisabled;
    public MessageDragonTamingConfig() {}

    public MessageDragonTamingConfig(IceAndFireConfig config) {
        this.disabled = config.disableDragonTaming;
        this.amphithereDisabled = config.disableAmphithereTaming;
        this.hippogryphDisabled = config.disableHippogryphTaming;
        this.cockatriceDisabled = config.disableCockatriceTaming;
        this.deathWormDisabled = config.disableDeathWormTaming;
        this.hippocampusDisabled = config.disableHippocampusTaming;
        this.pixieDisabled = config.disablePixieTaming;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        disabled = buf.readBoolean();
        amphithereDisabled = buf.readBoolean();
        hippogryphDisabled = buf.readBoolean();
        cockatriceDisabled = buf.readBoolean();
        deathWormDisabled = buf.readBoolean();
        hippocampusDisabled = buf.readBoolean();
        pixieDisabled = buf.readBoolean();
    }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(disabled);
        buf.writeBoolean(amphithereDisabled);
        buf.writeBoolean(hippogryphDisabled);
        buf.writeBoolean(cockatriceDisabled);
        buf.writeBoolean(deathWormDisabled);
        buf.writeBoolean(hippocampusDisabled);
        buf.writeBoolean(pixieDisabled);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageDragonTamingConfig message, EntityPlayer player, MessageContext context) {
        serverDisablesTaming = message.disabled;
        serverDisablesAmphithereTaming = message.amphithereDisabled;
        serverDisablesHippogryphTaming = message.hippogryphDisabled;
        serverDisablesCockatriceTaming = message.cockatriceDisabled;
        serverDisablesDeathWormTaming = message.deathWormDisabled;
        serverDisablesHippocampusTaming = message.hippocampusDisabled;
        serverDisablesPixieTaming = message.pixieDisabled;
    }
    @Override
    public void onServerReceived(MinecraftServer server, MessageDragonTamingConfig message, EntityPlayer player, MessageContext context) {}
}
