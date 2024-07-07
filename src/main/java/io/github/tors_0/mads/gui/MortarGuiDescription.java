package io.github.tors_0.mads.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public class MortarGuiDescription extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;

    public MortarGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.MORTAR_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 4));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(175,175);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 4, 1);

        WDynamicLabel angleLabel = new WDynamicLabel(() -> I18n.translate("text.mads.angle", propertyDelegate.get(3)));
        root.add(angleLabel, 0, 1);
        WDynamicLabel rotLabel = new WDynamicLabel(() -> I18n.translate("text.mads.rot", propertyDelegate.get(2)));
        root.add(angleLabel, 5, 1);

        // angle buttons
        WButton angleUp = new WButton(Text.literal("\u1403"));
        angleUp.setOnClick(() -> {
            int ang = propertyDelegate.get(3);
            propertyDelegate.set(3, ang >= 89 ? ang : ang + 1);
        });
        root.add(angleUp, 1,2);

        WButton angleDown = new WButton(Text.literal("\u1401"));
        angleDown.setOnClick(() -> {
            int ang = propertyDelegate.get(3);
            propertyDelegate.set(3, ang <= 75 ? ang : ang - 1);
        });
        root.add(angleDown, 2,2);

        // rotation buttons
        WButton rotLeft = new WButton(Text.literal("\u140A"));
        angleUp.setOnClick(() -> {
            int rot = propertyDelegate.get(2);
            propertyDelegate.set(2, rot - 1);
        });
        root.add(angleUp, 6,2);

        WButton rotRight = new WButton(Text.literal("\u1405"));
        angleDown.setOnClick(() -> {
            int rot = propertyDelegate.get(2);
            propertyDelegate.set(2, rot + 1);
        });
        root.add(angleDown, 7,2);

        root.add(this.createPlayerInventoryPanel(), 0, 4);

        root.validate(this);
    }
}
