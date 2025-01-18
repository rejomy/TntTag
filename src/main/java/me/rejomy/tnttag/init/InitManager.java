package me.rejomy.tnttag.init;

import me.rejomy.tnttag.Expansion;
import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.TntTagCommand;
import me.rejomy.tnttag.util.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class InitManager {
    private JavaPlugin plugin;
    public InitManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadFiles();
    }
    public void enable() {
        // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansion().register();
        }

        ListenerManager listenerManager = new ListenerManager();

        Main.getInstance().getCommand("tnttag").setExecutor(new TntTagCommand());
    }

    private void loadFiles() {
        plugin.saveDefaultConfig();

        if (!new File(plugin.getDataFolder(), "messages.yml").exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }
}
