package com.github.alexthe666.iceandfire.compat.jei.lightningdragonforge;

import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LightningDragonForgeRecipeWrapper implements IRecipeWrapper {

    private DragonForgeRecipe recipe;

    public LightningDragonForgeRecipeWrapper(DragonForgeRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(recipe.getInput());
        stacks.add(recipe.getBlood());
        ingredients.setInputs(ItemStack.class, stacks);
        ingredients.setOutput(ItemStack.class, recipe.getOutput());
    }

    public DragonForgeRecipe getRecipe() {
        return recipe;
    }
}
