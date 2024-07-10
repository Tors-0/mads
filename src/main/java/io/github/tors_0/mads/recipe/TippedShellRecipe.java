package io.github.tors_0.mads.recipe;

import io.github.tors_0.mads.registry.ModItems;
import io.github.tors_0.mads.registry.ModRecipeSerializers;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TippedShellRecipe extends SpecialCraftingRecipe {
    public TippedShellRecipe(Identifier identifier, CraftingCategory craftingCategory) {
        super(identifier, craftingCategory);
    }

    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        if (recipeInputInventory.getWidth() == 3 && recipeInputInventory.getHeight() == 3) {
            for (int i = 0; i < recipeInputInventory.getWidth(); i++) {
                for (int j = 0; j < recipeInputInventory.getHeight(); j++) {
                    ItemStack itemStack = recipeInputInventory.getStack(i + j * recipeInputInventory.getWidth());
                    if (itemStack.isEmpty()) {
                        return false;
                    }

                    if (i == 1 && j == 1) {
                        if (!itemStack.isOf(Items.LINGERING_POTION)) {
                            return false;
                        }
                    } else if (!itemStack.isOf(ModItems.EMPTY_SHELL)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack itemStack = recipeInputInventory.getStack(1 + recipeInputInventory.getWidth());
        if (!itemStack.isOf(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemStack2 = new ItemStack(ModItems.TIPPED_SHELL, 8);
            PotionUtil.setPotion(itemStack2, PotionUtil.getPotion(itemStack));
            PotionUtil.setCustomPotionEffects(itemStack2, PotionUtil.getCustomPotionEffects(itemStack));
            return itemStack2;
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.TIPPED_SHELL;
    }
}
