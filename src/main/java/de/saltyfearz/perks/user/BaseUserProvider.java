package de.saltyfearz.perks.user;

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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseUserProvider {

	private final Logger logger = LogManager.getLogger();
	private final MySQL sql;

	Map<UUID, BaseUser> cache = new ReferenceMap<>(ReferenceStrength.HARD, ReferenceStrength.SOFT);

	public BaseUserProvider(final MySQL sql) {
		this.sql = sql;
	}

	public Collection<BaseUser> getAll() {
		PreparedStatement stmt = this.sql.getOrCreateStatement("SELECT * FROM USER");
		synchronized (stmt) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					final UUID uniqueId = UUID.fromString(rs.getString("UUID"));
					cache.computeIfAbsent(uniqueId, key -> new BaseUser(sql, uniqueId));
				}
			} catch (final SQLException exc) {
				logger.error(exc);
			}
		}
		return Collections.unmodifiableCollection(this.cache.values());
	}

	public Integer getPlayerId(final Player player) {
		PreparedStatement stmt = this.sql.getOrCreateStatement("SELECT Id FROM USER WHERE UUID = ?");
		synchronized (stmt) {
			try {
				stmt.setString(1, player.getUniqueId().toString());
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

	public void createUser(final UUID uuid) {
		getAll();
		BaseUser user = this.cache.get(uuid);
		if (user == null) {
			PreparedStatement stmt = this.sql.getOrCreateStatement(UserStatements.USER_CREATE);
			synchronized (stmt) {
				try {
					stmt.setString(1, String.valueOf(uuid));
					if (stmt.executeUpdate() == 1)
						cache.computeIfAbsent(uuid, key -> new BaseUser(sql, uuid));
				} catch (final SQLException exc) {
					logger.error(exc);
				}
			}
		}
	}

	public BaseUser getByUniqueId(final UUID uniqueId) {
		getAll();
		BaseUser user = this.cache.get(uniqueId);
		if (user == null) {
			user = new BaseUser(sql, uniqueId);
		}
		return user;
	}

	public Boolean containsUniqueId(final UUID uniqueId) {
		AtomicBoolean finish = new AtomicBoolean(false);
		getAll().forEach(user -> {
			if (user.getUniqueId().equals(uniqueId))
				finish.set(true);
		});
		return finish.get();
	}
}
