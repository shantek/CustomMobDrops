package io.shantek.Helpers;

import io.shantek.CustomDrops;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    public CustomDrops customDrops;
    public Command(CustomDrops customDrops) {
        this.customDrops = customDrops;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("customdrops")) {
            return false;
        } else if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                customDrops.customDropConfig.loadConfig();

                if (sender instanceof Player player) {
                    player.sendMessage(ChatColor.GREEN + "Customdrops config file reloaded.");
                }
                customDrops.getLogger().info("Customdrops config file reloaded.");
            }
        }
        return false;
    }
}



