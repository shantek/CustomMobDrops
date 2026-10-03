package io.shantek.Helpers;

import io.shantek.CustomMobDrops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

    private final CustomMobDrops plugin;

    public TabComplete(CustomMobDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> options = new ArrayList<>();

        if (!command.getName().equalsIgnoreCase("custommobdrops")) return completions;

        if (args.length == 1) {
            options.add("list");
            if (sender.isOp() || sender.hasPermission("shantek.custommobdrops.reload")) options.add("reload");
            if (sender.isOp() || sender.hasPermission("shantek.custommobdrops.enable")) {
                options.add("enable");
                options.add("disable");
            }
            StringUtil.copyPartialMatches(args[0], options, completions);

        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if ((sub.equals("enable") || sub.equals("disable"))
                    && (sender.isOp() || sender.hasPermission("shantek.custommobdrops.manage"))) {
                // Populate from loaded mob names in the drop config
                options.addAll(
                        plugin.customDropConfig.getEntityDrops().keySet().stream()
                                .map(e -> e.name().toLowerCase())
                                .collect(Collectors.toList())
                );
                StringUtil.copyPartialMatches(args[1], options, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }
}

