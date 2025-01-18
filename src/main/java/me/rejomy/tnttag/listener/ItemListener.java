package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemListener implements Listener {
    MatchManager matchManager = Main.getInstance().getMatchManager();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if(matchManager.isInMatch(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(matchManager.isInMatch(player)) {
            event.setCancelled(true);
        }
    }
}

