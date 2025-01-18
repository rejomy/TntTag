package me.rejomy.tnttag.init;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ListenerManager {
    public ListenerManager() {
        register(
                new DamageListener(),
                new ConnectionListener(),
                new ItemListener(),
                new InventoryListener(),
                new InteractListener(),
                new TeleportListener(),
                new FoodListener(),
                new CommandListener(),
                new CitizensListener()
        );
    }

    private void register(Listener... listeners) {
        for(Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        }
    }
}
