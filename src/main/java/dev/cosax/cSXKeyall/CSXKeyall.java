package dev.cosax.cSXKeyall;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class CSXKeyall extends JavaPlugin {

    private long nextKeyallTime;
    private long intervalMillis;
    private Object keyallTask;
    private FileConfiguration langConfig;
    private DatabaseManager dbManager;

    private static boolean isFolia;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException ignored) {
            isFolia = false;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveDefaultLangFiles();
        loadLanguageAndDatabase();

        KeyallCommand cmdHandler = new KeyallCommand(this);
        this.getCommand("keyall").setExecutor(cmdHandler);
        this.getCommand("keyall").setTabCompleter(new KeyallTabCompleter());

        startTimer();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KeyAllExpansion(this).register();
        }

        getLogger().info("CSX-KeyAll core successfully loaded with Cross-Platform Support!");
    }

    @Override
    public void onDisable() {
        if (keyallTask != null) {
            if (isFolia) {
                FoliaHelper.cancelTask(keyallTask);
            } else if (keyallTask instanceof BukkitTask) {
                ((BukkitTask) keyallTask).cancel();
            }
        }
        if (dbManager != null) {
            dbManager.saveData("next_keyall_time", nextKeyallTime);
            dbManager.close();
        }
    }

    public void loadLanguageAndDatabase() {
        reloadConfig();

        if (dbManager != null) {
            dbManager.close();
        }
        this.dbManager = new DatabaseManager(this);

        String langType = getConfig().getString("lang", "en");
        File langFile = new File(getDataFolder(), "lang/" + langType + ".yml");
        if (!langFile.exists()) {
            langType = "en";
            langFile = new File(getDataFolder(), "lang/en.yml");
        }
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
        this.intervalMillis = getConfig().getLong("interval-millis", 3600000L);

        long savedNextTime = dbManager.loadData("next_keyall_time", 0L);
        if (savedNextTime > System.currentTimeMillis()) {
            this.nextKeyallTime = savedNextTime;
        } else {
            this.nextKeyallTime = System.currentTimeMillis() + intervalMillis;
            dbManager.saveData("next_keyall_time", this.nextKeyallTime);
        }
    }

    private void saveDefaultLangFiles() {
        String[] languages = {"en", "id"};
        for (String lang : languages) {
            File file = new File(getDataFolder(), "lang/" + lang + ".yml");
            if (!file.exists()) {
                saveResource("lang/" + lang + ".yml", false);
            }
        }
    }

    private void startTimer() {
        if (keyallTask != null) {
            if (isFolia) {
                FoliaHelper.cancelTask(keyallTask);
            } else if (keyallTask instanceof BukkitTask) {
                ((BukkitTask) keyallTask).cancel();
            }
        }

        Runnable timerLogic = () -> {
            if (System.currentTimeMillis() >= nextKeyallTime) {
                runKeyAll();
            }
        };

        if (isFolia) {
            keyallTask = FoliaHelper.startTimer(this, timerLogic);
        } else {
            keyallTask = Bukkit.getScheduler().runTaskTimer(this, timerLogic, 20L, 20L);
        }
    }

    public void runKeyAll() {
        Bukkit.broadcastMessage(getLangMsg("broadcast-header"));
        Bukkit.broadcastMessage(getLangMsg("broadcast-started"));

        List<String> commands = getConfig().getStringList("commands-list");
        if (!commands.isEmpty()) {
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }

        this.nextKeyallTime = System.currentTimeMillis() + intervalMillis;
        dbManager.saveData("next_keyall_time", nextKeyallTime);

        Bukkit.broadcastMessage(getLangMsg("broadcast-success"));
        Bukkit.broadcastMessage(getLangMsg("next-timer-info").replace("%time%", formatMillis(intervalMillis)));
    }

    public DatabaseManager getDbManager() { return dbManager; }

    public String getLangMsg(String path) {
        String prefix = langConfig.getString("prefix", "");
        String message = langConfig.getString(path, "");
        if (message.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public String getRawLangMsg(String path) {
        return ChatColor.translateAlternateColorCodes('&', langConfig.getString(path, ""));
    }

    public long getNextKeyallTime() { return nextKeyallTime; }
    public void setNextKeyallTime(long nextKeyallTime) { this.nextKeyallTime = nextKeyallTime; }
    public long getIntervalMillis() { return intervalMillis; }
    public void setIntervalMillis(long intervalMillis) { this.intervalMillis = intervalMillis; }

    public String getLeftTimeFormatted() {
        long diff = nextKeyallTime - System.currentTimeMillis();
        return formatMillis(diff);
    }

    public String formatMillis(long millis) {
        if (millis <= 0) return "0s";
        long totalSecs = millis / 1000;
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        if (hours > 0) return hours + "h " + minutes + "m " + seconds + "s";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }
}