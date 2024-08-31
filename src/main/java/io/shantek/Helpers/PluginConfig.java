package io.shantek.Helpers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;
    private boolean customMobDropsEnabled;
    private boolean lootingMultiplierEnabled;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        // Check and load 'custom-drops-enabled'
        if (config.isBoolean("custom-drops-enabled")) {
            customMobDropsEnabled = config.getBoolean("custom-drops-enabled");
        } else {
            customMobDropsEnabled = true; // Default value
            plugin.getLogger().warning("Invalid value for 'custom-drops-enabled'. Defaulting to true.");
            config.set("custom-drops-enabled", customMobDropsEnabled); // Correct the config
        }

        // Check and load 'looting-multiplier-enabled'
        if (config.isBoolean("looting-multiplier-enabled")) {
            lootingMultiplierEnabled = config.getBoolean("looting-multiplier-enabled");
        } else {
            lootingMultiplierEnabled = true; // Default value
            plugin.getLogger().warning("Invalid value for 'looting-multiplier-enabled'. Defaulting to true.");
            config.set("looting-multiplier-enabled", lootingMultiplierEnabled); // Correct the config
        }

        // Save any corrected values back to the config file
        plugin.saveConfig();
    }

    public void saveConfig() {
        FileConfiguration config = plugin.getConfig();
        config.set("custom-drops-enabled", customMobDropsEnabled);
        config.set("looting-multiplier-enabled", lootingMultiplierEnabled);
        plugin.saveConfig();
    }

    public boolean isCustomMobDropsEnabled() {
        return customMobDropsEnabled;
    }

    public void setCustomMobDropsEnabled(boolean customDropsEnabled) {
        this.customMobDropsEnabled = customDropsEnabled;
        saveConfig();
    }

    public boolean isLootingMultiplierEnabled() {
        return lootingMultiplierEnabled;
    }

    public void setLootingMultiplierEnabled(boolean lootingMultiplierEnabled) {
        this.lootingMultiplierEnabled = lootingMultiplierEnabled;
        saveConfig();
    }
}
