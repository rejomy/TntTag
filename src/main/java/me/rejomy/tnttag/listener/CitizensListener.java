package me.rejomy.tnttag.listener;

import net.citizensnpcs.api.event.NPCPushEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    @EventHandler
    public void onPush(NPCPushEvent event) {
        event.setCancelled(true);
    }

}
