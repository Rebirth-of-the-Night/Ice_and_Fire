package com.github.alexthe666.iceandfire.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

/**
 * @author Pabilo8
 * @since 27.06.2022
 */
public class CommandSaveCastle extends CommandBase {
    @Override
    public String getName() {
        return "savecastle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "savecastle";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        server.sendMessage(new TextComponentString("it works"));
        int totalW = 280, totalH = 130, totalD = 280;
        int partW = 32, partH = 32, partD = 32;
        String author = "Carver";
        String name = "Castle";
        boolean ignoreEntities = false;

        BlockPos origin = new BlockPos(-130, 1, 15-totalD);

        WorldServer worldserver = server.getWorld(sender.getEntityWorld().provider.getDimension());

        TemplateManager templatemanager = worldserver.getStructureTemplateManager();

        for (int x = 0, i = 0; x < totalW; x = Math.min(totalW, x + partW), i++)
            for (int y = 0, j = 0; y < totalH; y = Math.min(totalH, y + partH), j++)
                for (int z = 0, k = 0; z < totalD; z = Math.min(totalD, z + partD), k++) {
                    ResourceLocation res = new ResourceLocation(name + "_" + i + "_" + j + "_" + k);
                    BlockPos size = new BlockPos(
                            MathHelper.clamp(totalW - x, 0, partW),
                            MathHelper.clamp(totalH - y, 0, partH),
                            MathHelper.clamp(totalD - z, 0, partD)
                    );

                    BlockPos pos = origin.add(x, y, z);
                    Template template = templatemanager.getTemplate(server, res);
                    template.takeBlocksFromWorld(worldserver, pos, size, ignoreEntities, Blocks.STRUCTURE_VOID);
                    template.setAuthor(author);
                    templatemanager.writeTemplate(server, res);
                }
    }
}
