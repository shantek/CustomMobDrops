package io.shantek;

import io.shantek.Helpers.Command;
import io.shantek.Helpers.CustomDropConfig;
import io.shantek.Helpers.TabComplete;
import io.shantek.Listeners.EntityDeath;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomDrops extends JavaPlugin {

    public CustomDropConfig customDropConfig;
    public static CustomDrops instance;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        // Initialize custom drop config
        customDropConfig = new CustomDropConfig(this);
        customDropConfig.loadConfig();

        getCommand("customdrops").setExecutor(new Command(this));
        getCommand("customdrops").setTabCompleter(new TabComplete());

        // Register the plugin listeners
        registerPluginListeners();

        this.getLogger().info("CustomDrops plugin started.");
    }

    private void registerPluginListeners() {
        Bukkit.getPluginManager().registerEvents(new EntityDeath(this), this);
    }
}
