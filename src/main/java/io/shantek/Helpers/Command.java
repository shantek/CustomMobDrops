package io.shantek.Helpers;

import io.shantek.CustomMobDrops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.stream.Collectors;

public class Command implements CommandExecutor {

    private final CustomMobDrops customDrops;
    private final Functions functions;

    public Command(CustomMobDrops customDrops) {
        this.customDrops = customDrops;
        this.functions = new Functions(customDrops);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("custommobdrops")) return false;

        if (args.length == 0) {
            functions.sendMessage(sender, "Invalid command usage. Use /custommobdrops [reload | enable | disable | list]", true);
            return false;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                if (!sender.hasPermission("shantek.custommobdrops.reload") && !sender.isOp()) {
                    functions.sendMessage(sender, "You do not have permission to reload the plugin.", true);
                    return true;
                }
                customDrops.pluginConfig.loadConfig();
                customDrops.customDropConfig.loadConfig(sender);
                int count = customDrops.customDropConfig.getEntityDrops().size();
                functions.sendMessage(sender, "Reloaded config: " + count + " mobs configured.", false);
                customDrops.getLogger().info("Custom mob drops config file reloaded.");
                return true;

            case "enable":
                if (args.length < 2) {
                    // Enable the whole plugin
                    if (!sender.hasPermission("shantek.custommobdrops.enable") && !sender.isOp()) {
                        functions.sendMessage(sender, "You do not have permission to enable custom drops.", true);
                        return true;
                    }
                    customDrops.pluginConfig.setCustomMobDropsEnabled(true);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Custom drops are now enabled.");
                } else {
                    // Enable a specific mob
                    if (!sender.hasPermission("shantek.custommobdrops.manage") && !sender.isOp()) {
                        functions.sendMessage(sender, "You do not have permission to manage mob drops.", true);
                        return true;
                    }
                    EntityType entityType = resolveEntityType(args[1]);
                    if (entityType == null) {
                        functions.sendMessage(sender, "Unknown mob: " + args[1], true);
                        return true;
                    }
                    if (customDrops.customDropConfig.setMobEnabled(entityType, true, sender)) {
                        functions.sendMessage(sender, "Custom drops enabled for: " + entityType.name(), false);
                    } else {
                        functions.sendMessage(sender, "No drop config found for: " + entityType.name(), true);
                    }
                }
                return true;

            case "disable":
                if (args.length < 2) {
                    // Disable the whole plugin
                    if (!sender.hasPermission("shantek.custommobdrops.enable") && !sender.isOp()) {
                        functions.sendMessage(sender, "You do not have permission to disable custom drops.", true);
                        return true;
                    }
                    customDrops.pluginConfig.setCustomMobDropsEnabled(false);
                    Bukkit.broadcastMessage(ChatColor.RED + "Custom drops are now disabled.");
                } else {
                    // Disable a specific mob
                    if (!sender.hasPermission("shantek.custommobdrops.manage") && !sender.isOp()) {
                        functions.sendMessage(sender, "You do not have permission to manage mob drops.", true);
                        return true;
                    }
                    EntityType entityType = resolveEntityType(args[1]);
                    if (entityType == null) {
                        functions.sendMessage(sender, "Unknown mob: " + args[1], true);
                        return true;
                    }
                    if (customDrops.customDropConfig.setMobEnabled(entityType, false, sender)) {
                        functions.sendMessage(sender, "Custom drops disabled for: " + entityType.name(), false);
                    } else {
                        functions.sendMessage(sender, "No drop config found for: " + entityType.name(), true);
                    }
                }
                return true;

            case "list":
                Map<EntityType, CustomDropConfig.MobDropConfig> drops = customDrops.customDropConfig.getEntityDrops();
                if (drops.isEmpty()) {
                    functions.sendMessage(sender, "No custom drops are configured.", false);
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Custom mob drop configurations:");
                    for (Map.Entry<EntityType, CustomDropConfig.MobDropConfig> entry : drops.entrySet()) {
                        String status = entry.getValue().isEnabled()
                                ? ChatColor.GREEN + "[ON]"
                                : ChatColor.RED + "[OFF]";
                        sender.sendMessage(status + ChatColor.WHITE + " " + entry.getKey().name() +
                                ChatColor.GRAY + " (" + entry.getValue().getEntries().size() + " entr" +
                                (entry.getValue().getEntries().size() == 1 ? "y" : "ies") + ")");
                    }
                }
                return true;

            default:
                functions.sendMessage(sender, "Invalid command usage. Use /custommobdrops [reload | enable | disable | list]", true);
                return false;
        }
    }

    private EntityType resolveEntityType(String name) {
        try {
            return EntityType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

