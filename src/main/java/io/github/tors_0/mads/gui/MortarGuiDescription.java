package io.github.tors_0.mads.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class MortarGuiDescription extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;

    public MortarGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
        super(type, syncId, playerInventory);
    }
}
