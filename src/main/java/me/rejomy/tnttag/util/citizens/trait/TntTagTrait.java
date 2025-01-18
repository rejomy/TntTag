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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@TraitName("TntTagTrait")
public class TntTagTrait extends Trait {
    private Navigator navigator;
    Player player;
    // It`s for npc don`t run on give tnt other people.
    // Don`t set the value is zero, or if npc have tnt on the match, skippedTicks don`t increase
    // and skippedTicks > 0 don`t run.
    int skippedTicks = 1;
    int navigatingTicks;

    public TntTagTrait() {
        super("TntTagTrait");
    }

    boolean SomeSetting = false;

    // see the 'Persistence API' section
    @Persist("mysettingname")
    boolean automaticallyPersistedSetting = false;

    // Here you should load up any values you have previously saved (optional).
    // This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
    // This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
    // This is called BEFORE onSpawn, npc.getEntity() will return null.
    public void load(DataKey key) {
        SomeSetting = key.getBoolean("SomeSetting", false);
    }

    // Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
    public void save(DataKey key) {
        key.setBoolean("SomeSetting", SomeSetting);
    }

    // An example event handler. All traits will be registered automatically as Spigot event Listeners
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

    }

    // Called every tick
    @Override
    public void run() {
        if(player.getInventory().getItemInHand().getType() == Material.AIR) {
            // If npc does not have a tnt, we set it to 0, because if npc in pvp, we can`t update player target.
            boolean isFirst = navigatingTicks == 0;
            navigatingTicks = 0;

            if(navigator.getTargetType() == TargetType.ENTITY) {
                if(skippedTicks++ < 80) {
                    return;
                }
            } else if(navigator.isNavigating()) {
                return;
            }

            Match match = Main.getInstance().getMatchManager().get(player);

            Main.getInstance().citizens.runAwayFromPlayerWithTnt(match, npc);

            if(isFirst) {
                player.setMaximumNoDamageTicks(40);

                List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
            }
        } else {
            if(skippedTicks > 0) {
                // If npc give tnt other player, at this timing player can give npc back.
                // When the npc does not have a tnt, skipped ticks add, it may cause npc try to run on pvp.
                skippedTicks = 0;
            } else {
                // Here we check if npc with tnt target to player.
                // Every sixty ticks we update target.
                if (navigator.isNavigating() && navigatingTicks++ < 160) {
                    return;
                }
            }

            navigatingTicks = 0;

            Match match = Main.getInstance().getMatchManager().get(player);

            Main.getInstance().citizens.chasePlayersWithoutTnt(match, npc);

            player.setMaximumNoDamageTicks(40);

            List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

            for (PotionEffect effect : effects) {
                player.removePotionEffect(effect.getType());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 3));
        }
    }

    //Run code when your trait is attached to a NPC.
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