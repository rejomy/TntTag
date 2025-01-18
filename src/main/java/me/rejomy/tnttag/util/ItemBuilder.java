package me.rejomy.tnttag.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    public ItemStack item;
    public ItemStack getItem() {
        return item;
    }
    public ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        meta = item.getItemMeta();
    }

    public void setLore(String... lines) {
        meta.setLore(Arrays.stream(lines)
                        .map(ColorUtil::toColor)
                        .collect(Collectors.toList()));
        setMeta();
    }

    public void setLore(List<String> lines) {
        meta.setLore(lines.stream()
                .map(ColorUtil::toColor)
                .collect(Collectors.toList()));
        setMeta();
    }

    public void setDisplayName(String name) {
        meta.setDisplayName(ColorUtil.toColor(name));
        setMeta();
    }

    public void setMeta() {
        item.setItemMeta(meta);
    }
}
