package com.github.alexthe666.iceandfire.recipe;

import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class LecternRecipeFactory implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
        ShapedPrimer shape = new ShapedPrimer();
        shape.width = recipe.getRecipeWidth();
        shape.height = recipe.getRecipeHeight();
        shape.input = recipe.getIngredients();
        shape.mirrored = JsonUtils.getBoolean(json, "mirrored", true);

        String group = recipe.getGroup();
        return new ShapedOreRecipe(group.isEmpty() ? null : new ResourceLocation(group), recipe.getRecipeOutput(), shape) {
            @Override
            @Nonnull
            public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inventory) {
                NonNullList<ItemStack> remaining = super.getRemainingItems(inventory);
                for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                    ItemStack ingredient = inventory.getStackInSlot(slot);
                    if (ingredient.getItem() == Items.BOOK) {
                        ItemStack book = ingredient.copy();
                        book.setCount(1);
                        remaining.set(slot, book);
                    }
                }
                return remaining;
            }
        };
    }
}
