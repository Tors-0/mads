package io.github.tors_0.mads.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.tors_0.mads.gui.AmmoCrateGuiDescription;
import io.github.tors_0.mads.gui.MortarGuiDescription;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class AmmoCrateScreen extends CottonInventoryScreen<AmmoCrateGuiDescription> {
    public AmmoCrateScreen(AmmoCrateGuiDescription gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
