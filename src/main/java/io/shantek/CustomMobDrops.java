package io.shantek;

import io.shantek.Helpers.*;
import io.shantek.Listeners.EntityDeath;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomMobDrops extends JavaPlugin {

    public CustomDropConfig customDropConfig;
    public PluginConfig pluginConfig;
    public static CustomMobDrops instance;

    public Metrics metrics;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        // Initialize plugin config
        pluginConfig = new PluginConfig(this);

        // Initialize custom drop config
        customDropConfig = new CustomDropConfig(this);
        customDropConfig.loadConfig();

        getCommand("custommobdrops").setExecutor(new Command(this));
        getCommand("custommobdrops").setTabCompleter(new TabComplete());

        // Register the plugin listeners
        registerPluginListeners();

        this.getLogger().info("Custom Mob Drops plugin started.");

        int pluginId = 23219;
        Metrics metrics = new Metrics(this, pluginId);
    }

    private void registerPluginListeners() {
        Bukkit.getPluginManager().registerEvents(new EntityDeath(this), this);
    }
}
