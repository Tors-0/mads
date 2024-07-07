package io.github.tors_0.mads;

import io.github.tors_0.mads.screen.ModScreenHandlers;
import io.github.tors_0.mads.screen.MortarScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class MadsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        HandledScreens.register(ModScreenHandlers.MORTAR_SCREEN_HANDLER, MortarScreen::new);
    }
}
