package io.shantek.Helpers;

import io.shantek.CustomMobDrops;
import io.shantek.Helpers.CustomDropConfig.MobDropConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class Command implements CommandExecutor {

    public CustomMobDrops customDrops;
    private final Functions functions;

    public Command(CustomMobDrops customDrops) {
        this.customDrops = customDrops;
        this.functions = new Functions(customDrops);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("custommobdrops")) {
            return false;
        } else if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("shantek.custommobdrops.reload") || sender.isOp()) {
                    customDrops.customDropConfig.loadConfig(sender);
                    functions.sendMessage(sender, "Customdrops config file reloaded.", false);
                    customDrops.getLogger().info("Customdrops config file reloaded.");
                    return true;
                } else {
                    functions.sendMessage(sender, "You do not have permission to reload the plugin.", true);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (sender.hasPermission("shantek.custommobdrops.enable") || sender.isOp()) {
                    customDrops.pluginConfig.setCustomMobDropsEnabled(true);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Custom drops are now enabled.");
                    return true;
                } else {
                    functions.sendMessage(sender, "You do not have permission to enable custom drops.", true);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (sender.hasPermission("shantek.custommobdrops.enable") || sender.isOp()) {
                    customDrops.pluginConfig.setCustomMobDropsEnabled(false);
                    Bukkit.broadcastMessage(ChatColor.RED + "Custom drops are now disabled.");
                    return true;
                } else {
                    functions.sendMessage(sender, "You do not have permission to disable custom drops.", true);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                Set<EntityType> entityTypes = customDrops.customDropConfig.getEntityDrops().keySet();
                if (entityTypes.isEmpty()) {
                    functions.sendMessage(sender, "No custom drops are active.", false);
                } else {
                    String mobsList = entityTypes.stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));
                    sender.sendMessage(ChatColor.GREEN + "Custom drops are enabled for the following mobs:");
                    sender.sendMessage(ChatColor.WHITE + mobsList);
                }
                return true;
            }
        }

        // Show usage message if command is invalid
        functions.sendMessage(sender, "Invalid command usage. Use /custommobdrops [reload | enable | disable | list]", true);
        return false;
    }
}
