package me.rejomy.tnttag.manager;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.Match;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ParticleManager {
    private Match match;
    private Sound WIN_SOUND, EXPLODE_SOUND;
    public ParticleManager(Match match) {
        this.match = match;
        load();
    }

    public void blewUp(Player target) {
        play(match, target, Effect.EXPLOSION_LARGE, EXPLODE_SOUND);
    }

    public void win(Player target) {
        play(match, target, Effect.HAPPY_VILLAGER, WIN_SOUND);
    }

    private void play(Match match, Player target, Effect effect, Sound sound) {
        for(Player player : match.players.keySet()) {
            player.playEffect(target.getLocation(), effect, 10);

            if(sound != null) {
                player.playSound(target.getLocation(), sound, 2f, 2f);
            }
        }
    }

    public void load() {
        WIN_SOUND = Main.getInstance().getValue().WIN_SOUND;
        EXPLODE_SOUND = Main.getInstance().getValue().EXPLODE_SOUND;
    }
}
