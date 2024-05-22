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

public class CustomMobDrops extends JavaPlugin implements Listener {
    private int minShells = 0;
    private int maxShells = 3;
    private boolean pluginEnabled = true;
    private boolean debugMode = false;

    public CustomMobDrops() {
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
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
                IOException var5 = var5;
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
            IOException var4 = var4;
            this.getLogger().log(Level.SEVERE, "Could not save config file", var4);
        }

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Shulker && this.pluginEnabled && this.pluginEnabled) {
            int numShells = false;
            int numShells;
            if (this.minShells == this.maxShells) {
                numShells = this.minShells;
            } else {
                numShells = ThreadLocalRandom.current().nextInt(this.minShells, this.maxShells + 1);
            }

            if (this.debugMode) {
                this.getLogger().info("Shulker should drop " + (numShells + 1) + " shells");
            }

            event.getDrops().clear();

            for(int i = 0; i <= numShells; ++i) {
                event.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
            }
        }

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 && alias.startsWith("shells")) {
            List<String> completions = new ArrayList();
            completions.add("min");
            completions.add("max");
            completions.add("enabled");
            completions.add("debug");
            return completions;
        } else {
            return Collections.emptyList();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("shells")) {
            return false;
        } else if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /shells <min|max|enabled|debug> [value]");
            return true;
        } else {
            String subCommand = args[0].toLowerCase();
            int newMaxShells;
            if (subCommand.equals("min")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /shells min <number>");
                    return true;
                } else {
                    try {
                        newMaxShells = Integer.parseInt(args[1]);
                        this.getConfig().set("minimum_shells", newMaxShells);
                        this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Minimum shulker shell drops set to " + newMaxShells);
                        this.minShells = newMaxShells;
                        if (this.minShells > this.maxShells) {
                            sender.sendMessage(ChatColor.RED + "Minimum shells is currently set higher than maximum shells.");
                        }

                        return true;
                    } catch (NumberFormatException var7) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format. Please enter a valid integer.");
                        return true;
                    }
                }
            } else if (subCommand.equals("max")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /shells max <number>");
                    return true;
                } else {
                    try {
                        newMaxShells = Integer.parseInt(args[1]);
                        this.getConfig().set("maximum_shells", newMaxShells);
                        this.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Maximum shulker shell drops set to " + newMaxShells);
                        this.maxShells = newMaxShells;
                        if (this.maxShells < this.minShells) {
                            sender.sendMessage(ChatColor.RED + "Maximum shells is currently set lower than minimum shells.");
                        }

                        return true;
                    } catch (NumberFormatException var8) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format. Please enter a valid integer.");
                        return true;
                    }
                }
            } else {
                boolean isDebugEnabled;
                if (subCommand.equals("enabled")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /shells enabled <true|false>");
                        return true;
                    } else {
                        isDebugEnabled = Boolean.parseBoolean(args[1]);
                        this.getConfig().set("custom_drops_enabled", isDebugEnabled);
                        this.saveConfig();
                        this.pluginEnabled = isDebugEnabled;
                        Bukkit.getServer().broadcastMessage("Bonus shulker shells are now " + (isDebugEnabled ? "enabled" : "disabled") + "!");
                        return true;
                    }
                } else if (subCommand.equals("debug")) {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /shells debug <true|false>");
                        return true;
                    } else {
                        isDebugEnabled = Boolean.parseBoolean(args[1]);
                        this.getConfig().set("debug_mode", isDebugEnabled);
                        this.saveConfig();
                        if (isDebugEnabled) {
                            sender.sendMessage(ChatColor.GREEN + "Shulker shell debug mode has been enabled.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Shulker shell debug mode has been disabled.");
                        }

                        this.debugMode = isDebugEnabled;
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
