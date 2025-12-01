package me.rejomy.tnttag.util;

import me.rejomy.tnttag.match.Match;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

import static me.rejomy.tnttag.util.SoundUtil.getSound;

public class ActionUtil {

    public static void handle(Player player, int delay, HashMap<String, List<String>> actionsMap, String period, Match match) {
        if (actionsMap.containsKey("all")) {
            runActions(player, actionsMap.get("all"), period, match);
        }

        List<String> actions = actionsMap.get(String.valueOf(delay));
        if (actions != null) {
            runActions(player, actions, period, match);
        }
    }

    private static void runActions(Player player, List<String> actions, String period, Match match) {
        String title = ColorUtil.toColor("&7"), subtitle = ColorUtil.toColor("&7");

        for (String action : actions) {
            String[] split = action.split(":");
            String type = split[0].toLowerCase();
            String value = split[1];

            switch (type) {
                case "message":
                    player.sendMessage(ColorUtil.toColor(value));
                    break;
                case "title":
                    title = ColorUtil.toColor(value);
                    break;
                case "subtitle":
                    subtitle = ColorUtil.toColor(value);
                    break;
                case "sound":
                    player.playSound(player.getLocation(), getSound(value), 2f, 2f);
                    break;
                case "actionbar":
                    ActionBar.sendActionBar(player, ColorUtil.toColor(value)
                            .replace("$players", String.valueOf(match.players.size()))
                            .replace("$max", String.valueOf(match.getArena().max)));
                    break;
            }
        }

        player.sendTitle(title, subtitle);
    }
}
