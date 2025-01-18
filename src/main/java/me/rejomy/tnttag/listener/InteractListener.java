package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.CommandUtil;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.item.ItemObject;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class InteractListener implements Listener {
    private MatchManager manager = Main.getInstance().getMatchManager();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Match match = Main.getInstance().getMatchManager().get(player);

        if (match == null) {
            return;
        }

        ItemStack item = player.getItemInHand();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR &&
                item != null && item.hasItemMeta()) {
            int itemInHandSlot = player.getInventory().getHeldItemSlot();

            if(match.getArena().status == Arena.Status.ENDING || match.players.get(player).spectator) {
                if(Main.getInstance().getItemsConfig().ENDING_ITEMS.containsKey(itemInHandSlot)) {
                    for(String command : Main.getInstance().getItemsConfig().ENDING_ITEMS.get(itemInHandSlot).commands) {
                        CommandUtil.runCommand(player, command);
                    }
                }
            } else if(match.getArena().status == Arena.Status.WAITING || match.getArena().status == Arena.Status.STARTING) {
                if(Main.getInstance().getItemsConfig().WAITING_ITEMS.containsKey(itemInHandSlot)) {
                    for(String command : Main.getInstance().getItemsConfig().WAITING_ITEMS.get(itemInHandSlot).commands) {
                        CommandUtil.runCommand(player, command);
                    }
                }
            }

            event.setCancelled(true);
        }

        if (event.isCancelled()) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }
}
