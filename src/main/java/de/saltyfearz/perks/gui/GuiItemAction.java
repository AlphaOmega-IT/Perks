package de.saltyfearz.perks.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface GuiItemAction {
    boolean clicked(final GuiBuilder guiBuilder, final InventoryClickEvent event);
}
