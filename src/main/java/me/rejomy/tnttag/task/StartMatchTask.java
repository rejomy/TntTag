package me.rejomy.tnttag.task;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.util.ActionBar;
import me.rejomy.tnttag.util.ActionUtil;
import me.rejomy.tnttag.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartMatchTask extends BukkitRunnable {
    private Match match;
    private Arena arena;
    private int delay, startDelay;
    public int taskId;

    public StartMatchTask(Match match) {
        this.match = match;
        this.arena = match.getArena();

        delay = getDelay();
        startDelay = delay;
    }

    @Override
    public void run() {
        if(match.players.size() < arena.min) {
            arena.status = Arena.Status.WAITING;
            Bukkit.getScheduler().cancelTask(taskId);
            match.taskIsRunning = false;
            return;
        }

        if(getDelay() > startDelay) {
            delay+=getDelay();
        }

        if(delay-- > 0) {
            arena.status = Arena.Status.STARTING;

            for (Player player : match.players.keySet()) {
                player.setLevel(delay);

                ActionUtil.handle(player, delay, Main.getInstance().getValue().STARTING_ACTIONS, "starting", match);
            }

        } else {
            Main.getInstance().getMatchManager().start(match);
            match.taskIsRunning = false;
            Bukkit.getScheduler().cancelTask(taskId);
        }

    }

    private int getDelay() {
        int players = match.players.size();
        int delay;

        if(arena.max == players) {
            delay = Main.getInstance().getValue().DELAY_START_MAX;
        } else if((arena.max + arena.min) / 2 >= players) {
            delay = Main.getInstance().getValue().DELAY_START_MIDDLE;
        } else {
            delay = Main.getInstance().getValue().DELAY_START_MIN;
        }

        return delay;
    }
}
