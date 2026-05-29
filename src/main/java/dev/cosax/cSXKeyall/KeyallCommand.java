package dev.cosax.cSXKeyall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyallCommand implements CommandExecutor {

    private final CSXKeyall plugin;

    public KeyallCommand(CSXKeyall plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("op")) {
            sender.sendMessage(plugin.getLangMsg("no-permission"));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.loadLanguageAndDatabase();
                sender.sendMessage(plugin.getLangMsg("reload-success"));
                return true;
            }

            if (args[0].equalsIgnoreCase("cmd")) {
                if (args.length >= 2) {
                    List<String> commands = plugin.getConfig().getStringList("commands-list");

                    if (args[1].equalsIgnoreCase("add") && args.length >= 3) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        String newCmd = sb.toString().trim();
                        commands.add(newCmd);
                        plugin.getConfig().set("commands-list", commands);
                        plugin.saveConfig();

                        sender.sendMessage(plugin.getLangMsg("cmd-added")
                                .replace("%id%", String.valueOf(commands.size() - 1))
                                .replace("%cmd%", newCmd));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("remove") && args.length == 3) {
                        try {
                            int id = Integer.parseInt(args[2]);
                            if (id >= 0 && id < commands.size()) {
                                String removed = commands.remove(id);
                                plugin.getConfig().set("commands-list", commands);
                                plugin.saveConfig();
                                sender.sendMessage(plugin.getLangMsg("cmd-removed")
                                        .replace("%id%", String.valueOf(id))
                                        .replace("%cmd%", removed));
                            } else {
                                sender.sendMessage(plugin.getLangMsg("id-not-found"));
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(plugin.getLangMsg("id-must-be-number"));
                        }
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("list")) {
                        sender.sendMessage(plugin.getLangMsg("cmd-list-header"));
                        if (commands.isEmpty()) {
                            sender.sendMessage(plugin.getRawLangMsg("cmd-list-empty"));
                        } else {
                            for (int i = 0; i < commands.size(); i++) {
                                sender.sendMessage(plugin.getRawLangMsg("prefix") + " §e[" + i + "] §f/" + commands.get(i));
                            }
                        }
                        return true;
                    }
                }
                sender.sendMessage(plugin.getRawLangMsg("prefix") + " §cUsage: /keyall cmd <add|remove|list> [args]");
                return true;
            }

            if (args[0].equalsIgnoreCase("set-time") && args.length == 2) {
                long parsedMillis = parseTimeString(args[1]);
                if (parsedMillis <= 0) {
                    sender.sendMessage(plugin.getLangMsg("invalid-format"));
                    return true;
                }
                plugin.setIntervalMillis(parsedMillis);
                plugin.setNextKeyallTime(System.currentTimeMillis() + parsedMillis);
                plugin.getConfig().set("interval-millis", parsedMillis);
                plugin.saveConfig();

                plugin.getDbManager().saveData("next_keyall_time", plugin.getNextKeyallTime());

                sender.sendMessage(plugin.getLangMsg("time-changed").replace("%time%", args[1]));
                sender.sendMessage(plugin.getLangMsg("time-countdown-reset"));
                return true;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                plugin.setNextKeyallTime(System.currentTimeMillis() + plugin.getIntervalMillis());
                plugin.getDbManager().saveData("next_keyall_time", plugin.getNextKeyallTime());

                sender.sendMessage(plugin.getLangMsg("timer-reset"));
                sender.sendMessage(plugin.getLangMsg("next-timer-info").replace("%time%", plugin.formatMillis(plugin.getIntervalMillis())));
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                sender.sendMessage(plugin.getRawLangMsg("prefix") + " §e§lKEYALL INFO");
                sender.sendMessage(plugin.getRawLangMsg("prefix") + " §7Interval: §f" + plugin.formatMillis(plugin.getIntervalMillis()));
                sender.sendMessage(plugin.getLangMsg("next-timer-info").replace("%time%", plugin.getLeftTimeFormatted()));
                return true;
            }
        }

        sender.sendMessage(plugin.getRawLangMsg("prefix") + " " + plugin.getRawLangMsg("help-header"));
        sender.sendMessage(plugin.getRawLangMsg("help-info"));
        sender.sendMessage(plugin.getRawLangMsg("help-reset"));
        sender.sendMessage(plugin.getRawLangMsg("help-set-time"));
        sender.sendMessage(plugin.getRawLangMsg("help-cmd-list"));
        sender.sendMessage(plugin.getRawLangMsg("help-cmd-add"));
        sender.sendMessage(plugin.getRawLangMsg("help-cmd-remove"));
        sender.sendMessage(plugin.getRawLangMsg("help-reload"));
        return true;
    }

    private long parseTimeString(String timeStr) {
        long totalMillis = 0;
        Pattern pattern = Pattern.compile("(\\d+)([hms])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeStr);
        boolean found = false;
        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            switch (unit) {
                case "h": totalMillis += value * 3600 * 1000; break;
                case "m": totalMillis += value * 60 * 1000; break;
                case "s": totalMillis += value * 1000; break;
            }
        }
        return found ? totalMillis : -1;
    }
}