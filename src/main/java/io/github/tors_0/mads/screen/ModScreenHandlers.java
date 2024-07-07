package io.github.tors_0.mads.screen;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.gui.MortarGuiDescription;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.feature_flags.FeatureFlags;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MortarGuiDescription> MORTAR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER_TYPE, Mads.getId("mortar"),
                    new ScreenHandlerType<>((syncId, inventory) -> new MortarGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY),
                            FeatureFlags.VANILLA_SET));

    public static void registerScreenHandlers() {}
}
