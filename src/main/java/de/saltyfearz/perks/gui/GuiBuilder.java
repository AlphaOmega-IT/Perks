package de.saltyfearz.perks.gui;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import perk.BasePerk;

import java.util.HashMap;
import java.util.Map;

public class GuiBuilder {

    private String title;
    private InventoryType invType = InventoryType.CHEST;
    private Integer rows;
    private final Map<Integer, GuiItem> items = new HashMap<>();
    private final Map<Integer, Map<String, GuiItem>> itemPerks = new HashMap<>();
    private final Map<Player, Inventory> openInventories = new HashMap<>();

    public GuiBuilder withTitle(final String title) {
        this.title = title;
        return this;
    }

    public GuiBuilder withRows(final Integer rows) {
        this.rows = rows;
        return this;
    }

    public GuiBuilder withType(final InventoryType invType) {
        this.invType = invType;
        return this;
    }

    public GuiBuilder withItemPerk(final int x, final int y, final GuiItem item, final String perkName) {
        return withItemPerk(x + y * 9, item, perkName);
    }

    public GuiBuilder withItem(final int slot, final GuiItem item) {
        this.items.put(slot, item);
        return this;
    }

    public GuiBuilder withItemPerk(int slot, final GuiItem item, final String perkName) {
        Map<String, GuiItem> perkItems = new HashMap<>();
        perkItems.put(perkName, item);
        this.itemPerks.put(slot, perkItems);
        this.items.put(slot, item);
        return this;
    }

    public GuiBuilder updateItem(int x, int y, final ItemStack item) {
        return updateItem(x + y * 9, new GuiItem(item));
    }

    public GuiBuilder updateItem(int x, int y, GuiItem item) {
        return updateItem(x + y * 9, item);
    }

    public GuiBuilder updateItem(int slot, final GuiItem item) {
        this.items.put(slot, item);
        for (Map.Entry<Player, Inventory> entry : this.openInventories.entrySet()) {
            entry.getValue().setItem(slot, item.getItemStack());
            entry.getKey().updateInventory();
        }
        return this;
    }

    public GuiBuilder getThis() {
        return this;
    }

    public void open(final Player player, final Plugin plugin) {
        Inventory inv;
        if (this.invType != InventoryType.CHEST) {
            inv = Bukkit.createInventory(player, this.invType, (this.title == null) ? this.invType.getDefaultTitle() : this.title);
        } else {
            inv = Bukkit.createInventory(player, this.rows * 9, (this.title == null) ? this.invType.getDefaultTitle() : this.title);
        }
        player.openInventory(inv);
        this.items.forEach((slot, item) -> inv.setItem(slot, item.getItemStack()));
        player.updateInventory();
        registerHandler(plugin, player, inv);
    }

    private void registerHandler(final Plugin plugin, final Player player, final Inventory inv) {
        Bukkit.getPluginManager().registerEvents(new InventoryHandler(inv, player), plugin);
    }

    private class InventoryHandler implements Listener {
        private final Inventory inv;
        private final Player player;

        private InventoryHandler(final Inventory inv, final Player player) {
            this.inv = inv;
            this.player = player;
        }

        @EventHandler
        public void onInventoryClick(final InventoryClickEvent event) {
            if (!event.getView().getTitle().equalsIgnoreCase(Constants.INV_TITLE))
                return;
            Map<Player, Integer> pages = Perks.getPages();
            GuiItem item = GuiBuilder.this.items.get(event.getSlot());
            Map<String, GuiItem> itemPerks = GuiBuilder.this.itemPerks.get(event.getSlot());
            if (item != null) {
                event.setCancelled(item.callAction(Perks.getInstance(), GuiBuilder.this.getThis(), event));
                ItemMeta iM = item.getItemStack().getItemMeta();
                if (iM != null) {
                    if (iM.getDisplayName().equals(Constants.PAGE_2_BUTTON)) {
                        pages.putIfAbsent(player, Constants.PAGE_2);
                    } else if (iM.getDisplayName().equals(Constants.PAGE_1_BUTTON)) {
                        pages.putIfAbsent(player, Constants.PAGE_1);
                    } if (!GuiBuilder.this.itemPerks.isEmpty()) {
                        Map<String, GuiItem> items = new HashMap<>();
                        if (iM.getDisplayName().equals(Constants.DEACTIVATE)) {
                            GuiItem guiItem = GuiBuilder.this.itemPerks.get(event.getSlot()).get(itemPerks.keySet().stream().findFirst().get());
                            guiItem.withItemMeta(Constants.ACTIVATE);
                            guiItem.getItemStack().setType(Material.LIME_DYE);
                            items.put(iM.getDisplayName().substring(2), guiItem);
                            GuiBuilder.this.items.put(event.getSlot(), guiItem);
                            GuiBuilder.this.itemPerks.replace(event.getSlot(), items);
                            changeInteractButtons(player, itemPerks);
                        } else if (iM.getDisplayName().equals(Constants.ACTIVATE)) {
                            GuiItem guiItem = GuiBuilder.this.itemPerks.get(event.getSlot()).get(itemPerks.keySet().stream().findFirst().get());
                            guiItem.withItemMeta(Constants.DEACTIVATE);
                            guiItem.getItemStack().setType(Material.GRAY_DYE);
                            items.put(iM.getDisplayName().substring(2), guiItem);
                            GuiBuilder.this.items.put(event.getSlot(), guiItem);
                            GuiBuilder.this.itemPerks.replace(event.getSlot(), items);
                            changeInteractButtons(player, itemPerks);
                        } else if (iM.getDisplayName().equals(Constants.NOT_UNLOCKED)) {
                            player.sendMessage(Constants.PREFIX + Constants.NOT_UNLOCKED_PERK_YET.replace("(?)", GuiBuilder.this.itemPerks.get(event.getSlot()).keySet().stream().findFirst().get()));
                        }
                    }
                    Perks.getPages().put(player, pages.get(player));
                    getThis().open(player, Perks.getInstance());
                }
            }
        }

        @EventHandler
        public void onInventoryClose(final InventoryCloseEvent event) {
            if (!this.inv.equals(event.getInventory()))
                return;
            unregisterHandler();
        }

        private void unregisterHandler() {
            HandlerList.unregisterAll(this);
            GuiBuilder.this.openInventories.remove(this.player);
        }

        private void changeInteractButtons(final Player player, final Map<String, GuiItem> itemPerks) {
            Map<Player, Map<BasePerk, Boolean>> activatedPerks = Perks.getActivatedPerks();
            Map<BasePerk, Boolean> newPerks = activatedPerks.get(player);
            if (newPerks == null)
                return;
            newPerks.computeIfPresent(Perks.getBasePerkProvider().getPerkByName(itemPerks.keySet().stream().findFirst().get()), ((basePerk, aBoolean) -> !aBoolean));
            activatedPerks.replace(player, activatedPerks.get(player), newPerks);
            Perks.getActivatedPerks().replace(player, activatedPerks.get(player));
            Perks.getBasePerkProvider().savePerkInteractions(player);
        }
    }
}
