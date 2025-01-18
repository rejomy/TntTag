package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SpawnSubCommand extends SubCommand {

    public SpawnSubCommand() {
        super("spawn", "Set arena spawn.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length < 2) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "2");
            return false;
        }

        double x, y, z;
        float yaw, pitch;
        String world;

        if(sender instanceof ConsoleCommandSender) {

            if (args.length < 8) {
                sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                        "$need", "8");
                sendError(sender, CommandError.OTHER, "$reason", "Use /tnttag start x y z yaw pitch world");
                return false;
            }

            x = Float.parseFloat(args[2]);
            y = Float.parseFloat(args[3]);
            z = Float.parseFloat(args[4]);
            yaw = Float.parseFloat(args[5]);
            pitch = Float.parseFloat(args[6]);
            world = args[7];

            if(Bukkit.getWorld(world) == null) {
                sendError(sender, CommandError.OTHER, "$reason", "World " + world + " is not exists!");
                return false;
            }

        } else {
            Player player = (Player) sender;
            Location location = player.getLocation();

            x = location.getX();
            y = location.getY();
            z = location.getZ();
            yaw = location.getYaw();
            pitch = location.getPitch();
            world = location.getWorld().getName();
        }

        File directory = new File(Main.getInstance().getDataFolder(), "arena");
        AbstractFile file = new AbstractFile(directory, args[1]);

        if(!file.getFile().exists()) {
            sendError(sender, CommandError.OTHER, "$reason", "The arena " + args[1] + " is not exists!");
            return false;
        }

        file.load();
        YamlConfiguration config = file.getConfig();

        config.set("start.x", x);
        config.set("start.y", y);
        config.set("start.z", z);
        config.set("start.yaw", yaw);
        config.set("start.pitch", pitch);
        config.set("start.world", world);

        file.getFileUtil().save(file.getFile(), config);

        sendMessage(sender, "Location: " + x + "/" + y + "/" + z + " " + yaw + " " + pitch + " " + world
            + " has been set!");

        return false;
    }

}
