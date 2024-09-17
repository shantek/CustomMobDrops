package io.shantek.Helpers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;
    private boolean customMobDropsEnabled;
    private boolean lootingMultiplierEnabled;
    public boolean debuggingEnabled;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        customMobDropsEnabled = config.getBoolean("custom-drops-enabled", true);
        lootingMultiplierEnabled = config.getBoolean("looting-multiplier-enabled", true);
        debuggingEnabled = config.getBoolean("debugging-enabled", false);
    }

    public void saveConfig() {
        FileConfiguration config = plugin.getConfig();
        config.set("custom-drops-enabled", customMobDropsEnabled);
        config.set("looting-multiplier-enabled", lootingMultiplierEnabled);
        config.set("debugging-enabled", debuggingEnabled);
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

    public boolean isDebuggingEnabled() {  // Added method
        return debuggingEnabled;
    }

    public void setDebuggingEnabled(boolean debuggingEnabled) {  // Added method
        this.debuggingEnabled = debuggingEnabled;
        saveConfig();
    }
}
