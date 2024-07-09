package io.github.tors_0.mads;

import io.github.tors_0.mads.registry.*;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mads implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MADS");

    public static final String ID = "mads";

    public static Identifier getId(String name) {
        return new Identifier(ID, name);
    }

    @Override
    public void onInitialize(ModContainer mod) {
        LOGGER.info("Hello Quilt world from {}! Stay fresh!", mod.metadata().name());

        ModBlocks.registerBlocks();
        ModItems.registerItemsAndBlockItems();

        ModEntities.registerEntityTypes();
        ModBlockEntities.registerBlockEntities();

        ModScreenHandlers.registerScreenHandlers();

        ModEventCallbacks.registerCallbacks();
    }
}