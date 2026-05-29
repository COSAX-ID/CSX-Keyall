package dev.cosax.cSXKeyall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyallTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> options = Arrays.asList("info", "reset", "set-time", "cmd", "reload");
            for (String s : options) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(s);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("cmd")) {
            List<String> cmdOptions = Arrays.asList("add", "remove", "list");
            for (String s : cmdOptions) {
                if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(s);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set-time")) {
            completions.add("1h");
            completions.add("30m");
            completions.add("45s");
        }

        return completions;
    }
}