package io.github.tors_0.mads.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.registry.ModBlockEntities;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MortarGuiDescription extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;
    private static final Identifier ANGLE_MESSAGE = Mads.getId("button_angle_change");
    private static final Identifier ANGLE_SERVER_MESSAGE = Mads.getId("button_angle_change_serv");
    private static final Identifier ROT_MESSAGE = Mads.getId("button_rot_update");
    private static final Identifier ROT_SERVER_MESSAGE = Mads.getId("button_rot_update_serv");

    public MortarGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.MORTAR_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 1),
                getBlockPropertyDelegate(context, 4));

        /**
         * server only
         */
        AtomicReference<BlockPos> blockPos = new AtomicReference<>();
        context.get((world, pos) -> {
            blockPos.set(pos);
            return Optional.empty();
        });

        this.addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {

            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
                handler.updateToClient();
            }
        });

        // setup server- and client-side receivers for info
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(ANGLE_MESSAGE, data -> {
            int deltaAngle = data.readInt();
            int prevAngle = this.propertyDelegate.get(3);
            this.propertyDelegate.set(3, deltaAngle + prevAngle); // modify server value

            ScreenNetworking.of(this, NetworkSide.SERVER).send(ANGLE_SERVER_MESSAGE,
                    packetByteBuf -> {
                        packetByteBuf.writeInt(prevAngle + deltaAngle);
                        packetByteBuf.writeBlockPos(blockPos.get());
                    }); // inform clients of updated value
        });
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(ROT_MESSAGE, data -> {
            int newRot = data.readInt();
            this.propertyDelegate.set(2, newRot); // update server value

            ScreenNetworking.of(this, NetworkSide.SERVER).send(ROT_SERVER_MESSAGE,
                    packetByteBuf -> {
                        packetByteBuf.writeInt(newRot);
                        packetByteBuf.writeBlockPos(blockPos.get());
                    }); // inform clients of updated value
        });
        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(ANGLE_SERVER_MESSAGE, data -> {
            int newAngle = data.readInt();
            BlockPos blockPos1 = data.readBlockPos();

            world.getBlockEntity(blockPos1, ModBlockEntities.MORTAR_BLOCK_ENTITY).get().getPropertyDelegate().set(3, newAngle);
        });
        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(ROT_SERVER_MESSAGE, data -> {
            int newRot = data.readInt();
            BlockPos blockPos1 = data.readBlockPos();

            world.getBlockEntity(blockPos1, ModBlockEntities.MORTAR_BLOCK_ENTITY).get().getPropertyDelegate().set(2, newRot);
        });

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(175,175);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 4, 1);

        WDynamicLabel angleLabel = new WDynamicLabel(() -> I18n.translate("text.mads.angle", this.propertyDelegate.get(3)));
        angleLabel.setAlignment(HorizontalAlignment.CENTER);
        root.add(angleLabel, 0, 1, 4, 1);
        WDynamicLabel rotLabel = new WDynamicLabel(() -> I18n.translate("text.mads.rot", this.propertyDelegate.get(2)));
        rotLabel.setAlignment(HorizontalAlignment.CENTER);
        root.add(rotLabel, 5, 1,4,1);

        // angle buttons
        WButton angleUp = new WButton(Text.literal("↑"));
        angleUp.setOnClick(() -> {
            int ang = this.propertyDelegate.get(3);
            this.propertyDelegate.set(3, ang >= 85 ? 85 : ang + 1);
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ANGLE_MESSAGE, buf -> buf.writeInt(ang >= 85 ? 0 : 1));
        });
        root.add(angleUp, 1,2, 1, 1);

        WButton angleDown = new WButton(Text.literal("↓"));
        angleDown.setOnClick(() -> {
            int ang = this.propertyDelegate.get(3);
            this.propertyDelegate.set(3, ang <= 75 ? ang : ang - 1);
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ANGLE_MESSAGE, buf -> buf.writeInt(ang <= 75 ? 0 : -1));
        });
        root.add(angleDown, 2,2, 1, 1);

        // rotation buttons
        WButton rotLeft = new WButton(Text.literal("←"));
        rotLeft.setOnClick(() -> {
            int rot = this.propertyDelegate.get(2);
            this.propertyDelegate.set(2, rot - 1 <= 0 ? 359 : rot - 1);
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ROT_MESSAGE, buf -> buf.writeInt(rot - 1 <= 0 ? 359 : rot - 1));
        });
        root.add(rotLeft, 6,2, 1, 1);

        WButton rotRight = new WButton(Text.literal("→"));
        rotRight.setOnClick(() -> {
            int rot = this.propertyDelegate.get(2);
            this.propertyDelegate.set(2, rot + 1 >= 360 ? 0 : rot + 1);
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(ROT_MESSAGE, buf -> buf.writeInt(rot + 1 >= 360 ? 0 : rot + 1));
        });
        root.add(rotRight, 7,2, 1, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 4);

        root.validate(this);
    }
}
