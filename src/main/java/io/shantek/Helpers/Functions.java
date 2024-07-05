package io.shantek.Helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Functions {

    private final JavaPlugin plugin;

    public Functions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Function used to send individual messages/errors to the player
    public void sendMessage(CommandSender sender, String message, boolean isError) {
        if (sender == null) {
            if (isError) {
                plugin.getLogger().severe(message);
            } else {
                plugin.getLogger().info(message);
            }
        } else {
            if (isError) {
                sender.sendMessage(ChatColor.RED + message);
            } else {
                sender.sendMessage(ChatColor.GREEN + message);
            }
        }
    }
}
