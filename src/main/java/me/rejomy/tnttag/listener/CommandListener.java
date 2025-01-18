package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    private final MatchManager manager = Main.getInstance().getMatchManager();
    private final Config config = Main.getInstance().getValue();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Match match = manager.get(player);

        if(match != null && match.getArena().status == Arena.Status.PLAYING && !match.players.get(player).spectator) {
            String command = event.getMessage().substring(1);
            String[] args = command.split(" ");

            for(String configCommand : config.commandList) {
                String[] configArgs = configCommand.split(" ");

                if(configArgs.length > args.length) {
                    continue;
                }

                boolean same = false;

                for(byte number = 0; number < configArgs.length; number++) {
                    same = args[number].equalsIgnoreCase(configArgs[number]);
                }

                if(same) {
                    event.setCancelled(!config.commandWhiteList);
                    return;
                } else if(config.commandWhiteList) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
