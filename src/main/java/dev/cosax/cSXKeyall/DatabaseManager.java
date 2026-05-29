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
    private HikariDataSource dataSource; // Digunakan khusus MySQL
    private Connection flatFileConnection; // Digunakan khusus SQLite lokal
    private String type;

    public DatabaseManager(CSXKeyall plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        ConfigurationSection dbSection = plugin.getConfig().getConfigurationSection("database");
        if (dbSection == null) return;

        // Jika config diset ke H2, paksa alihkan ke SQLITE karena Paper modern tidak mendukung H2 bawaan
        String rawType = dbSection.getString("type", "SQLITE").toUpperCase();
        this.type = rawType.equals("H2") ? "SQLITE" : rawType;

        if (type.equals("MYSQL")) {
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
            // Direct Connection via SQLite (100% didukung bawaan semua core Minecraft)
            try {
                Class.forName("org.sqlite.JDBC");
                File dbFile = new File(plugin.getDataFolder(), "storage.db");
                this.flatFileConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            } catch (Exception e) {
                plugin.getLogger().severe("Gagal memuat driver SQLite lokal: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        // Buat tabel data
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
            if (flatFileConnection == null || flatFileConnection.isClosed()) {
                // Instansiasi ulang direct connection jika terputus
                try {
                    Class.forName("org.sqlite.JDBC");
                    File dbFile = new File(plugin.getDataFolder(), "storage.db");
                    this.flatFileConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
                } catch (Exception e) {
                    throw new SQLException("Gagal menyambung kembali ke SQLite: " + e.getMessage());
                }
            }
            return flatFileConnection;
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
            // Berlaku universal untuk SQLite (menggantikan MERGE INTO milik H2)
            query = "INSERT OR REPLACE INTO csx_keyall_data (data_key, data_value) VALUES (?, ?)";
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
            if (flatFileConnection != null && !flatFileConnection.isClosed()) {
                flatFileConnection.close();
            }
        } catch (SQLException ignored) {}
    }
}