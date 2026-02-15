package com.github.alexthe666.iceandfire.recipe;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.enums.EnumBloodedDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class BloodRecipes {

    public static void init() {
        registerRecipes();
    }

    private static void registerRecipes() {
        int recipeIndex = 0;

        for (EnumDragonArmor dragonArmor : EnumDragonArmor.values()) {
            for (EnumBloodedDragonArmor output : EnumBloodedDragonArmor.values())
                if (dragonArmor.eggType == output.eggType) {
                    recipeIndex = registerArmorSet(dragonArmor, recipeIndex, output);
                }
        }
    }

    private static int registerArmorSet(EnumDragonArmor dragonArmor, int startIndex, EnumBloodedDragonArmor output) {
        int index = startIndex;

        Item bloodItem;

        if (dragonArmor.eggType.dragonType == DragonType.ICE) {
            bloodItem = IafItemRegistry.ice_dragon_blood;
        } else if (dragonArmor.eggType.dragonType == DragonType.LIGHTNING) {
            bloodItem = IafItemRegistry.lightning_dragon_blood;
        } else {
            bloodItem = IafItemRegistry.fire_dragon_blood;
        }

        index = addRecipe(dragonArmor.helmet, bloodItem, output.helmet, index);
        index = addRecipe(dragonArmor.chestplate, bloodItem, output.chestplate, index);
        index = addRecipe(dragonArmor.leggings, bloodItem, output.leggings, index);
        index = addRecipe(dragonArmor.boots, bloodItem, output.boots, index);


        return index;
    }

    private static int addRecipe(Item dragonArmor, Item blood, Item output, int index) {
        ResourceLocation recipeName = new ResourceLocation(IceAndFire.MODID, "blood_dragon_armor_" + index);

        IRecipe recipe = new ShapelessOreRecipe(
                new ResourceLocation(""),
                new ItemStack(output),
                Ingredient.fromStacks(new ItemStack(dragonArmor)),
                Ingredient.fromStacks(new ItemStack(blood))
        ).setRegistryName(recipeName);

        GameRegistry.findRegistry(IRecipe.class).register(recipe);

        return index + 1;
    }
}