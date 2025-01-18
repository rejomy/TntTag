package me.rejomy.tnttag.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandUtil {

    public static void runCommand(Player player, String command) {
        if(command.contains("[player]")) {
            command = command.replaceAll("\\[player](\\s)?", "");
            player.chat("/" + command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("$player", player.getName()));
        }
    }

}
