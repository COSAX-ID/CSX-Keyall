package dev.cosax.cSXKeyall;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final CSXKeyall plugin;
    private HikariDataSource dataSource;
    private Connection h2Connection;
    private String type;

    public DatabaseManager(CSXKeyall plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("database");
        if (dbSection == null) return;

        String rawType = dbSection.getString("type", "H2").toUpperCase();

        if (rawType.equals("MYSQL")) {
            this.type = "MYSQL";

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://" + dbSection.getString("host") + ":" + dbSection.getInt("port") + "/" + dbSection.getString("name"));
            config.setUsername(dbSection.getString("username"));
            config.setPassword(dbSection.getString("password"));
            config.addDataSourceProperty("useSSL", String.valueOf(dbSection.getBoolean("ssl")));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            config.setMaximumPoolSize(4);
            config.setMinimumIdle(1);
            config.setMaxLifetime(600000);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(5000);
            config.setPoolName("CSXKeyAllPool");

            this.dataSource = new HikariDataSource(config);
        } else {
            this.type = "H2";

            try {
                Class.forName("org.h2.Driver");
                File dbFile = new File(plugin.getDataFolder(), "storage");
                this.h2Connection = DriverManager.getConnection("jdbc:h2:file:" + dbFile.getAbsolutePath() + ";MODE=MySQL");
            } catch (Exception e) {
                plugin.getLogger().severe("Gagal memuat driver H2 lokal: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS csx_keyall_data (" +
                             "data_key VARCHAR(50) PRIMARY KEY, " +
                             "data_value BIGINT NOT NULL)"
             )) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Gagal menginisialisasi tabel database: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        if (type.equals("MYSQL")) {
            return dataSource.getConnection();
        } else {
            if (h2Connection == null || h2Connection.isClosed()) {
                try {
                    Class.forName("org.h2.Driver");
                    File dbFile = new File(plugin.getDataFolder(), "storage");
                    this.h2Connection = DriverManager.getConnection("jdbc:h2:file:" + dbFile.getAbsolutePath() + ";MODE=MySQL");
                } catch (Exception e) {
                    throw new SQLException("Gagal menyambung kembali ke H2: " + e.getMessage());
                }
            }
            return h2Connection;
        }
    }

    public long loadData(String key, long defaultValue) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT data_value FROM csx_keyall_data WHERE data_key = ?")) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("data_value");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Gagal memuat data '" + key + "': " + e.getMessage());
        }
        return defaultValue;
    }

    public void saveData(String key, long value) {
        String query;
        if (type.equals("MYSQL")) {
            query = "INSERT INTO csx_keyall_data (data_key, data_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE data_value = ?";
        } else {
            query = "MERGE INTO csx_keyall_data (data_key, data_value) KEY(data_key) VALUES (?, ?)";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, key);
            stmt.setLong(2, value);
            if (type.equals("MYSQL")) {
                stmt.setLong(3, value);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Gagal menyimpan data '" + key + "': " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
            if (h2Connection != null && !h2Connection.isClosed()) {
                h2Connection.close();
            }
        } catch (SQLException ignored) {}
    }
}
