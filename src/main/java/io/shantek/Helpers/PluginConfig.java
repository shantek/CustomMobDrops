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
        customMobDropsEnabled = config.getBoolean("custom-drops-enabled", true);
        lootingMultiplierEnabled = config.getBoolean("looting-multiplier-enabled", true);
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
