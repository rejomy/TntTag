package me.rejomy.tnttag.arena;

import org.bukkit.Location;

public class Arena {
    public String name;
    public int min, max;
    public boolean bots;
    public Location start;
    public Status status;

    public enum Status {
        WAITING,
        STARTING,
        PLAYING,
        ENDING;
    }

}
