package io.github.tors_0.mads.registry;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.item.ShellItem;
import io.github.tors_0.mads.item.TippedShellItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    Item EMPTY_SHELL = createItem("empty_shell", new Item(new QuiltItemSettings()));
    Item SHELL = createItem("shell", new ShellItem(new QuiltItemSettings().maxCount(16)));
    Item TIPPED_SHELL = createItem("tipped_shell", new TippedShellItem(new QuiltItemSettings().maxCount(16)));

    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier(Mads.ID, name));
        return item;
    }

    static void registerItemsAndBlockItems() {
        ITEMS.keySet().forEach(item -> {
            Registry.register(Registries.ITEM, ITEMS.get(item), item);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> ITEMS.keySet().forEach(entries::addItem));
    }
}
