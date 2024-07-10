package io.github.tors_0.mads.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.registry.ModBlockEntities;
import io.github.tors_0.mads.registry.ModItems;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MortarGuiDescription extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 2;
    private static final Identifier ANGLE_MESSAGE = Mads.getId("button_angle_change");
    private static final Identifier ANGLE_SERVER_MESSAGE = Mads.getId("button_angle_change_serv");
    private static final Identifier ROT_MESSAGE = Mads.getId("button_rot_update");
    private static final Identifier ROT_SERVER_MESSAGE = Mads.getId("button_rot_update_serv");

    private static final TextureIcon GUNPOW_ICON = new TextureIcon(Mads.getId("textures/gui/icon/gunpowder.png"));
    private static final TextureIcon SHELL_ICON = new TextureIcon(Mads.getId("textures/gui/icon/shell.png"));

    public MortarGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.MORTAR_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 2),
                getBlockPropertyDelegate(context, 4));

        /**
         * server use only
         */
        AtomicReference<BlockPos> blockPos = new AtomicReference<>();
        context.get((world, pos) -> {
            blockPos.set(pos);
            return Optional.empty();
        });

        // setup server- and client-side receivers for info
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(ANGLE_MESSAGE, data -> {
            int newAngle = data.readInt();
            this.propertyDelegate.set(3, newAngle); // modify server value
        });
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(ROT_MESSAGE, data -> {
            int newRot = data.readInt();
            this.propertyDelegate.set(2, newRot); // update server value
        });
        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(ANGLE_SERVER_MESSAGE, data -> {
            world.getBlockEntity(data.readBlockPos(), ModBlockEntities.MORTAR_BLOCK_ENTITY).get().getPropertyDelegate().set(3, data.readInt());
        });
        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(ROT_SERVER_MESSAGE, data -> {
            world.getBlockEntity(data.readBlockPos(), ModBlockEntities.MORTAR_BLOCK_ENTITY).get().getPropertyDelegate().set(2, data.readInt());
        });

        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(175,175);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        itemSlot.setIcon(SHELL_ICON);
        root.add(itemSlot, 72, 20);

        WItemSlot gunpowSlot = WItemSlot.of(blockInventory, 1);
        gunpowSlot.setInputFilter(itemStack -> itemStack.isOf(Items.GUNPOWDER));
        gunpowSlot.setIcon(GUNPOW_ICON);
        root.add(gunpowSlot, 72, 40);

        WSlider angleSlider = new WSlider(60, 85, Axis.VERTICAL);
        angleSlider.setValueChangeListener(val -> this.propertyDelegate.set(3, val));
        root.add(angleSlider, 32,40,8,35);

        WLabel angleLabel = new WLabel(Text.translatable("text.mads.angle"));
        angleLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(angleLabel, 0, 15, 72, 20);
        WDynamicLabel angle = new WDynamicLabel(() -> {
            angleSlider.setValue(propertyDelegate.get(3));
            return I18n.translate("label.mads.degrees", this.propertyDelegate.get(3));
        });
        angle.setAlignment(HorizontalAlignment.CENTER);
        root.add(angle, 0, 28, 72, 20);

        WSlider rotSlider = new WSlider(0, 359, Axis.HORIZONTAL);
        rotSlider.setValueChangeListener(val -> this.propertyDelegate.set(2, val));
        root.add(rotSlider, 100, 40, 52, 18);

        WLabel rotLabel = new WLabel(Text.translatable("text.mads.rot"));
        rotLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(rotLabel, 90, 15, 72, 20);
        WDynamicLabel rotation = new WDynamicLabel(() -> {
            rotSlider.setValue(this.propertyDelegate.get(2));
            return I18n.translate("label.mads.degrees", this.propertyDelegate.get(2));
        });
        rotation.setAlignment(HorizontalAlignment.CENTER);
        root.add(rotation, 90, 28,72,20);

        // angle buttons
//        WButton angleUp = new WButton(Text.literal("↑"));
//        angleUp.setOnClick(() -> {
//            int ang = this.propertyDelegate.get(3);
//            this.propertyDelegate.set(3, ang >= 85 ? 85 : ang + 1);
//        });
//        root.add(angleUp, 20,40, 15, 20);
//
//        WButton angleDown = new WButton(Text.literal("↓"));
//        angleDown.setOnClick(() -> {
//            int ang = this.propertyDelegate.get(3);
//            this.propertyDelegate.set(3, ang <= 60 ? ang : ang - 1);
//        });
//        root.add(angleDown, 38,40, 15, 20);

        // rotation buttons
//        WButton rotLeft = new WButton(Text.literal("←"));
//        rotLeft.setOnClick(() -> {
//            int rot = this.propertyDelegate.get(2);
//            this.propertyDelegate.set(2, rot - 10 < 0 ? 360 + rot - 10 : rot - 10);
//        });
//        root.add(rotLeft, 110,40, 15, 20);
//
//        WButton rotRight = new WButton(Text.literal("→"));
//        rotRight.setOnClick(() -> {
//            int rot = this.propertyDelegate.get(2);
//            this.propertyDelegate.set(2, rot + 10 >= 360 ? 0 : rot + 10);
//        });
//        root.add(rotRight, 128,40, 15, 20);

        WButton setButton = new WButton(Text.literal("✔"));
        setButton.setOnClick(() -> {
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ROT_MESSAGE, buf -> buf.writeInt(this.propertyDelegate.get(2)));
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ANGLE_MESSAGE, buf -> buf.writeInt(this.propertyDelegate.get(3)));
        });
        root.add(setButton, 148, 60, 15, 15);

        root.add(this.createPlayerInventoryPanel(), 0, 80);

        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
    }
}
