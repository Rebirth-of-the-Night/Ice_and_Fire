package com.github.alexthe666.iceandfire.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * @author Pabilo8
 * @since 27.06.2022
 */
public class CommandTreeBaseButOurs extends CommandTreeBase {

    public CommandTreeBaseButOurs() {
        addSubcommand(new CommandSaveCastle());
        addSubcommand(new CommandLoadCastle());
    }

    @Override
    public String getName() {
        return "stakan";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "do not";
    }
}
