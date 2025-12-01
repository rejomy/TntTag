package me.rejomy.tnttag.task;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.util.ActionBar;
import me.rejomy.tnttag.util.RandomUtil;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.citizens.CitizensUtil;
import me.rejomy.tnttag.util.person.PersonManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayTask extends BukkitRunnable {

    private final CitizensUtil citizensUtil = Main.getInstance().citizens;
    private final Match match;
    public int taskId;
    private int delay;

    public PlayTask(Match match) {
        this.match = match;
        delay = getRoundDelay();
    }

    @Override
    public void run() {
        delay--;
        match.explosionDelay = delay;

        byte size = (byte) match.getAlivePlayers().size();

        if (size == 0 || size == 1 && match.npcs.isEmpty()) {
            Bukkit.getScheduler().cancelTask(taskId);

            match.end();

            for (NPC npc : match.npcs.keySet()) {
                citizensUtil.remove(npc);
            }

            return;
        }

        // Если время раунда истекло:
        // Взрываем всех и начинаем новый раунд.
        if (delay == 0) {
            // Reset the level of XP as we are using it for time counting.
            match.getAlivePlayers().forEach(player -> player.setLevel(0));
            match.findAndBlewUpPlayers();
            return;
        } else if (delay == -Main.getInstance().getValue().DELAY_ROUND_POST || match.round == 0) {
            delay = getRoundDelay();

            if (size <= 6 && match.round != 0) {
                for (Player player : match.players.keySet()) {
                    player.teleport(match.getArena().start);
                }

                for (NPC npc : match.npcs.keySet()) {
                    npc.getEntity().teleport(match.getArena().start);
                }
            }

            List<String> tntNames = getNamesAndGiveTnt();

            ++match.round;

            for (Map.Entry<Player, PlayerTnt> map : match.players.entrySet()) {
                Player player = map.getKey();
                PlayerTnt data = map.getValue();

                player.sendMessage(Main.getInstance().getValue().GAME_ROUND_START_MESSAGE
                        .replace("$tnt", String.join(", ", tntNames))
                        .replace("$round", String.valueOf(match.round)));

                if (data.isTnt()) {
                    player.sendMessage(Main.getInstance().getValue().GAME_ROUND_START_TNT);
                } else if (data.spectator) {
                    player.sendMessage(Main.getInstance().getValue().GAME_ROUND_START_SPECTATOR);
                } else {
                    player.sendMessage(Main.getInstance().getValue().GAME_ROUND_START_LIFE);
                }
            }

            return;
        } else if (delay < 0) {
            return;
        }

        for (Map.Entry<Player, PlayerTnt> map : match.players.entrySet()) {
            if (map.getValue().spectator) {
                ActionBar.sendActionBar(map.getKey(), Main.getInstance().getValue().GAME_LIFE_ACTIONBAR);
                continue;
            }

            Player player = map.getKey();
            player.setLevel(delay);

            if (match.isTnt(player)) {
                ActionBar.sendActionBar(player, Main.getInstance().getValue().GAME_TNT_ACTIONBAR);
                player.getInventory().setItem(8, match.getCompass(player));
            } else {
                ActionBar.sendActionBar(player, Main.getInstance().getValue().GAME_LIFE_ACTIONBAR);
            }
        }

    }

    public List<String> getNamesAndGiveTnt() {
        List<String> tntNames = new ArrayList<>();

        int percentage = Main.getInstance().getValue().GAME_TNT_PLAYERS_PERCENTAGE;

        HashMap<Object, PlayerTnt> players = new HashMap<>(match.npcs);
        players.putAll(match.players);

        List<Object> alivePlayers = players.entrySet().stream()
                .filter(map -> !map.getValue().spectator)
                .sorted(Comparator.comparingInt(value -> value.getValue().count))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int tntAmount = Math.max(1, (alivePlayers.size() * percentage) / 100);

        for (byte a = 0; a < tntAmount; a++) {
            Object player = alivePlayers.get(a);

            if (player instanceof NPC) {
                citizensUtil.giveTnt(match, (NPC) player);
                citizensUtil.chasePlayersWithoutTnt(match, (NPC) player);
                tntNames.add(((NPC) player).getName());

                // Отправляем фразу в чат.
                // Делаем рандомную задержку для правдоподобности.
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                        ((Player) ((NPC) player).getEntity()).chat(PersonManager.getPersonByName(
                                ((NPC) player).getName()).getPhrase()), RandomUtil.RANDOM.nextInt(50) + 7);

            } else {
                match.give((Player) player);
                tntNames.add(((Player) player).getName());
            }
        }

        return tntNames;
    }

    public int getRoundDelay() {
        int players = getAlivePlayersAndBotsAmount();

        for (String format : Main.getInstance().getValue().DELAY_ROUND) {
            String[] values = format.split(":");

            if (players > Integer.parseInt(values[0])) {
                return Integer.parseInt(values[1]);
            }
        }

        return -1;
    }

    public int getAlivePlayersAndBotsAmount() {
        return match.getAlivePlayers().size() + match.npcs.size();
    }
}
