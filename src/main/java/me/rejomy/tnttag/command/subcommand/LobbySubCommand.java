package me.rejomy.tnttag.command.subcommand;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.command.SubCommand;
import me.rejomy.tnttag.util.file.AbstractFile;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LobbySubCommand extends SubCommand {

    public LobbySubCommand() {
        super("lobby", "Set lobby command.");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length != 1) {
            sendError(sender, CommandError.ARGS, "$args", String.valueOf(args.length),
                    "$need", "1");
            return false;
        } else if (sender instanceof ConsoleCommandSender) {
            sendError(sender, CommandError.ONLY_FOR_PLAYER);
            return false;
        }

        double x, y, z;
        float yaw, pitch;
        String world;

        Player player = (Player) sender;
        Location location = player.getLocation();
        Main.getInstance().getValue().LOBBY = location;

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
        world = location.getWorld().getName();

        AbstractFile file = new AbstractFile(Main.getInstance().getDataFolder(), "settings");
        file.create();
        file.load();
        YamlConfiguration config = file.getConfig();

        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("yaw", yaw);
        config.set("pitch", pitch);
        config.set("world", world);

        file.getFileUtil().save(file.getFile(), config);

        sendMessage(sender, "Location: " + x + "/" + y + "/" + z + " " + yaw + " " + pitch + " " + world
                + " has been set!");

        return false;
    }

}
