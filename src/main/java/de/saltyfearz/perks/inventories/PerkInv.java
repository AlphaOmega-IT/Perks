package de.saltyfearz.perks.inventories;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.gui.GuiBuilder;
import de.saltyfearz.perks.gui.GuiItem;
import de.saltyfearz.perks.utils.Constants;
import de.saltyfearz.perks.utils.SkullCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import perk.BasePerk;
import perk.BasePerkProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PerkInv {

    private final static BasePerkProvider perkProvider = new BasePerkProvider(Perks.getSQLConnection());

    private static final Collection<BasePerk> perks = Perks.getAllPerks();

    public static void perkInv(final Player player) {
        GuiBuilder guiBuilder1 = new GuiBuilder();
        guiBuilder1.withType(InventoryType.CHEST);
        guiBuilder1.withTitle(Constants.INV_TITLE);
        guiBuilder1.withRows(6);

        GuiBuilder guiBuilder2 = new GuiBuilder();
        guiBuilder2.withType(InventoryType.CHEST);
        guiBuilder2.withTitle(Constants.INV_TITLE);
        guiBuilder2.withRows(6);

        GuiItem perkItem;

        Map<Player, Integer> pages = Perks.getPages();
        pages.computeIfAbsent(player, k -> Constants.PAGE_1);

        Map<String, Boolean> perksOfPlayer = perkProvider.getAllPerksFromPlayer(player);
        for (BasePerk perk : perks) {
            if (Perks.getPages().get(player).equals(Constants.PAGE_1)) {
                switch (perk.getPerkName()) {
                    case "Fallschaden" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 1, 1, player);
                        perkItem = new GuiItem(Material.COBWEB);
                        perkItem.withItemMeta("§9Fallschaden");
                        perkItem.withItemLore(Collections.singletonList("§7Du bekommst keinen Fallschaden mehr!"), true);
                        guiBuilder1.withItem(1, perkItem);
                    }
                    case "Feuerfest" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 4, 1, player);
                        perkItem = new GuiItem(Material.LAVA_BUCKET);
                        perkItem.withItemMeta("§cFeuerfest");
                        perkItem.withItemLore(Collections.singletonList("§7Du bekommst keinen Feuerschaden mehr!"), true);
                        guiBuilder1.withItem(4, perkItem);
                    }
                    case "Schwerkraft" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 7, 1, player);
                        perkItem = new GuiItem(Material.FEATHER);
                        perkItem.withItemMeta("§6Schwerkraft");
                        perkItem.withItemLore(Collections.singletonList("§7Du kannst auf deinem Plot fliegen!"), true);
                        guiBuilder1.withItem(7, perkItem);
                    }
                    case "Unterwasserforscher" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 1, 4, player);
                        perkItem = new GuiItem(Material.PUFFERFISH);
                        perkItem.withItemMeta("§bUnterwasserforscher");
                        perkItem.withItemLore(Collections.singletonList("§7Du kannst unter Wasser atmen!"), true);
                        guiBuilder1.withItem(28, perkItem);
                    }
                    case "Speed" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 4, 4, player);
                        perkItem = new GuiItem(Material.DIAMOND_BOOTS);
                        perkItem.withItemMeta("§2Speed");
                        perkItem.withItemLore(Collections.singletonList("§7Du erhälst Geschwindigkeit II!"), true);
                        guiBuilder1.withItem(31, perkItem);
                    }
                    case "Vollgestopft" -> {
                        setInteractPerkButtons(perk, guiBuilder1, perksOfPlayer, 7, 4, player);
                        perkItem = new GuiItem(Material.COOKED_BEEF);
                        perkItem.withItemMeta("§dVollgestopft");
                        perkItem.withItemLore(Collections.singletonList("§7Du bekommst keinen Hunger mehr!"), true);
                        guiBuilder1.withItem(34, perkItem);
                    }
                    default -> {
                        perkItem = new GuiItem(Material.BOOK);
                        perkItem.withItemMeta("§f§lAnleitung");
                        perkItem.withItemLore(Constants.loreInformation, true);
                        guiBuilder1.withItem(45, perkItem);
                        perkItem = new GuiItem(SkullCreator.getCustomTextureHead(Constants.PAGE_2_HEAD, true));
                        perkItem.withItemMeta(Constants.PAGE_2_BUTTON);
                        guiBuilder1.withItem(53, perkItem);
                    }
                }
                guiBuilder1.open(player, Perks.getInstance());
            }
            if (pages.get(player).equals(Constants.PAGE_2)) {
                switch (perk.getPerkName()) {
                    case "Hulk" -> {
                        setInteractPerkButtons(perk, guiBuilder2, perksOfPlayer, 1, 1, player);
                        perkItem = new GuiItem(Material.DIAMOND_SWORD);
                        perkItem.withItemMeta("§aHulk");
                        perkItem.withItemLore(Collections.singletonList("§7Du bekommst Stärke II!"), true);
                        guiBuilder2.withItem(1, perkItem);
                    }
                    default -> {
                        perkItem = new GuiItem(SkullCreator.getCustomTextureHead(Constants.PAGE_1_HEAD, true));
                        perkItem.withItemMeta(Constants.PAGE_1_BUTTON);
                        guiBuilder2.withItem(45, perkItem);
                    }
                }
                guiBuilder2.open(player, Perks.getInstance());
            }
        }
    }

    private static void setInteractPerkButtons(final BasePerk perk, final GuiBuilder guiBuilder, final Map<String, Boolean> perksOfPlayer, final int posX, final int posY, final Player player) {

        GuiItem deactivatedItem = new GuiItem(Material.GRAY_DYE);
        deactivatedItem.withItemMeta(Constants.DEACTIVATE);

        GuiItem notOwnedItem = new GuiItem(Material.BARRIER);
        notOwnedItem.withItemMeta(Constants.NOT_UNLOCKED);

        GuiItem activatedItem = new GuiItem(Material.LIME_DYE);
        activatedItem.withItemMeta(Constants.ACTIVATE);

        Map<BasePerk, Boolean> activatedPerks = Perks.getActivatedPerks().get(player);
        if (!perksOfPlayer.containsKey(perk.getPerkName())) {
            guiBuilder.withItemPerk(posX, posY, notOwnedItem, perk.getPerkName());
        } else if (perksOfPlayer.containsKey(perk.getPerkName()) && perksOfPlayer.get(perk.getPerkName())) {
            if (activatedPerks.containsKey(perk) && activatedPerks.containsValue(false)) {
                guiBuilder.withItemPerk(posX, posY, activatedItem, perk.getPerkName());
            } else if (activatedPerks.containsKey(perk) && activatedPerks.containsValue(true)) {
                guiBuilder.withItemPerk(posX, posY, deactivatedItem, perk.getPerkName());
            }
        } else if (perksOfPlayer.containsKey(perk.getPerkName()) && !perksOfPlayer.get(perk.getPerkName())) {
            if (activatedPerks.containsKey(perk) && activatedPerks.containsValue(true)) {
                guiBuilder.withItemPerk(posX, posY, deactivatedItem, perk.getPerkName());
            } else if (activatedPerks.containsKey(perk) && activatedPerks.containsValue(false)) {
                guiBuilder.withItemPerk(posX, posY, activatedItem, perk.getPerkName());
            }
        }
    }
}
