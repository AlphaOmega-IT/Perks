package de.saltyfearz.perks.user;

import de.saltyfearz.perks.Perks;
import de.saltyfearz.perks.database.AbstractDatabaseStruct;
import de.saltyfearz.perks.database.MySQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BaseUser extends AbstractDatabaseStruct {

    private final MySQL sql;
    private final UUID uniqueId;

    private final Logger logger = LogManager.getLogger();

    public BaseUser(final MySQL sql, final UUID uniqueId) {
        this.sql = sql;
        this.uniqueId = uniqueId;
    }

    @Override
    protected void doUpdate() {
        PreparedStatement stmt = sql.getOrCreateStatement(UserStatements.USER_SEARCH_SPECIFIC);
        synchronized (stmt) {
            try {
                stmt.setString(1, uniqueId.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Perks.getInstance(), () -> {
                            PreparedStatement userStmt = sql.getOrCreateStatement("REPLACE INTO USER ('UUID') VALUES (?)");
                            try {
                                userStmt.setString(1, uniqueId.toString());
                                userStmt.executeUpdate();
                            } catch (final SQLException exc) {
                                logger.error("Could not update user", exc);
                            }
                        }, 20L);
                    } else {
                        PreparedStatement userStmt = sql.getOrCreateStatement(UserStatements.USER_CREATE);
                        userStmt.setString(1, uniqueId.toString());
                        userStmt.executeUpdate();
                    }
                }
            } catch (final SQLException exc) {
                logger.error("Could not find user", exc);
            }
        }
    }

    public UUID getUniqueId() { return uniqueId; }

}
