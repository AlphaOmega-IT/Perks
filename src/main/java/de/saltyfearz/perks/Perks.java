package de.saltyfearz.perks;

import de.saltyfearz.perks.commands.PerkCommand;
import de.saltyfearz.perks.database.MySQL;
import de.saltyfearz.perks.listeners.PlayerPerkEvents;
import de.saltyfearz.perks.listeners.PlayerJoinEvents;
import de.saltyfearz.perks.user.BaseUserProvider;
import de.saltyfearz.perks.user.UserStatements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import perk.BasePerk;
import perk.BasePerkProvider;
import perk.PerkStatements;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Perks extends JavaPlugin {

    private final Logger logger = LogManager.getLogger();
    private static Perks instance;
    private static MySQL sql;
    private BaseUserProvider baseUserProvider;
    private BasePerkProvider basePerkProvider;
    private Map<Player, Integer> pages = new HashMap<>();
    public static Map<Player, Map<BasePerk, Boolean>> activatedPerksOfPlayer = new HashMap<>();
    private Map<String, Boolean> perksOfPlayer;

    @Override
    public void onLoad() {
        System.out.println("PLUGIN GETTING LOADED");
        instance = this;
        if (!(this.getDataFolder().exists()))
            this.getDataFolder().mkdir();
        sql = new MySQL(new File(this.getDataFolder(), "mysql.yml"));
        sql.connect();
        tableCreation();

        this.baseUserProvider = new BaseUserProvider(sql);
        this.basePerkProvider = new BasePerkProvider(sql);

        this.baseUserProvider.getAll();
        this.basePerkProvider.getAll();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPerkEvents(), this);
        this.getCommand("perks").setExecutor(new PerkCommand());

        System.out.println("PLUGIN LOADED");

        BasePerk basePerk = new BasePerk(sql);
        if(getBasePerkProvider().getAll().isEmpty()) {
            basePerk.createPerks();
        }
        onEffects();
    }

    @Override
    public void onDisable() {
        sql.close();
        System.out.println("PLUGIN UNLOADED");
    }

    public static Perks getInstance() { return instance; }
    public static MySQL getSQLConnection() { return sql; }

    private void tableCreation() {
        PreparedStatement stmt = sql.getOrCreateStatement(UserStatements.USER_CREATE_TABLE);
        synchronized (stmt) {
            try {
                stmt.executeUpdate();
            } catch (final SQLException exc) {
                logger.error("Could not create user table in mysql database", exc);
            }
        }
        PreparedStatement stmt2 = sql.getOrCreateStatement(PerkStatements.PERK_CREATE_TABLE);
        synchronized (stmt2) {
            try {
                stmt2.executeUpdate();
            } catch (final SQLException exc) {
                logger.error("Could not create perk table in mysql database", exc);
            }
        }
        PreparedStatement stmt3 = sql.getOrCreateStatement(PerkStatements.PERK_USER_TABLE);
        synchronized (stmt3) {
            try {
                stmt3.executeUpdate();
            } catch (final SQLException exc) {
                logger.error("Could not create perk table in mysql database", exc);
            }
        }
    }

    public void onEffects() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                perksOfPlayer = basePerkProvider.getAllPerksFromPlayer(player);
                updatePlayerEffects(player, perksOfPlayer);
            }
        }, 0, 20 * 5);
    }

    private void updatePlayerEffects(final Player player, final Map<String, Boolean> perksOfPlayer) {
        for (BasePerk perk : getAllPerks()) {
            if (player.hasPermission("Perks." + perk.getPerkName())) {
                if (perksOfPlayer.containsKey(perk.getPerkName())) {
                    if (perksOfPlayer.containsValue(true)) {
                        switch (perk.getPerkName()) {
                            case "Feuerfest" -> player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999999, 0));
                            case "Schwerkraft" -> player.setAllowFlight(true);
                            case "Unterwasserforscher" -> player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 99999999, 0));
                            case "Speed" -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
                            case "Hulk" -> player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 1));
                        }
                    } else {
                        switch (perk.getPerkName()) {
                            case "Feuerfest" -> player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            case "Schwerkraft" -> player.setAllowFlight(false);
                            case "Unterwasserforscher" -> player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                            case "Speed" -> player.removePotionEffect(PotionEffectType.SPEED);
                            case "Hulk" -> player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        }
                    }
                }
            }
        }
    }

    public static BaseUserProvider getBaseUserProvider() { return instance.baseUserProvider; }
    public static BasePerkProvider getBasePerkProvider() { return instance.basePerkProvider; }
    public static Collection<BasePerk> getAllPerks() { return instance.basePerkProvider.getAll(); }

    public static Map<Player, Integer> getPages() { return instance.pages; }
    public static void setPages(final Map<Player, Integer> pages) { instance.pages = pages; }

    public static Map<Player, Map<BasePerk, Boolean>> getActivatedPerks() { return activatedPerksOfPlayer; }
}
