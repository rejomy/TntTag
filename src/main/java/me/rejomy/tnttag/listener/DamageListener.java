package me.rejomy.tnttag.listener;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.citizens.CitizensUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    MatchManager manager = Main.getInstance().getMatchManager();
    CitizensUtil citizens = Main.getInstance().citizens;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (manager.isInMatch(player)) {
            Arena arena = manager.get(player).getArena();

            if (arena.status != Arena.Status.PLAYING) {
                event.setCancelled(true);
            } else {
                event.setDamage(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity(),
                killer = (Player) event.getDamager();

        if (!manager.isInMatch(killer) || !manager.isInMatch(victim)) {
            return;
        }

        Match match = manager.get(killer);

        if (!killer.hasMetadata("NPC") && match.players.get(killer).spectator
            || !victim.hasMetadata("NPC") && match.players.get(victim).spectator) {
            event.setCancelled(true);
            return;
        }

        Arena.Status status = match.getArena().status;

        if (status == Arena.Status.PLAYING) {
            if (match.isTnt(killer)) {
                match.take(killer);

                if (victim.hasMetadata("NPC") && citizens.getNPC(match, victim) != null) {
                    NPC victimNPC = citizens.getNPC(match, victim);
                    citizens.giveTnt(match, victimNPC);
                } else {
                    match.give(victim);
                }

            } else if (citizens.isTNT(killer, match)) {
                NPC killerNPC = citizens.getNPC(match, killer);
                citizens.handleTntHit(match, killerNPC);
                match.give(victim);
            }

        }
    }
}
