package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.Command;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MinSubCommand extends SubCommand {

    public MinSubCommand() {
        super("min", "Set min players for a arena.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length != 3) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "3");
            return false;
        }

        File directory = new File(Main.getInstance().getDataFolder(), "arena");
        AbstractFile file = new AbstractFile(directory, args[1]);

        if (!file.getFile().exists()) {
            sendError(sender, CommandError.OTHER, "$reason", "The arena " + args[1] + " is not exists!");
            return false;
        }

        file.load();
        YamlConfiguration config = file.getConfig();

        config.set("min", args[2]);

        file.getFileUtil().save(file.getFile(), config);

        sendMessage(sender, "Min value " + args[2] + " has been set!");

        return false;
    }

}
