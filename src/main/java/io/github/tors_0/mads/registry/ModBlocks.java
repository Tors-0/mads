package io.github.tors_0.mads.registry;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.block.AmmoCrateBlock;
import io.github.tors_0.mads.block.MortarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModBlocks {
    Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

    Block MORTAR = createBlock("mortar", new MortarBlock(QuiltBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()), true);
    Block AMMO_CRATE = createBlock("ammo_crate", new AmmoCrateBlock(QuiltBlockSettings.copyOf(Blocks.COMPOSTER).nonOpaque()), true);

    static void registerBlocks() {
        BLOCKS.keySet().forEach(block -> {
            Registry.register(Registries.BLOCK, BLOCKS.get(block), block);
        });
    }

    private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(Mads.ID, name));
        if (createItem) {
            ModItems.ITEMS.put(new BlockItem(block, new QuiltItemSettings()), BLOCKS.get(block));
        }
        return block;
    }
}
