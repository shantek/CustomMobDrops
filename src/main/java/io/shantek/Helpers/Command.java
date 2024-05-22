package io.shantek.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Command {

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
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
