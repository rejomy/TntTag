package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.data.DataManager;
import me.rejomy.tnttag.data.PlayerData;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    MatchManager manager = Main.getInstance().getMatchManager();

    @EventHandler
    public void onConnection(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (DataManager.get(player.getUniqueId()) == null) {
            PlayerData data = new PlayerData();
            data.uuid = player.getUniqueId();
            data.winsAndLoses = 0;
            DataManager.add(data);
        }
    }

    @EventHandler
    public void onDisconnection(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Match match = manager.get(player);

        if (match != null) {
            player.getInventory().clear();
            manager.remove(match, player);

            Arena arena = match.getArena();

            if(arena.status == Arena.Status.STARTING || arena.status == Arena.Status.WAITING) {
                if (match.players.size() < match.getArena().min) {
                    arena.status = Arena.Status.WAITING;
                }
                Main.getInstance().getQueueInventory().put();
            } else if(arena.status == Arena.Status.PLAYING) {
                Main.getInstance().getSpectateInventory().put();
            }
        }
    }
}
