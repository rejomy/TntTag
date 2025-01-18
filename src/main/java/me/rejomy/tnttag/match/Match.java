package me.rejomy.tnttag.match;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.data.DataManager;
import me.rejomy.tnttag.data.PlayerData;
import me.rejomy.tnttag.manager.ParticleManager;
import me.rejomy.tnttag.util.ColorUtil;
import me.rejomy.tnttag.util.ItemBuilder;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.PlayerUtil;
import me.rejomy.tnttag.util.item.ItemObject;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class Match {

    /**
     * Need for prevent duplicate waiting task when player rejoin instantly, that triggering new waiting task.
     *  and cause problems
     */
    public boolean taskIsRunning;

    public int explosionDelay;
    public int round;
    public final ParticleManager particle = new ParticleManager(this);
    private final ItemStack itemTnt;
    private Arena arena;
    public Arena getArena() {
        return arena;
    }
    public HashMap<NPC, PlayerTnt> npcs = new HashMap<>();
    public HashMap<Player, PlayerTnt> players = new HashMap<>();

    public Match(Arena arena) {
        this.arena = arena;
        arena.status = Arena.Status.WAITING;

        ItemBuilder builder = new ItemBuilder(Material.TNT);
        builder.setDisplayName("&cTnt");
        itemTnt = builder.getItem();
    }

    public void sendMessage(String message) {
        for (Player player : players.keySet()) {
            player.sendMessage(ColorUtil.toColor(message));
        }
    }

    public void remove(Player player) {
        if(players.get(player).spectator) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }

        players.remove(player);

        for(Player target : players.keySet()) {
            target.showPlayer(player);
        }

        if(arena.status == Arena.Status.STARTING || arena.status == Arena.Status.WAITING) {
            if (players.size() < getArena().min) {
                arena.status = Arena.Status.WAITING;
            }
            Main.getInstance().getQueueInventory().put();
        } else if(arena.status == Arena.Status.PLAYING) {
            Main.getInstance().getSpectateInventory().put();
        }
    }

    public void blewUp(Player player) {
        sendMessage(Main.getInstance().getValue().EXPLODE_MESSAGE.replace("$player", player.getName()));
        particle.blewUp(player);

        players.get(player).spectator = true;
        setSpectator(player);
    }

    public void setSpectator(Player player) {
        PlayerUtil.clearEffects(player);
        PlayerUtil.clearInventory(player);

        for (Map.Entry<Player, PlayerTnt> map : players.entrySet()) {
            if (!map.getValue().spectator && map.getKey() != player) {
                map.getKey().hidePlayer(player);
            }
        }

        for(Map.Entry<Integer, ItemObject> items : Main.getInstance().getItemsConfig().ENDING_ITEMS.entrySet()) {
            player.getInventory().setItem(items.getKey(), items.getValue().item);
        }

        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void take(Player player) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            PlayerUtil.clearEffects(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
        });

        PlayerUtil.clearInventory(player);

        players.get(player).setHasTntStatus(false);
    }

    public void give(Player player) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            PlayerUtil.clearEffects(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 3));
        });

        players.get(player).setHasTntStatus(true);

        sendMessage(Main.getInstance().getValue().GAME_TNT_MESSAGE
                .replace("$player", player.getName()));

        player.getInventory().setHelmet(new ItemStack(Material.TNT));
        player.getInventory().setItem(0, itemTnt);
        player.getInventory().setItem(8, getCompass(player));
    }

    public boolean isTnt(Player player) {
        return players.get(player) != null && players.get(player).isTnt();
    }
    public List<Player> getAlivePlayers() {
        return players.entrySet().stream().filter(map -> !map.getValue().spectator)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void end() {
        arena.status = Arena.Status.ENDING;

        for (Map.Entry<Player, PlayerTnt> map : players.entrySet()) {
            Player player = map.getKey();
            PlayerUtil.clearEffects(player);
            PlayerUtil.clearInventory(player);

            for(Map.Entry<Integer, ItemObject> items : Main.getInstance().getItemsConfig().ENDING_ITEMS.entrySet()) {
                player.getInventory().setItem(items.getKey(), items.getValue().item);
            }

            if (map.getValue().spectator) {
                player.setFlying(false);
                player.setAllowFlight(false);

                for (Player target : players.keySet()) {
                    if (target != player) {
                        target.showPlayer(player);
                    }
                }

            } else {
                PlayerData data = DataManager.get(player.getUniqueId());
                data.games++;
                data.wins++;
                data.rounds+=round;

                int deaths = data.games - data.wins;
                float kd = deaths > 0? (float) data.wins / deaths : data.wins;
                data.killsAndDeath = ((int) (kd * 100.0)) / 100.0;

                sendMessage(Main.getInstance().getValue().WIN_MESSAGE.replace("$player", player.getName()));
                particle.win(player);
            }
        }

        if(!npcs.isEmpty()) {
            NPC winner = npcs.keySet().iterator().next();
            Player player = (Player) winner.getEntity();
            sendMessage(Main.getInstance().getValue().WIN_MESSAGE.replace("$player", player.getName()));
            particle.win(player);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            for (Player player : new ArrayList<>(players.keySet())) {
                player.teleport(Main.getInstance().getValue().LOBBY);
                PlayerUtil.clearInventory(player);
            }

            Main.getInstance().getQueueInventory().put();
            Main.getInstance().getSpectateInventory().put();

            Main.getInstance().getMatchManager().delete(this);
        }, Main.getInstance().getValue().DELAY_END * 20L);
    }

    public ItemStack getCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();

        List<Player> targets = new ArrayList<>(getAlivePlayers());
        targets.addAll(
                npcs.entrySet().stream()
                        .filter(map -> !map.getValue().isTnt())
                        .map(map -> (Player) map.getKey().getEntity())
                        .collect(Collectors.toList()));

        Player target = targets.stream().filter(p -> p != player)
                .min(Comparator.comparingDouble(p -> player.getLocation().distance(p.getLocation())))
                .orElse(null);

        String name = target == null ? "&cError!" : "&e" + target.getName() + " &6" + Math.round(
                target.getLocation().distance(player.getLocation()));

        meta.setDisplayName(ColorUtil.toColor(name));

        compass.setItemMeta(meta);

        return compass;
    }

}
