package me.rejomy.tnttag.util.inventory.impl;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.arena.ArenaManager;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.ColorUtil;
import me.rejomy.tnttag.util.ItemBuilder;
import me.rejomy.tnttag.util.inventory.AbstractInventory;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class QueueInventory extends AbstractInventory {

    public HashMap<Integer, Match> maps = new HashMap<>();

    public QueueInventory() {
        super(Main.getInstance().getConfig().getString("inventory.queue.name"), 27);
    }

    public void put() {
        ArenaManager arenaManager = Main.getInstance().getArenaManager();
        MatchManager matchManager = Main.getInstance().getMatchManager();

        List<Match> matches = new ArrayList<>(matchManager.getMatches()).stream()
                .filter(match -> match.getArena().status != Arena.Status.ENDING
                    && match.getArena().status != Arena.Status.PLAYING)
                .sorted(Comparator.comparingInt(match -> -match.players.size())
        ).toList();

        int count = 0;

        if(!matches.isEmpty()) {
            while (count < matches.size()) {
                Match match = matches.get(count);

                ItemBuilder builder = match.getArena().status == Arena.Status.STARTING?
                        new ItemBuilder(createFireCharge(Color.YELLOW)) : new ItemBuilder(createFireCharge(Color.GREEN));

                List<String> lore = new ArrayList<>(Main.getInstance().getValue().QUEUE_ARENA_LORE)
                        .stream().map(line -> line
                                .replace("$arena", match.getArena().name)
                                .replace("$players", String.valueOf(match.players.size())))
                        .collect(Collectors.toList());

                builder.setLore(lore);

                builder.meta.setDisplayName(Main.getInstance().getValue().QUEUE_ARENA_NAME
                        .replace("$arena", match.getArena().name));

                builder.meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                builder.meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                builder.setMeta();

                inventory.setItem(12 + count, builder.getItem());
                maps.put(12 + count, match);

                count++;
            }
        }

        if(count < 3) {
            List<Arena> freeArena = new ArrayList<>(arenaManager.arenas).stream().filter(arena ->
                    arena.status == null).collect(Collectors.toList());

            while (count < 3) {
                if(freeArena.isEmpty()) {
                    break;
                }

                int index = ThreadLocalRandom.current().nextInt(freeArena.size());
                Arena arena = freeArena.remove(index);
                Match match = matchManager.create(arena);

                ItemBuilder builder = new ItemBuilder(createFireCharge(Color.GREEN));

                List<String> lore = new ArrayList<>(Main.getInstance().getValue().QUEUE_ARENA_LORE)
                        .stream().map(line -> line
                                .replace("$arena", arena.name)
                                .replace("$players", "0"))
                        .collect(Collectors.toList());

                builder.setLore(lore);
                builder.meta.setDisplayName(Main.getInstance().getValue().QUEUE_ARENA_NAME
                        .replace("$arena", arena.name));
                builder.meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                builder.meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                builder.setMeta();

                maps.put(12 + count, match);

                inventory.setItem(12 + count, builder.getItem());

                count++;
            }
        }

    }

    public ItemStack createFireCharge(Color color) {
        ItemStack item = new ItemStack(Material.FIREWORK_CHARGE);

        FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();

        meta.setEffect(FireworkEffect.builder().withColor(color).build());

        item.setItemMeta(meta);

        return item;
    }

}
