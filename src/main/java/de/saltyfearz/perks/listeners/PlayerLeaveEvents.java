package de.saltyfearz.perks.listeners;

import de.saltyfearz.perks.Perks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEvents implements Listener {

    @EventHandler
    public void onPlayerLeaveEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!Perks.getActivatedPerks().get(player).isEmpty()) {
            Perks.getActivatedPerks().remove(player);
        }
    }
}
