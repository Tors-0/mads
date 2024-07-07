package io.github.tors_0.mads.screen;

import io.github.tors_0.mads.Mads;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<MortarScreenHandler> MORTAR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER_TYPE, Mads.getId("mortar"),
                    new ExtendedScreenHandlerType<>(MortarScreenHandler::new));

    public static void registerScreenHandlers() {}
}
