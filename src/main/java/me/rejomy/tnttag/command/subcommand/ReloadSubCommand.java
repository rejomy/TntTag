package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand() {
        super("reload", "Reload the plugin.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length != 1) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "1");
            return false;
        }

        Main.getInstance().reloadConfig();
        Main.getInstance().getValue().fillMessages();
        Main.getInstance().getValue().fillConfig();
        Main.getInstance().getArenaManager().load();

        MatchManager matchManager = Main.getInstance().getMatchManager();
        List<Match> matches = new ArrayList<>(matchManager.getMatches());

        for(Match match : matches) {
            matchManager.delete(match);
        }

        Main.getInstance().getQueueInventory().put();
        Main.getInstance().getSpectateInventory().put();

        sender.sendMessage("[TntTag] Config has been reloaded!");

        return false;
    }

}
