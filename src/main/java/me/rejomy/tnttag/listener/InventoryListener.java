package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.Command;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.ColorUtil;
import me.rejomy.tnttag.util.PlayerTnt;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    MatchManager matchManager = Main.getInstance().getMatchManager();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (matchManager.isInMatch(player)) {
            event.setCancelled(true);
            return;
        }

        if (inventory.getName().equals(ColorUtil.toColor(
                Main.getInstance().getConfig().getString("inventory.queue.name")))) {

            event.setCancelled(true);

            if (Main.getInstance().getQueueInventory().maps.containsKey(event.getSlot())) {
                matchManager.add(
                        Main.getInstance().getQueueInventory().maps.get(event.getSlot()), player);

                Sound sound = Main.getInstance().getValue().INVENTORY_CLICK_SOUND;
                if (sound != null) {
                    player.playSound(player.getLocation(), sound, 3f, 3f);
                }
            }

        } else if(inventory.getName().equals(ColorUtil.toColor(
                Main.getInstance().getConfig().getString("inventory.spectate.name")))) {

            event.setCancelled(true);

            if (Main.getInstance().getSpectateInventory().maps.containsKey(event.getSlot())) {
                Match match = Main.getInstance().getSpectateInventory().maps.get(event.getSlot());

                player.teleport(match.getArena().start);

                PlayerTnt playerTnt = new PlayerTnt();
                playerTnt.spectator = true;
                match.players.put(player, playerTnt);

                match.setSpectator(player);

                Sound sound = Main.getInstance().getValue().INVENTORY_CLICK_SOUND;

                if (sound != null) {
                    player.playSound(player.getLocation(), sound, 3f, 3f);
                }
            }

        }

    }

}

