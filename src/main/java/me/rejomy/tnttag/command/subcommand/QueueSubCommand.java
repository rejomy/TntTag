package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class QueueSubCommand extends SubCommand {

    public QueueSubCommand() {
        super("queue", "Gui with available arenas.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length != 1) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "1");
            return false;
        }

        if(sender instanceof ConsoleCommandSender) {
            sendError(sender, CommandError.ONLY_FOR_PLAYER);
            return false;
        }

        Player player = (Player) sender;

        player.openInventory(Main.getInstance().getQueueInventory().getInventory());
        PlayerUtil.sendMessage(player, Main.getInstance().getConfig().getString("inventory.queue.command"));

        return false;
    }

}
