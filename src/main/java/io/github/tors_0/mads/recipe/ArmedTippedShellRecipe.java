package io.github.tors_0.mads.recipe;

import com.google.common.collect.Lists;
import io.github.tors_0.mads.registry.ModItems;
import io.github.tors_0.mads.registry.ModRecipeSerializers;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class ArmedTippedShellRecipe extends SpecialCraftingRecipe {
    public ArmedTippedShellRecipe(Identifier identifier, CraftingCategory craftingCategory) {
        super(identifier, craftingCategory);
    }

    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < recipeInputInventory.size(); i++) {
            ItemStack itemStack = recipeInputInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack);
            }
        }

        return list.size() == 2 && list.stream().anyMatch(itemStack -> itemStack.isOf(ModItems.FUZE)) && list.stream().anyMatch(itemStack1 -> itemStack1.isOf(ModItems.TIPPED_SHELL));
    }

    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (int i = 0; i < recipeInputInventory.size(); i++) {
            ItemStack itemStack = recipeInputInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack);
            }
        }

        if (!(list.size() == 2 && list.stream().anyMatch(itemStack -> itemStack.isOf(ModItems.FUZE)) && list.stream().anyMatch(itemStack1 -> itemStack1.isOf(ModItems.TIPPED_SHELL)))) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemStack;
            if (list.get(0).isOf(ModItems.TIPPED_SHELL)) {
                itemStack = list.get(0);
            } else {
                itemStack = list.get(1);
            }
            ItemStack itemStack2 = new ItemStack(ModItems.ARMED_TIPPED_SHELL);
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
