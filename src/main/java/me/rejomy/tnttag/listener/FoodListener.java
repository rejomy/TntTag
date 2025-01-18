package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodListener implements Listener {

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if(Main.getInstance().getMatchManager().isInMatch(player)) {
            event.setCancelled(true);
        }
    }

}
