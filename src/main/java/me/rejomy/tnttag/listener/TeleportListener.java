package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
    private MatchManager manager = Main.getInstance().getMatchManager();

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Если телепортация из 1 мира в другой.
        if(event.getTo().getWorld() != event.getFrom().getWorld()) {
            Match match = manager.get(player);

            // Если игрок в матче.
            if(match == null) {
                return;
            }

            // Если мир, в который будут телепортировать игрока не равен миру матча, то удаляем игрока из матча.
            if(!event.getTo().getWorld().getName().equalsIgnoreCase(
                    match.getArena().start.getWorld().getName())) {
                match.remove(player);
            }
        }
    }
}
