package dev.cosax.cSXKeyall;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Helper class isolating Folia-specific API calls.
 * Only loaded at runtime when the server is running Folia (RegionizedServer detected).
 * Prevents NoClassDefFoundError on Spigot/Paper servers that don't have Folia classes.
 */
class FoliaHelper {

    static Object startTimer(JavaPlugin plugin, Runnable timerLogic) {
        GlobalRegionScheduler scheduler = Bukkit.getGlobalRegionScheduler();
        return scheduler.runAtFixedRate(plugin, (task) -> timerLogic.run(), 20L, 20L);
    }

    static void cancelTask(Object task) {
        if (task instanceof ScheduledTask) {
            ((ScheduledTask) task).cancel();
        }
    }
}
