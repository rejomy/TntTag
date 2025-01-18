package me.rejomy.tnttag.arena;

import me.rejomy.tnttag.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArenaManager {
    public List<Arena> arenas = new ArrayList<>();
    public ArenaManager() {
        load();
    }
    public void load() {
        File directory = new File(Main.getInstance().getDataFolder(), "arena");

        File[] listFiles = directory.listFiles();

        if(listFiles == null) {
            return;
        }

        for(File file : listFiles) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            if(!contains(config, "start.x") || !contains(config, "min")
                || !contains(config, "max")) {
                Main.getInstance().getLogger().severe("Arena " + file.getName() + " is invalid! Skipping this arena...");
                continue;
            }

            Arena arena = new Arena();
            arena.name = file.getName().replace(".yml", "");

            double x, y, z;
            float yaw, pitch;
            World world;

            x = (double) config.get("start.x");
            y = (double) config.get("start.y");
            z = (double) config.get("start.z");
            pitch = Float.parseFloat(String.valueOf(config.getDouble("start.pitch")));
            yaw = Float.parseFloat(String.valueOf(config.getDouble("start.yaw")));
            world = Bukkit.getWorld(config.getString("start.world"));

            arena.start = new Location(world, x, y, z, yaw, pitch);

            arena.min = Integer.parseInt(config.getString("min"));
            arena.max = Integer.parseInt(config.getString("max"));

            arena.bots = contains(config, "bots") && config.getBoolean("bots");

            arenas.add(arena);
        }

    }
    private boolean contains(YamlConfiguration config, String path) {
        return config.get(path) != null;
    }
}
