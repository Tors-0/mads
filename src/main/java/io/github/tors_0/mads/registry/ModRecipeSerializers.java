package io.github.tors_0.mads.registry;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.recipe.ArmedTippedShellRecipe;
import io.github.tors_0.mads.recipe.TippedShellRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipeSerializers {
    public static SpecialRecipeSerializer<TippedShellRecipe> TIPPED_SHELL;
    public static SpecialRecipeSerializer<ArmedTippedShellRecipe> ARM_TIPPED_SHELL;

    static {
        TIPPED_SHELL = Registry.register(Registries.RECIPE_SERIALIZER, Mads.getId("crafting_special_tipped_shell"), new SpecialRecipeSerializer<>(TippedShellRecipe::new));
        ARM_TIPPED_SHELL = Registry.register(Registries.RECIPE_SERIALIZER, Mads.getId("crafting_special_arm_tipped_shell"), new SpecialRecipeSerializer<>(ArmedTippedShellRecipe::new));
    }

    public static void registerRecipeSerializers() {}
}
