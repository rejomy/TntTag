package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.Command;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CreateSubCommand extends SubCommand {

    public CreateSubCommand() {
        super("create", "Create new arena.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if(args.length != 2) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "2");
            return false;
        }

        File directory = new File(Main.getInstance().getDataFolder(), "arena");
        AbstractFile file = new AbstractFile(directory, args[1]);

        if(file.getFile().exists()) {
            sendError(sender, CommandError.OTHER, "$reason", "The arena " + args[1] + " is exists!");
            return false;
        }

        file.create();

        sendMessage(sender, "Arena &e" + args[1] + "&f success created!");

        return false;
    }

}
