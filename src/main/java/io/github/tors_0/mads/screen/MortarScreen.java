package io.github.tors_0.mads.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tors_0.mads.Mads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MortarScreen extends HandledScreen<MortarScreenHandler> {
    private static final Identifier TEXTURE = Mads.getId("textures/gui/mortar_gui.png");

    public MortarScreen(MortarScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        graphics.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (handler.isAngleAtTop()) {
            graphics.drawTexture(TEXTURE, x + 28, y + 34, 197, 90, 15, 10);
        } else {
            graphics.drawTexture(TEXTURE, x + 28, y + 34, 197, 66, 15, 10);
        }

        if (handler.isAngleAtBottom()) {
            graphics.drawTexture(TEXTURE, x + 28, y + 46, 178, 90, 15, 10);
        } else {
            graphics.drawTexture(TEXTURE, x + 28, y + 46, 178, 66, 15, 10);
        }

        graphics.drawTexture(TEXTURE, x + 126, y + 34, 177, 48, 10, 15);
        graphics.drawTexture(TEXTURE, x + 138, y + 34, 177, 29, 10, 15);

        graphics.drawCenteredShadowedText(textRenderer, "Angle: " + handler.getAngle(), x + 36, y + 24, 16184820);
        graphics.drawCenteredShadowedText(textRenderer, "Rotation: " + handler.getRot(), x + 137, y + 24, 16184820);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        drawMouseoverTooltip(graphics, mouseX, mouseY);
    }
}
