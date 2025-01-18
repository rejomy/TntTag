package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.command.Command;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.ActionBar;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BotsSubCommand extends SubCommand {

    public BotsSubCommand() {
        super("bots", "Enable npcs in a arena.");
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

        if (!file.getFile().exists()) {
            sendError(sender, CommandError.OTHER, "$reason", "The arena " + args[1] + " is not exists!");
            return false;
        }

        file.load();
        YamlConfiguration config = file.getConfig();

        boolean bots = config.get("bots") == null || !config.getBoolean("bots");

        for(Arena arena : Main.getInstance().getArenaManager().arenas) {
            if(arena.name.equals(args[1])) {
                arena.bots = !arena.bots;
            }
        }

        config.set("bots", bots);

        file.getFileUtil().save(file.getFile(), config);

        sendMessage(sender, "Bot value " + bots + " has been set!");

        return false;
    }

}
