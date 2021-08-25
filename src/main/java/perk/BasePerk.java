package perk;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.database.AbstractDatabaseStruct;
import de.saltyfearz.perks.database.MySQL;
import de.saltyfearz.perks.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasePerk extends AbstractDatabaseStruct {

    private final Logger logger = LogManager.getLogger();

    private final MySQL sql;
    private final String perkName;

    public BasePerk(final MySQL sql, final String perkName) {
        this.sql = sql;
        this.perkName = perkName;
    }

    public BasePerk(final MySQL sql) {
        this.sql = sql;
        this.perkName = Constants.EMPTY;
    }

    @Override
    protected void doUpdate() {
        PreparedStatement stmt = sql.getOrCreateStatement(PerkStatements.PERK_SEARCH_SPECIFIC);
        synchronized (stmt) {
            try {
                stmt.setString(1, perkName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Perks.getInstance(), () -> {
                            PreparedStatement userStmt = sql.getOrCreateStatement("REPLACE INTO PERK (`PerkName`) VALUES (?)");
                            try {
                                userStmt.setString(1, perkName);
                            } catch (final SQLException exc) {
                                logger.error("Could not update perk", exc);
                            }
                        }, 10L);
                    } else {
                        PreparedStatement userStmt = sql.getOrCreateStatement(PerkStatements.PERK_CREATE);
                        userStmt.setString(1, perkName);
                        userStmt.executeUpdate();
                    }
                }
            } catch (final SQLException exc) {
                logger.error("Could not find perk", exc);
            }
        }
    }

    public void createPerk(final String perkName) {
        PreparedStatement stmt = this.sql.getOrCreateStatement("INSERT IGNORE INTO PERK (`PerkName`) VALUES (?)");
        synchronized (stmt) {
            try {
                stmt.setString(1, perkName);
                stmt.executeUpdate();
                logger.info("PERK: " + perkName + " got created.");
            } catch (final SQLException exc) {
                logger.error(exc);
            }
        }
    }

    public void createPerks() {
        createPerk("Fallschaden");
        createPerk("Feuerfest");
        createPerk("Schwerkraft");
        createPerk("Unterwasserforscher");
        createPerk("Speed");
        createPerk("Vollgestopft");
        createPerk("Hulk");
    }

    public String getPerkName() { return perkName; }

}
