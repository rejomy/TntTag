package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TeleportSubCommand extends SubCommand {
    public TeleportSubCommand() {
        super("tp", "Teleport you to a arena.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 2) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "2");
            return false;
        } else if (sender instanceof ConsoleCommandSender) {
            sendError(sender, CommandError.ONLY_FOR_PLAYER);
            return false;
        }

        Main.getInstance().getArenaManager().arenas.forEach(arena -> {
            if(arena.name.contains(args[1])) {
                ((Player) sender).teleport(arena.start);
            }
        });

        return false;
    }
}
