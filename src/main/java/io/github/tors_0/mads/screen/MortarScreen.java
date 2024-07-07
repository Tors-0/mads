package io.github.tors_0.mads.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.gui.MortarGuiDescription;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MortarScreen extends CottonInventoryScreen<MortarGuiDescription> {
    public MortarScreen(MortarGuiDescription gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
