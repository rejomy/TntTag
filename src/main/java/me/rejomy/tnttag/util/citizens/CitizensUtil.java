package me.rejomy.tnttag.util.citizens;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.util.PlayerTnt;
import me.rejomy.tnttag.util.citizens.trait.TntTagTrait;
import me.rejomy.tnttag.util.person.PersonBuilder;
import me.rejomy.tnttag.util.person.PersonManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class CitizensUtil {
    private Random random = new Random();
    boolean enable;

    public CitizensUtil() {
        // Register your trait with Citizens.
        net.citizensnpcs.api.CitizensAPI.getTraitFactory()
                .registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(TntTagTrait.class));

        load();
    }

    private void load() {
        enable = Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }

    public void spawn(Match match, PersonBuilder person) {
        if (!enable) {
            return;
        }

        Location location = match.getArena().start;

        //NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§7" + generator.generate());
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, person.getName());

        match.npcs.put(npc, new PlayerTnt());

        npc.spawn(location);
        npc.getOrAddTrait(TntTagTrait.class);

        // Default attack delay ticks is 20
        npc.getNavigator().getDefaultParameters().attackDelayTicks(Main.getInstance().getValue().NPC_ATTACK_DELAY);
        // Default attack range is 1.75
        npc.getNavigator().getDefaultParameters().attackRange(Main.getInstance().getValue().NPC_ATTACK_RANGE);

        npc.setProtected(false);

        Player player = (Player) npc.getEntity();
        player.setMaximumNoDamageTicks(40);

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

            for (PotionEffect effect : effects) {
                player.removePotionEffect(effect.getType());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
        });
    }

    public boolean isTNT(Player player, Match match) {
        return match.npcs.entrySet().stream()
                .filter(map -> map.getValue().isTnt() && map.getKey().getEntity() == player)
                .findAny().orElse(null) != null;
    }

    public NPC getNPC(Match match, Player player) {
        return match.npcs.keySet().stream().filter(target -> target.getEntity() == player).findAny().orElse(null);
    }

    public void handleTntHit(Match match, NPC npc) {
        Player player = (Player) npc.getEntity();
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, null);
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, null);
        //npc.setName("§7" + npc.getName().replaceAll("§[0-9]", ""));

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

            for (PotionEffect effect : effects) {
                player.removePotionEffect(effect.getType());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
        });

        match.npcs.put(npc, new PlayerTnt());
    }

    public void blewUp(Match match) {
        if (!enable || match.npcs.isEmpty()) {
            return;
        }

        match.npcs.entrySet().removeIf(map -> {
            if (map.getValue().isTnt()) {
                // Если включено убийство игроков на одном блоке с тнт.
                // Проходимся по игрокам без тнт и убиваем их.
                if(Main.getInstance().getValue().killSameBlockWithTnt) {
                    List<Player> players = map.getKey().getEntity().getLocation().getWorld().getNearbyEntities(
                            map.getKey().getEntity().getLocation(), 1, 1, 1).stream()
                            .filter(entity -> entity instanceof Player && !entity.hasMetadata("NPC")
                                    && !match.players.get(entity).isTnt())
                            .map(entity -> (Player) entity)
                            .collect(Collectors.toList());

                    for(Player player : players) {
                        match.blewUp(player);
                    }
                }

                match.particle.blewUp((Player) map.getKey().getEntity());
                match.sendMessage(Main.getInstance().getValue().EXPLODE_MESSAGE
                        .replace("$player", map.getKey().getName()));
                remove(map.getKey());
                return true;
            } else {
                NPC npc = map.getKey();
                npc.getNavigator().setTarget(null, false);
                return false;
            }
        });
    }

    public String giveTnt(Match match, NPC npc) {
        if (!enable) {
            return null;
        }

        Player player = (Player) npc.getEntity();

        //npc.setName("§c" + npc.getName().replaceAll("§[0-9]", ""));

        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.TNT, 1));
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.TNT, 1));

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

            for (PotionEffect effect : effects) {
                player.removePotionEffect(effect.getType());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 3));
        });

        match.sendMessage(Main.getInstance().getValue().GAME_TNT_MESSAGE.replace("$player", player.getDisplayName()));

        match.npcs.get(npc).setHasTntStatus(true);

        return npc.getName();
    }

    public void remove(NPC npc) {
        if (!enable) {
            return;
        }

        CitizensAPI.getNPCRegistry().deregister(npc);
    }

    public void removeAll(Match match) {
        if(!enable || match.npcs.isEmpty()) {
            return;
        }

        for(NPC npc : match.npcs.keySet()) {
            remove(npc);
        }
    }

    public void runAwayFromPlayerWithTnt(Match match, NPC npc) {
        Location location = npc.getEntity().getLocation();

        Player killer = match.players.keySet().stream()
                .filter(player -> player.getInventory().getHelmet() != null
                        && player.getInventory().getHelmet().getType() == Material.TNT
                        && player.getLocation().distance(location) < 30)
                .findFirst().orElse(null);

        if (killer != null) {
            Player victim = getPlayerWithoutTnt(match, (Player) npc.getEntity());

            if(victim == null || victim.getLocation().distance(killer.getLocation())
                    < npc.getEntity().getLocation().distance(killer.getLocation())) {

                boolean value = false;

                if(new Random().nextBoolean()) {
                    for (NPC target : match.npcs.keySet()) {
                        if (target == npc) {
                            continue;
                        }

                        if(target.getEntity().getLocation().distance(npc.getEntity().getLocation()) < 15) {
                            continue;
                        }

                        value = true;
                        victim = (Player) target.getEntity();
                        break;
                    }
                }

                if(!value) {
                    Player player = (Player) npc.getEntity();

                    if(player.getLocation().distanceSquared(match.getArena().start) > 144) {
                        npc.getNavigator().setTarget(match.getArena().start);
                    } else {
                        npc.getNavigator().setTarget(getNavigateLocation(npc, killer));
                    }
                }
            }

            if (victim != null) {
                npc.getNavigator().setTarget(victim.getLocation());
            }
        }
    }

    public Location getNavigateLocation(NPC npc, Player killer) {
        Player player = (Player) npc.getEntity();
        Location location = player.getLocation().clone();

        int multiplier = random.nextBoolean()? -1 : 1;
        double newX = location.getX() + (10 + random.nextInt(30)) * multiplier;
        double newZ = location.getZ() + (10 + random.nextInt(30)) * multiplier;

        for(byte repeat = 0; repeat < 7; repeat++) {
            for(byte j = 0; j < 20; j++) {
                int value = j > 9? -1: 1;

                Location newLoc = new Location(location.getWorld(), newX, location.getY() + (j + 10 * repeat) * value, newZ);
                if(newLoc.getBlock() != null && newLoc.getBlock().getType().isSolid()) {
                    if(npc.getNavigator().canNavigateTo(newLoc)
                            && killer.getLocation().distance(npc.getEntity().getLocation()) < newLoc.distance(killer.getLocation())) {
                        return newLoc;
                    } else {
                        return getNavigateLocation(npc, killer);
                    }
                }
            }
        }

        return null;
    }

    public void chasePlayersWithoutTnt(Match match, NPC npc) {
        if (!enable) {
            return;
        }

        Location location = npc.getEntity().getLocation();

        Player target = match.getAlivePlayers().stream()
                .filter(player -> player.getInventory().getHelmet() == null
                        || player.getInventory().getHelmet().getType() != Material.TNT)
                .min(Comparator.comparing(player -> player.getLocation().distanceSquared(location)))
                .orElse(null);

        if (target != null) {
            npc.getNavigator().setTarget(target, true);
        }
    }

    private Player getPlayerWithoutTnt(Match match, Player player) {
        Player target = match.getAlivePlayers().stream()
                .filter(player1 -> player1.getInventory().getHelmet() == null
                            || player1.getInventory().getHelmet().getType() != Material.TNT)
                .sorted(Comparator.comparing(value -> value.getLocation().distance(player.getLocation())))
                .findAny().orElse(null);

        return target;
    }
}
