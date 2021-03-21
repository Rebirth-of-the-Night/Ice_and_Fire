package com.github.alexthe666.iceandfire.recipe;

import net.minecraft.item.ItemStack;

public class DragonForgeRecipe {

    private final ItemStack input;
    private final ItemStack blood;
    private final ItemStack output;

    public DragonForgeRecipe(ItemStack input, ItemStack blood, ItemStack output) {
        this.input = input;
        this.blood = blood;
        this.output = output;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getBlood() {
        return blood;
    }

    public ItemStack getOutput() {
        return output;
    }
}
