package de.saltyfearz.perks.database;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class MySQL {

    private HashMap<String, Statement> statements = new HashMap<>();

    private static Connection con;

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String port;

    private YamlConfiguration yamlConfig;

    public MySQL(final File mysql) {
        if (!mysql.exists()) {
            try {
                mysql.createNewFile();
                this.yamlConfig = YamlConfiguration.loadConfiguration(mysql);
                yamlConfig.options().copyDefaults(true);
                yamlConfig.addDefault("host", "host");
                yamlConfig.addDefault("database", "database");
                yamlConfig.addDefault("username", "username");
                yamlConfig.addDefault("password", "password");
                yamlConfig.addDefault("port", "port");
                yamlConfig.save(mysql);
            } catch (final IOException exc) {
                exc.printStackTrace();
            }
        }
        this.yamlConfig = YamlConfiguration.loadConfiguration(mysql);
        this.host = yamlConfig.getString("host");
        this.database = yamlConfig.getString("database");
        this.username = yamlConfig.getString("username");
        this.password = yamlConfig.getString("password");
        this.port = yamlConfig.getString("port");
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (final SQLException exc) {
            exc.printStackTrace();
        }
        System.out.println("MySQL connected to " + host);

    }

    public void close() {
        try {
            con.close();
            System.out.println("MySQL connection closed.");
        } catch (final SQLException exc) {
            exc.printStackTrace();
        }
    }

    public ResultSet resultSQL(final String query) throws SQLException {
        PreparedStatement stmt = getOrCreateStatement(query);
        synchronized (stmt) {
            return stmt.executeQuery();
        }
    }

    public PreparedStatement getOrCreateStatement(String sql) {
        return (PreparedStatement) statements.computeIfAbsent(sql, stmt -> {
            try {
                return con.prepareStatement(stmt);
            } catch (SQLException exc) {
                exc.printStackTrace();
                return null;
            }
        });
    }

    public void update(String sql, Object... args) {
        Executors.newSingleThreadExecutor().execute(() -> {
            PreparedStatement stmt = getOrCreateStatement(sql);
            synchronized (stmt) {
                try {
                    for (int i = 0; i < args.length; i++) {
                        stmt.setObject(i + 1, args[i]);
                    }
                    stmt.executeUpdate();
                } catch (SQLException exc) {
                    exc.printStackTrace();
                }
            }
        });
    }
}
