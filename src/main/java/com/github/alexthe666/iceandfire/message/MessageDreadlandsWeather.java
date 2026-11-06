package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.world.dimension.WorldProviderDreadLands;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDreadlandsWeather extends AbstractMessage<MessageDreadlandsWeather> {
    private boolean queenDefeated;

    public MessageDreadlandsWeather() {
    }

    public MessageDreadlandsWeather(boolean queenDefeated) {
        this.queenDefeated = queenDefeated;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        queenDefeated = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeBoolean(queenDefeated);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageDreadlandsWeather message, EntityPlayer player, MessageContext context) {
        client.addScheduledTask(() -> {
            if (client.world != null && client.world.provider instanceof WorldProviderDreadLands) {
                ((WorldProviderDreadLands) client.world.provider).setQueenDefeated(message.queenDefeated);
            }
        });
    }

    @Override
    public void onServerReceived(MinecraftServer server, MessageDreadlandsWeather message, EntityPlayer player, MessageContext context) {
    }
}
