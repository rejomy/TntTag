package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.data.DataManager;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.PlayerUtil;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpectateSubCommand extends SubCommand {
    public SpectateSubCommand() {
        super("spectate", "Spectate command.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sendError(sender, CommandError.ONLY_FOR_PLAYER);
            return false;
        }

        Player player = (Player) sender;

        if(args.length > 2) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "1");
            return false;
        } else if(args.length == 1) {
            player.openInventory(Main.getInstance().getSpectateInventory().getInventory());
            PlayerUtil.sendMessage(player, Main.getInstance().getConfig().getString("inventory.spectate.command"));
        } else if(args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if(target == null) {
                sendError(sender, CommandError.OTHER, "$reason", "Player " + args[1] + " is not found!");
                return false;
            }

            Match match = Main.getInstance().getMatchManager().get(target);

            if(match == null) {
                sendError(sender, CommandError.OTHER, "$reason", "Player " + args[1] + " does not playing to tnttag!");
                return false;
            }

            player.teleport(match.getArena().start);

            PlayerTnt playerTnt = new PlayerTnt();
            playerTnt.spectator = true;
            match.players.put(player, playerTnt);

            match.setSpectator(player);
        }

        return false;
    }

}
