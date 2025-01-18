package me.rejomy.tnttag.util.inventory.impl;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.arena.Arena;
import me.rejomy.tnttag.arena.ArenaManager;
import me.rejomy.tnttag.match.Match;
import me.rejomy.tnttag.match.MatchManager;
import me.rejomy.tnttag.util.ColorUtil;
import me.rejomy.tnttag.util.ItemBuilder;
import me.rejomy.tnttag.util.inventory.AbstractInventory;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.util.*;
import java.util.stream.Collectors;

public class SpectateInventory extends AbstractInventory {
    private Random random = new Random();
    public HashMap<Integer, Match> maps = new HashMap<>();

    public SpectateInventory() {
        super(Main.getInstance().getConfig().getString("inventory.spectate.name"), 27);
    }

    public void put() {
        ArenaManager arenaManager = Main.getInstance().getArenaManager();
        MatchManager matchManager = Main.getInstance().getMatchManager();

        List<Match> matches = new ArrayList<>(matchManager.getMatches()).stream()
                .filter(match -> match.getArena().status != Arena.Status.ENDING
                    && match.getArena().status != Arena.Status.WAITING
                    && match.getArena().status != Arena.Status.STARTING)
                .sorted(Comparator.comparingInt(match -> -match.round)
                ).collect(Collectors.toList());

        int offset = 0;

        for (Match match : matches) {
            if(offset >= inventory.getSize()) {
                break;
            }

            ItemBuilder builder = new ItemBuilder(createFireCharge(Color.GREEN));

            List<String> lore = new ArrayList<>(Main.getInstance().getConfig().getStringList("inventory.spectate.arena.lore"))
                    .stream().map(line -> ColorUtil.toColor(line)
                            .replace("$arena", match.getArena().name)
                            .replace("$players", String.valueOf(match.players.size())))
                    .collect(Collectors.toList());

            builder.setLore(lore);

            builder.meta.setDisplayName(ColorUtil.toColor(
                    Main.getInstance().getConfig().getString("inventory.spectate.arena.name"))
                    .replace("$arena", match.getArena().name));

            builder.meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            builder.meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            builder.setMeta();

            inventory.setItem(offset, builder.getItem());
            maps.put(offset, match);

            offset++;
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