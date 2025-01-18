package me.rejomy.tnttag.command;

import me.rejomy.tnttag.command.subcommand.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class TntTagCommand extends Command {
    private List<SubCommand> subCommands = new ArrayList<>();

    public TntTagCommand() {
        subCommands.add(new BotsSubCommand());
        subCommands.add(new JoinSubCommand());
        subCommands.add(new ReloadSubCommand());
        subCommands.add(new MaxSubCommand());
        subCommands.add(new MinSubCommand());
        subCommands.add(new SpawnSubCommand());
        subCommands.add(new CreateSubCommand());
        subCommands.add(new QueueSubCommand());
        subCommands.add(new TeleportSubCommand());
        subCommands.add(new LobbySubCommand());
        subCommands.add(new SpectateSubCommand());
        subCommands.add(new LeaveSubCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length == 0) {
            sendError(sender, CommandError.NOT_FOUND);
            return false;
        }

        for(SubCommand sub : subCommands) {
            if(sub.command.equalsIgnoreCase(args[0])) {
                if(sub.onlyForPlayer && sender instanceof ConsoleCommandSender) {
                    sendError(sender, CommandError.ONLY_FOR_PLAYER);
                } else if(sender.hasPermission("tnttag.command." + sub.command)) {
                    sub.onCommand(sender, command, label, args);
                } else {
                    sendError(sender, CommandError.NO_PERMISSION, "$perm", "tnttag.command." + sub.command);
                }
                return true;
            }
        }

        sendError(sender, CommandError.NOT_FOUND);

        return false;
    }

}
