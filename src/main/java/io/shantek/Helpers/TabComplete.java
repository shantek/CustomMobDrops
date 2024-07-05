package io.shantek.Helpers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("customdrops")) {
            if (args.length == 1) {
                commands.add("reload");
                commands.add("enable");
                commands.add("disable");
                StringUtil.copyPartialMatches(args[0], commands, completions);
                Collections.sort(completions);
            }
        }
        return completions;
    }
}
