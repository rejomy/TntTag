package me.rejomy.tnttag;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.data.DataManager;
import me.rejomy.tnttag.data.PlayerData;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Expansion extends PlaceholderExpansion {
    private Main plugin = Main.getInstance();
    private MatchManager manager = Main.getInstance().getMatchManager();
    @Override
    public String getAuthor() {
        return "Rejomy";
    }

    @Override
    public String getIdentifier() {
        return "tnttag";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if(params.equalsIgnoreCase("online")) {
            int players =
                    manager.getMatches().stream()
                            .mapToInt(match -> match.players.size())
                            .sum();
            return String.valueOf(players);
        }

        PlayerData data = DataManager.get(offlinePlayer.getUniqueId());

        if(data == null) {
            return "DATA_NULL";
        }

        switch (params) {
            case "wins":
                return String.valueOf(data.wins);
            case "rounds":
                return String.valueOf(data.rounds);
            case "kd":
                return String.valueOf(data.winsAndLoses);
            case "games":
                return String.valueOf(data.games);
        }

        Player player = Bukkit.getPlayer(offlinePlayer.getName());

        if(player == null || !manager.isInMatch(player)) {
            return "NOT_IN_MATCH";
        }

        if(params.equalsIgnoreCase("ingame")) {
            return "true";
        }

        Match match = manager.get(player);

        switch (params) {
            case "inround":
                return String.valueOf(manager.get(player).getArena().status == Arena.Status.PLAYING);
            case "players":
                return String.valueOf(match.getAlivePlayers().size() + match.npcs.size());
            case "round":
                return String.valueOf(match.round);
            case "arena":
                return match.getArena().name;
            case "istnt":
                return String.valueOf(match.isTnt(player));
            case "explosiontime":
                return String.valueOf(Math.max(match.explosionDelay, 0));
        }

        return null; // Placeholder is unknown by the Expansion
    }
}