package perk;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.database.MySQL;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasePerkProvider {

    private final Logger logger = LogManager.getLogger();
    private final MySQL sql;

    Map<String, BasePerk> cache = new ReferenceMap<>(ReferenceStrength.HARD, ReferenceStrength.SOFT);
    Map<String, Boolean> perkCache = new ReferenceMap<>(ReferenceStrength.HARD, ReferenceStrength.SOFT);

    public BasePerkProvider(final MySQL sql) {
        this.sql = sql;
    }

    public Collection<BasePerk> getAll() {
        if (!cache.isEmpty()) return Collections.unmodifiableCollection(this.cache.values());
        PreparedStatement stmt = this.sql.getOrCreateStatement("SELECT * FROM PERK ORDER BY Id");
        synchronized (stmt) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final String perkName = rs.getString("PerkName");
                    cache.computeIfAbsent(perkName, key -> new BasePerk(sql, perkName));
                }
            } catch (final SQLException exc) {
                logger.error(exc);
            }
        }
        return Collections.unmodifiableCollection(this.cache.values());
    }

    public Map<String, Boolean> getAllPerksFromPlayer(final Player player) {
        PreparedStatement stmt = this.sql.getOrCreateStatement(PerkStatements.PERKS_SEARCH_BY_USER);
        final UUID uuid = player.getUniqueId();
        synchronized (stmt) {
            try {
                stmt.setString(1, String.valueOf(uuid));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    final String perkName = rs.getString("PerkName");
                    final boolean activated = rs.getBoolean("Activated");
                    perkCache.putIfAbsent(perkName, activated);
                }
            } catch (final SQLException exc) {
                logger.error(exc);
            }
        }
        return perkCache;
    }

    public Map<BasePerk, Boolean> getAllPerksFromPlayerConvertedToPerk(final Player player) {
        Map<String, Boolean> perks = getAllPerksFromPlayer(player);
        Map<BasePerk, Boolean> perksConverted = new HashMap<>();
        for (Map.Entry<String, Boolean> pair : perks.entrySet()) {
            perksConverted.putIfAbsent(Perks.getBasePerkProvider().getPerkByName(pair.getKey()), pair.getValue());
        }
        return perksConverted;
    }

    public void savePerkInteractions(final Player player) {
        PreparedStatement stmt = this.sql.getOrCreateStatement(PerkStatements.PERKS_UPDATE_ACTIVATED);
        synchronized (stmt) {
            Map<BasePerk, Boolean> perks;
            perks = Perks.getActivatedPerks().get(player);
            Integer id = Perks.getBaseUserProvider().getPlayerId(player);
            perks.forEach((key, value) -> {
                try {
                    stmt.setBoolean(1, value);
                    stmt.setInt(2, getPerkId(key));
                    stmt.setInt(3, id);
                    stmt.executeUpdate();
                } catch (final SQLException exc) {
                    logger.error("could not update perks", exc);
                }
            });
        }
    }

    public BasePerk getPerkByUniqueId(final String perkName) {
        getAll();
        BasePerk perk = this.cache.get(perkName);
        if (perk == null) {
            perk = new BasePerk(sql, perkName);
        }
        return perk;
    }

    public BasePerk getPerkByName(final String perkName) {
        getAll();
        return this.cache.get(perkName);
    }

    public Integer getPerkId(final BasePerk perk) {
        PreparedStatement stmt = this.sql.getOrCreateStatement("SELECT * FROM PERK WHERE PerkName = ?");
        synchronized (stmt) {
            try {
                stmt.setString(1, perk.getPerkName());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("Id");
                }
            } catch (final SQLException exc) {
                logger.error(exc);
            }
        }
        return 0;
    }
}
