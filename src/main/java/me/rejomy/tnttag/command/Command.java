package me.rejomy.tnttag.command;

import me.rejomy.tnttag.util.ColorUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class Command implements CommandExecutor {

    public void sendError(CommandSender sender, CommandError commandError, String... replace) {
        String error = ColorUtil.toColor(commandError.message);

        if(replace.length >= 2) {
            for (byte i = 1; i < replace.length; i++) {
                error = error.replace(replace[i - 1], replace[i]);
            }
        }

        sender.sendMessage(error);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ColorUtil.toColor("&8            &m---------"));
        sender.sendMessage(ColorUtil.toColor("&f " + message));
        sender.sendMessage(ColorUtil.toColor("&8            &m---------"));
    }

    protected enum CommandError {
        NOT_FOUND("&8[&eTntTag&8] &7Please, see the command list:" +
                "\n &f/tnttag create name &8- &7create the arena." +
                "\n &f/tnttag min name &8- &7select min players amount for the arena." +
                "\n &f/tnttag max name &8- &7Select max players amount for the arena" +
                "\n &f/tnttag bots name &8- &7Enable or disable npcs in game. (need citizens)" +
                "\n &f/tnttag start name &8- &7Select start location for the arena." +
                "\n &f/tnttag lobby &8- &7Set lobby." +
                "\n &f/tnttag queue &8- &7Tnt tag queue."),
        ARGS("&8[&eTntTag&8] &7Args error! &c$args&8/$need"),
        NO_PERMISSION("&8[&eTntTag&8] &7You don`t have permission &f$perm &7to perform this command."),
        OTHER("&8[&eTntTag&8] &7$reason"),
        ONLY_FOR_PLAYER("&8[&eTntTag&8] &fThis command can`t be executed on console!");

        final String message;

        CommandError(String message) {
            this.message = message;
        }
    }

}
