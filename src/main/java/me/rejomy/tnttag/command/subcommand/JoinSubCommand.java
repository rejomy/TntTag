package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.arena.ArenaManager;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class JoinSubCommand extends SubCommand {

    public JoinSubCommand() {
        super("join", "Join to the match.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length != 1) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "2");
            return false;
        }

        if(sender instanceof ConsoleCommandSender) {
            sendError(sender, CommandError.ONLY_FOR_PLAYER);
            return false;
        }

        Player player = (Player) sender;

        // Check if player has current match.
        Match currentMatch = Main.getInstance().getMatchManager().get(player);

        if (currentMatch != null) {
            currentMatch.remove(player);
        }
        //

        MatchManager matchManager = Main.getInstance().getMatchManager();

        List<Match> matches = new ArrayList<>(matchManager.getMatches()).stream()
                .filter(match -> match.getArena().status != Arena.Status.ENDING
                        && match.getArena().status != Arena.Status.PLAYING)
                .sorted(Comparator.comparingInt(match -> -match.players.size()))
                .collect(Collectors.toList());

        if(matches.isEmpty()) {
            sendError(sender, CommandError.OTHER, "$reason", "Arenas for playing not found!");
            return false;
        }

        matchManager.add(matches.get(0), player);

        return false;
    }

}
