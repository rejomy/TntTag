package me.rejomy.tnttag.command;

public abstract class SubCommand extends Command {
    public final String command, description;
    public boolean onlyForPlayer;

    public SubCommand(String subCommand, String description) {
        this.command = subCommand;
        this.description = description;
    }

    public SubCommand(String subCommand, String description, boolean onlyForPlayer) {
        this(subCommand, description);

        this.onlyForPlayer = onlyForPlayer;
    }
}
