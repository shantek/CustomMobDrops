package io.shantek.Helpers;

import io.shantek.CustomMobDrops;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;
    private boolean customDropsEnabled;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        customDropsEnabled = config.getBoolean("custom-drops-enabled", true);
    }

    public void saveConfig() {
        FileConfiguration config = plugin.getConfig();
        config.set("custom-drops-enabled", customDropsEnabled);
        plugin.saveConfig();
    }

    public boolean isCustomMobDropsEnabled() {
        return customDropsEnabled;
    }

    public void setCustomMobDropsEnabled(boolean customDropsEnabled) {
        this.customDropsEnabled = customDropsEnabled;
        saveConfig();
    }
}
