package dev.cosax.cSXKeyall;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class KeyAllExpansion extends PlaceholderExpansion {

    private final CSXKeyall plugin;

    public KeyAllExpansion(CSXKeyall plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() { return "csx"; }
    @Override
    public String getAuthor() { return "cosaxid"; }
    @Override
    public String getVersion() { return "1.0.0"; }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equalsIgnoreCase("keyall")) {
            return plugin.getLeftTimeFormatted();
        }
        return null;
    }
}