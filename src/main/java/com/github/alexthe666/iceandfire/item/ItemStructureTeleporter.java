package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.event.StructureSearch;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemStructureTeleporter extends ItemGeneric {
    private static final int COOLDOWN_TICKS = 20;
    private final LocatableStructure structure;

    public ItemStructureTeleporter(LocatableStructure structure) {
        super(structure.getId() + "_teleporter", "iceandfire." + structure.getId() + "_teleporter");
        this.structure = structure;
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + I18n.format("item.iceandfire.structure_teleporter.tooltip"));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("item.iceandfire.structure_teleporter.tooltip.sneak"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (!(player instanceof EntityPlayerMP) || player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("message.iceandfire.structure_teleporter.creative_only"));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        boolean started = StructureSearch.start((EntityPlayerMP) player, structure, player.isSneaking());
        return new ActionResult<>(started ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }
}
