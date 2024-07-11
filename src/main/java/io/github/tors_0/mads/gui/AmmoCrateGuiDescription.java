package io.github.tors_0.mads.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.tors_0.mads.item.MortarProjectile;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;

public class AmmoCrateGuiDescription extends SyncedGuiDescription {
    public AmmoCrateGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.AMMO_CRATE_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 16), getBlockPropertyDelegate(context, 0));

        WGridPanel root = new WGridPanel(9);
        root.setInsets(Insets.ROOT_PANEL);
        this.setRootPanel(root);

        WItemSlot blockInventorySlot = new WItemSlot(this.blockInventory, 0, 4, 4, false);
        blockInventorySlot.setInputFilter(itemStack -> itemStack.getItem() instanceof MortarProjectile proj && proj.isArmed());
        root.add(blockInventorySlot, 5, 1);

        blockInventorySlot.addChangeListener(new WItemSlot.ChangeListener() {
            @Override
            public void onStackChanged(WItemSlot slot, Inventory inventory, int index, ItemStack stack) {

            }
        });

        root.add(this.createPlayerInventoryPanel(), 0, 10);

        root.validate(this);
    }
}
