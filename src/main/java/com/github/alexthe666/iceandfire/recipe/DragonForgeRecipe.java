package com.github.alexthe666.iceandfire.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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





    public void smelt(NonNullList<ItemStack> forge) {
        ItemStack input = forge.get(0);
        ItemStack blood = forge.get(1);
        ItemStack output = forge.get(2);
        smelt(forge, input, blood, output);
    }

    public void smelt(NonNullList<ItemStack> forge, ItemStack input, ItemStack blood, ItemStack output) {
        if (output.isEmpty()) {
            ItemStack stack = getOutput().copy();
            if (input.getCount() == 1 && getOutput().getCount() == 1) {
                stack.setStackDisplayName(input.getDisplayName());
                stack.setItemDamage(input.getItemDamage());
                stack.setRepairCost(input.getRepairCost());
                stack.setTagCompound(input.getTagCompound());
            }
            forge.set(2, stack);
        } else {
            output.grow(getOutput().getCount());
        }
        input.shrink(getInput().getCount());
        blood.shrink(getBlood().getCount());
    }
}
