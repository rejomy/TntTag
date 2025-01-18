package me.rejomy.tnttag.util;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static me.rejomy.tnttag.util.SoundUtil.getSound;

public class Config {
    public int DELAY_START_MIN, DELAY_START_MIDDLE, DELAY_START_MAX, DELAY_END, DELAY_ROUND_POST, GAME_TNT_PLAYERS_PERCENTAGE,
        NPC_ATTACK_DELAY;
    public List<String> DELAY_ROUND;
    public double NPC_ATTACK_RANGE;
    public final HashMap<String, List<String>> STARTING_ACTIONS = new HashMap<>();
    public List<String> QUEUE_ARENA_LORE;
    public String QUEUE_ARENA_NAME,
        GAME_TNT_ACTIONBAR,
        GAME_LIFE_ACTIONBAR,
        GAME_TNT_MESSAGE,
        EXPLODE_MESSAGE,
        WIN_MESSAGE,
        ARENA_FULL_MESSAGE,
        ADD_TO_QUEUE_MESSAGE, ADD_TO_QUEUE_PLAYER_MESSAGE,
        GAME_ROUND_START_MESSAGE, GAME_ROUND_START_LIFE, GAME_ROUND_START_TNT, GAME_ROUND_START_SPECTATOR;

    public Sound WIN_SOUND, EXPLODE_SOUND, INVENTORY_CLICK_SOUND;
    public Location LOBBY;
    public boolean commandWhiteList = false, killSameBlockWithTnt, killLastMSTntPlayers;
    public List<String> commandList;

    public Config() {
        fillConfig();
        fillMessages();
    }

    public void fillConfig() {
        FileConfiguration config = Main.getInstance().getConfig();

        killSameBlockWithTnt = config.getBoolean("settings.tnt-same-block-kill");
        killLastMSTntPlayers = config.getBoolean("settings.millis-tnt-kill");

        commandWhiteList = config.getString("command.mode").equalsIgnoreCase("whitelist");
        commandList = config.getStringList("command.commands");

        GAME_TNT_PLAYERS_PERCENTAGE = config.getInt("settings.tnt-players-percentage");

        DELAY_START_MAX = config.getInt("delay.start.max");
        DELAY_START_MIN = config.getInt("delay.start.min");
        DELAY_START_MIDDLE = config.getInt("delay.start.middle");

        DELAY_ROUND = config.getStringList("delay.round-delay");
        DELAY_ROUND_POST = config.getInt("delay.round-post");
        DELAY_END = config.getInt("delay.end");

        QUEUE_ARENA_LORE = config.getStringList("inventory.queue.arena.lore").stream()
                .map(ColorUtil::toColor).collect(Collectors.toList());
        QUEUE_ARENA_NAME = ColorUtil.toColor(config.getString("inventory.queue.arena.name"));

        EXPLODE_SOUND = getSound(config.getString("game.blew up sound"));
        WIN_SOUND = getSound(Main.getInstance().getConfig().getString("game.win sound"));
        INVENTORY_CLICK_SOUND = getSound(config.getString("inventory.click-sound"));

        NPC_ATTACK_RANGE = config.getDouble("npc-settings.attack-range");
        NPC_ATTACK_DELAY = config.getInt("npc-settings.attack-delay");
    }

    public void fillMessages() {
        File file = new File(Main.getInstance().getDataFolder(), "messages.yml");
        YamlConfiguration messages = YamlConfiguration.loadConfiguration(file);

        // Fill starting messages to map.
        for(String section : messages.getConfigurationSection("starting").getKeys(false)) {
            boolean isList = messages.get("starting." + section) instanceof List;

            STARTING_ACTIONS.put(section, isList? messages.getStringList("starting." + section) :
                    Collections.singletonList(messages.getString("starting." + section)));
        }

        GAME_TNT_ACTIONBAR = ColorUtil.toColor(messages.getString("game.actionbar tnt"));
        GAME_LIFE_ACTIONBAR = ColorUtil.toColor(messages.getString("game.actionbar life"));
        GAME_TNT_MESSAGE = ColorUtil.toColor(messages.getString("game.tnt message"));

        GAME_ROUND_START_MESSAGE = ColorUtil.toColor(messages.getString("game.round-start.main"));
        GAME_ROUND_START_TNT = ColorUtil.toColor(messages.getString("game.round-start.tnt"));
        GAME_ROUND_START_LIFE = ColorUtil.toColor(messages.getString("game.round-start.life"));
        GAME_ROUND_START_SPECTATOR = ColorUtil.toColor(messages.getString("game.round-start.spectator"));

        ARENA_FULL_MESSAGE = ColorUtil.toColor(messages.getString("ARENA_IS_FULL"));
        ADD_TO_QUEUE_PLAYER_MESSAGE = ColorUtil.toColor(messages.getString("waiting.ADD_TO_QUEUE_PLAYER"));
        ADD_TO_QUEUE_MESSAGE = ColorUtil.toColor(messages.getString("waiting.ADD_TO_QUEUE"));

        EXPLODE_MESSAGE = ColorUtil.toColor(messages.getString("game.explode"));
        WIN_MESSAGE = ColorUtil.toColor(messages.getString("game.win"));

        AbstractFile settings = new AbstractFile(Main.getInstance().getDataFolder(), "settings");

        if(settings.getFile().exists()) {
            settings.load();
            YamlConfiguration lobby = settings.getConfig();

            LOBBY = new Location(
                    Bukkit.getWorld(lobby.getString("world")),
                    lobby.getDouble("x"),
                    lobby.getDouble("y"),
                    lobby.getDouble("z"),
                    (float) lobby.getDouble("yaw"),
                    (float) lobby.getDouble("pitch")
            );

        } else {
            List<World> worlds = Bukkit.getWorlds();
            for(World world : worlds) {
                LOBBY = world.getSpawnLocation();
                break;
            }
        }
    }
}
