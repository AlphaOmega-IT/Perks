package de.saltyfearz.perks.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class GuiItem {

    private final ItemStack iS;
    private ItemMeta iM;
    private List<String> lore = new ArrayList<>();
    private GuiItemAction iAction;
    private boolean asyncFunction = false;

    public GuiItem(final ItemStack iS) {
        this.iS = iS;
    }

    public GuiItem(final Material m) {
        this.iS = new ItemStack(m);
    }

    public GuiItem withItemMeta(final String name) {
        this.iM = this.iS.getItemMeta();
        if (this.iM != null)
            this.iM.setDisplayName(name);
        return this;
    }

    public GuiItem withItemLore(final List<String> lore, final boolean hasNewLore) {
        if (hasNewLore)
            this.lore = lore;
        else
            this.lore.addAll(lore);
        return this;
    }

    public GuiItem withAction(final GuiItemAction action) {
        this.iAction = action;
        return this;
    }

    public GuiItem withAsyncAction(final GuiItemAction action) {
        this.iAction = action;
        this.asyncFunction = true;
        return this;
    }

    public ItemStack getItemStack() {
        this.iM.setLore(lore);
        this.iS.setItemMeta(iM);
        return iS;
    }

    public GuiItemAction getIAction() { return iAction; }

    protected boolean callAction(final Plugin plugin, final GuiBuilder guiBuilder, final InventoryClickEvent event) {
        if (this.iAction != null) {
            if (this.asyncFunction)
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> this.iAction.clicked(guiBuilder, event));
            else
                return this.iAction.clicked(guiBuilder, event);
        }
        return true;
    }
}
