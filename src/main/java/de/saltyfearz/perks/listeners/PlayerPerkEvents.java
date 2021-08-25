package de.saltyfearz.perks.listeners;

import de.saltyfearz.perks.Perks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Map;

public class PlayerPerkEvents implements Listener {

    @EventHandler
    public void onPlayerFallDamage(final EntityDamageEvent event) { //Hier könnten auch ebenfalls Drowning, Burn etc hinzugefügt werden, anstatt die Potioneffekte zu vergeben
        if (!(event.getEntity() instanceof Player player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        Map<String, Boolean> perksOfPlayer = Perks.getBasePerkProvider().getAllPerksFromPlayer(player);
        if (player.hasPermission("Perks.Fallschaden")) {
            if (perksOfPlayer.get("Fallschaden")) {
                if (perksOfPlayer.containsValue(true))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHungerChangeEvent(final FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        Map<String, Boolean> perksOfPlayer = Perks.getBasePerkProvider().getAllPerksFromPlayer(player);
        if (player.hasPermission("Perks.Vollgestopft")) {
            if (perksOfPlayer.get("Vollgestopft") != null) {
                if (perksOfPlayer.get("Vollgestopft")) {
                    if (perksOfPlayer.containsValue(true))
                        event.setCancelled(true);
                }
            }
        }
    }
}
