package io.shantek.Helpers;

import io.shantek.CustomDrops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command implements CommandExecutor {

    public CustomDrops customDrops;
    private final Functions functions;

    public Command(CustomDrops customDrops) {
        this.customDrops = customDrops;
        this.functions = new Functions(customDrops);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("customdrops")) {
            return false;
        } else if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("shantek.customdrops.reload")) {
                    customDrops.customDropConfig.loadConfig(sender);
                    functions.sendMessage(sender, "Customdrops config file reloaded.", false);
                    customDrops.getLogger().info("Customdrops config file reloaded.");
                } else {
                    functions.sendMessage(sender, "You do not have permission to reload the plugin.", true);
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (sender.hasPermission("shantek.customdrops.enable")) {
                    customDrops.pluginConfig.setCustomDropsEnabled(true);
                    functions.sendMessage(sender, "Custom drops are now enabled.", false);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Custom drops are now enabled.");
                } else {
                    functions.sendMessage(sender, "You do not have permission to enable custom drops.", true);
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("shantek.customdrops.enable")) {
                    customDrops.pluginConfig.setCustomDropsEnabled(false);
                    functions.sendMessage(sender, "Custom drops are now disabled.", false);
                    Bukkit.broadcastMessage(ChatColor.RED + "Custom drops are now disabled.");
                } else {
                    functions.sendMessage(sender, "You do not have permission to disable custom drops.", true);
                }
            }
        }
        return false;
    }
}
