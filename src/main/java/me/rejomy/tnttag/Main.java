package me.rejomy.tnttag;

import me.rejomy.tnttag.arena.ArenaManager;
import me.rejomy.tnttag.database.DataBase;
import me.rejomy.tnttag.database.SQLite;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.util.Config;
import me.rejomy.tnttag.init.InitManager;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.citizens.CitizensUtil;
import me.rejomy.tnttag.util.inventory.impl.QueueInventory;
import me.rejomy.tnttag.util.inventory.impl.SpectateInventory;
import me.rejomy.tnttag.util.item.ItemsConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    public final DataBase dataBase = new SQLite();
    private final InitManager init = new InitManager(this);
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }
    private MatchManager matchManager;
    private ArenaManager arenaManager;
    private Config valueContainer;
    private ItemsConfig itemsConfig;
    public CitizensUtil citizens;
    public Config getValue() {
        return valueContainer;
    }
    public ItemsConfig getItemsConfig() {
        return itemsConfig;
    }
    public MatchManager getMatchManager() {
        return matchManager;
    }
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
    private QueueInventory queueInventory;
    private SpectateInventory spectateInventory;
    public QueueInventory getQueueInventory() {
        return queueInventory;
    }
    public SpectateInventory getSpectateInventory() {
        return spectateInventory;
    }

    @Override
    public void onLoad() {
        instance = this;
        init.load();

        try {
            dataBase.loadDataFromDataBase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        citizens = new CitizensUtil();
        matchManager = new MatchManager();
        arenaManager = new ArenaManager();
        itemsConfig = new ItemsConfig();
        itemsConfig.load();
        valueContainer = new Config();
        queueInventory = new QueueInventory();
        spectateInventory = new SpectateInventory();
        spectateInventory.put();
        queueInventory.put();

        init.enable();
    }

    @Override
    public void onDisable() {
        List<Match> matches = new ArrayList<>(matchManager.getMatches());

        for(Match match : matches) {
            matchManager.delete(match);
        }

        try {
            dataBase.savePlayersData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
