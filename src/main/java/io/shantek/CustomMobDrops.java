package io.shantek;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import io.shantek.Helpers.*;
import io.shantek.Listeners.*;

public class CustomMobDrops extends JavaPlugin implements Listener {
    public int minShells = 0;
    public int maxShells = 3;
    public boolean pluginEnabled = true;
    public boolean debugMode = false;

    public static CustomMobDrops instance;

    public CustomMobDrops() {
    }

    public void onEnable() {

        // Save the instance of the plugin
        instance = this;

        // Register the plugin listeners
        registerPluginListeners();

        Command shells = this.getCommand("shells");
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            if (config.contains("minimum_shells")) {
                this.minShells = config.getInt("minimum_shells");
            }

            if (config.contains("maximum_shells")) {
                this.maxShells = config.getInt("maximum_shells");
            }

            if (config.contains("custom_drops_enabled")) {
                this.pluginEnabled = config.getBoolean("custom_drops_enabled");
            }

            if (config.contains("debug_mode")) {
                this.debugMode = config.getBoolean("debug_mode");
            }
        }

        this.getLogger().info("Shulker Shells plugin started.");
    }

    public void onDisable() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException var5) {
                //IOException var5 = var5;
                this.getLogger().log(Level.SEVERE, "Could not create config file", var5);
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("minimum_shells", this.minShells);
        config.set("maximum_shells", this.maxShells);
        config.set("custom_drops_enabled", this.pluginEnabled);
        config.set("debug_mode", this.debugMode);

        try {
            config.save(configFile);
        } catch (IOException var4) {
            //IOException var4 = var4;
            this.getLogger().log(Level.SEVERE, "Could not save config file", var4);
        }

    }

    private void registerPluginListeners() {

        Bukkit.getPluginManager().registerEvents(new EntityDeath(this), this);

    }

}
