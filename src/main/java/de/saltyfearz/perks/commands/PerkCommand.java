package de.saltyfearz.perks.commands;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.inventories.PerkInv;
import de.saltyfearz.perks.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }
        final Player player = ((Player) sender).getPlayer();
        if (player == null) {
            return true;
        }
        if (!(Perks.getBasePerkProvider().getAllPerksFromPlayer(player).size() > 0)) {
            sender.sendMessage(Constants.PREFIX + Constants.NO_PERKS);
            return true;
        }
        PerkInv.perkInv(player);
        return false;


    }
}
