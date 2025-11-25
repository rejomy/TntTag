package me.rejomy.tnttag.util.citizens.trait;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.Match;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@TraitName("TntTagTrait")
public class TntTagTrait extends Trait {

    private Player player;
    private Navigator navigator;

    private boolean lastTickHasTnt;
    // How much ticks player already navigating.
    private int navigatingTicksCount = 1;

    public TntTagTrait() {
        super("TntTagTrait");
    }

    @Override
    public void run() {
        Match match = Main.getInstance().getMatchManager().get(player);

        // Happens before the server is fully initialized if there are cached NPC.
        if (player == null || match == null) {
            npc.destroy();
            return;
        }

        int maxNavigatingTicks = 160;
        boolean hasTnt = player.getInventory().getItemInHand().getType() != Material.AIR;

        if (hasTnt) {
            if (!lastTickHasTnt) {
                lastTickHasTnt = true;

                List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 3));
            } else if (navigator.isNavigating() && navigatingTicksCount++ < maxNavigatingTicks) {
                return;
            }

            navigatingTicksCount = 0;
            Main.getInstance().citizens.chasePlayersWithoutTnt(match, npc);
        } else {
            if (lastTickHasTnt) {
                lastTickHasTnt = false;

                List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));

                // Reset navigating ticks, because we want to keep the player attacking after loosing tnt
                // for a half of max navigating ticks.
                navigatingTicksCount = maxNavigatingTicks / 2;
            }

            if (navigator.isNavigating() && navigatingTicksCount++ < maxNavigatingTicks) {
                return;
            } else if (navigator.getTargetType() == TargetType.ENTITY) {
                // Prevent attacking someone in case.
                npc.getNavigator().cancelNavigation();
            }

            navigatingTicksCount = 0;
            Main.getInstance().citizens.runAwayFromPlayerWithTnt(match, npc);
        }

        // For players it uses 20 ticks, but for NPC 40 works as 20...
        player.setMaximumNoDamageTicks(40);
    }

    // Run code when your trait is attached to the NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        navigator = npc.getNavigator();
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {
        player = (Player) npc.getEntity(); //cause NPE getEntity null
    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }
}