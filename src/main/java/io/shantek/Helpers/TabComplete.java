package io.shantek.Helpers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete {

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

}
