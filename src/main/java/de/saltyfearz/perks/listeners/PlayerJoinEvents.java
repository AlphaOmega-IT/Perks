package de.saltyfearz.perks.listeners;

import de.saltyfearz.perks.Perks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinEvents implements Listener {

    @EventHandler
    public void onPlayerLoginEvent(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        Perks.getBaseUserProvider().createUser(player.getUniqueId());
        Perks.getActivatedPerks().putIfAbsent(player, Perks.getBasePerkProvider().getAllPerksFromPlayerConvertedToPerk(player));
    }
}
