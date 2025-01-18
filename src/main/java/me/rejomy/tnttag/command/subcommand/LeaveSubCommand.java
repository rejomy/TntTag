package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.match.Match;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveSubCommand extends SubCommand {
    public LeaveSubCommand() {
        super("leave", "Leave from a match.", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player player = (Player) sender;
        Match match = Main.getInstance().getMatchManager().get(player);

        if(match == null) {
            return false;
        }

        match.remove(player);

        player.teleport(Main.getInstance().getValue().LOBBY);

        return false;
    }
}
