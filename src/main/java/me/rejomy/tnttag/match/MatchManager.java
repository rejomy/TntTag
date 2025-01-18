package me.rejomy.tnttag.match;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.task.PlayTask;
import me.rejomy.tnttag.task.StartMatchTask;
import me.rejomy.tnttag.util.ColorUtil;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.item.ItemObject;
import me.rejomy.tnttag.util.person.PersonBuilder;
import me.rejomy.tnttag.util.person.PersonManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchManager {
    private List<Match> matches = new ArrayList<>();
    public List<Match> getMatches() {
        return matches;
    }
    private final int bots = Main.getInstance().getConfig().getInt("npc-settings.npc-in-match");

    public boolean add(Match match, Player player) {
        int size = match.players.size(), max = match.getArena().max;
        
        if(size == max) {
            player.sendMessage(Main.getInstance().getValue().ARENA_FULL_MESSAGE);
            return false;
        }

        player.getInventory().clear();

        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

        for(PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }

        player.teleport(match.getArena().start);

        match.players.put(player, new PlayerTnt());

        if(size + 1 == match.getArena().min) {
            if (!match.taskIsRunning) {
                StartMatchTask task = new StartMatchTask(match);
                task.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), task, 20, 20);

                match.taskIsRunning = true;
            }
        }

        for(Map.Entry<Integer, ItemObject> items : Main.getInstance().getItemsConfig().WAITING_ITEMS.entrySet()) {
            player.getInventory().setItem(items.getKey(), items.getValue().item);
        }

        player.setGameMode(GameMode.ADVENTURE);

        Main.getInstance().getQueueInventory().put();

        String messageAll = Main.getInstance().getValue().ADD_TO_QUEUE_MESSAGE
                .replace("$arena", match.getArena().name)
                .replace("$players", String.valueOf(match.players.size()))
                .replace("$player", player.getName())
                .replace("$need", String.valueOf(match.getArena().max));

        match.sendMessage(messageAll);

        String message = Main.getInstance().getValue().ADD_TO_QUEUE_PLAYER_MESSAGE
                .replace("$arena", match.getArena().name)
                .replace("$players", String.valueOf(match.players.size()))
                .replace("$need", String.valueOf(match.getArena().max));

        player.sendMessage(message);

        return true;
    }

    public void remove(Match match, Player player) {
        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

        for(PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }

        player.getInventory().clear();

        match.players.remove(player);
    }

    public boolean isInMatch(Player player) {
        return get(player) != null;
    }

    public Match get(Player player) {
        Match target = matches.stream().filter(match -> match.players.containsKey(player)).findFirst().orElse(null);

        if(target == null) {
            for(Match match : getMatches()) {
                for(Map.Entry<NPC, PlayerTnt> map : match.npcs.entrySet()) {
                    if(map.getKey().getEntity() == player) {
                        return match;
                    }
                }
            }
        }

        return target;
    }

    public void delete(Match match) {
        Main.getInstance().citizens.removeAll(match);

        matches.remove(match);

        Arena arena = match.getArena();
        arena.status = null;

        Main.getInstance().getQueueInventory().put();
    }

    public Match create(Arena arena) {
        Match match = new Match(arena);

        matches.add(match);
        return match;
    }

    public void start(Match match) {
        match.players.keySet().forEach(player -> {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
            });

            player.getInventory().clear();

            player.teleport(match.getArena().start);
        });

        if(match.getArena().bots) {

            for (PersonBuilder person : PersonManager.getPersons(bots)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                        Main.getInstance().citizens.spawn(match, person));
            }
        }

        match.getArena().status = Arena.Status.PLAYING;

        Main.getInstance().getQueueInventory().put();

        PlayTask playTask = new PlayTask(match);
        playTask.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), playTask, 20, 20);

        Main.getInstance().getSpectateInventory().put();
    }

}
