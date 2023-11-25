package com.github.alexthe666.iceandfire.command;

import com.github.alexthe666.iceandfire.world.gen.processor.DreadCastleProcessor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

/**
 * @author Pabilo8
 * @since 27.06.2022
 */
public class CommandLoadCastle extends CommandBase {
    static final TemplateManager RES_LOC_TEMPLATE_MANAGER = new TemplateManager("Alex is a wanker", DataFixesManager.createFixer());

    @Override
    public String getName() {
        return "loadcastle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "loadcastle";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        server.sendMessage(new TextComponentString("it works!"));

        int totalW = 274, totalH = 130, totalD = 274;
        int partW = 32, partH = 32, partD = 32;
        String name = "castle";
        Entity senderEntity = sender.getCommandSenderEntity();

        if (senderEntity == null || server.getEntityWorld().isRemote)
            return;

        float blockReachDistance = 100f;
        Vec3d vec3d = senderEntity.getPositionEyes(0);
        Vec3d vec3d1 = senderEntity.getLook(0);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        RayTraceResult traceResult = senderEntity.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
        if (traceResult == null || traceResult.typeOfHit == RayTraceResult.Type.MISS)
            return;

        BlockPos origin = traceResult.getBlockPos().up();

        WorldServer worldserver = server.getWorld(sender.getEntityWorld().provider.getDimension());

        for (int x = 0, i = 0; x < totalW; x = Math.min(totalW, x + partW), i++)
            for (int y = 0, j = 0; y < totalH; y = Math.min(totalH, y + partH), j++)
                for (int z = 0, k = 0; z < totalD; z = Math.min(totalD, z + partD), k++) {
                    ResourceLocation res = new ResourceLocation("iceandfire:castle/" +name + "_" + i + "_" + j + "_" + k);
                    BlockPos pos = origin.add(x, y, z);
                    Template template = RES_LOC_TEMPLATE_MANAGER.getTemplate(null, res);

                    template.addBlocksToWorld(worldserver, pos, new PlacementSettings(), 0);
                }
    }
}
