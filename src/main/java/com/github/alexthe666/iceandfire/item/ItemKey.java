package com.github.alexthe666.iceandfire.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Pabilo8
 * @since 15.06.2022
 */
public class ItemKey extends ItemGeneric {

    private final TextFormatting color;

    public ItemKey(String gameName, String name, TextFormatting color) {
        super(gameName, name);
        this.color = color;
    }

    public ItemKey(String gameName, String name, int textLength, TextFormatting color) {
        super(gameName, name, textLength);
        this.color = color;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return color + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
    }
}
