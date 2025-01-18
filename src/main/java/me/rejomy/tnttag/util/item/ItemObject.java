package me.rejomy.tnttag.util.item;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemObject {
    public final ItemStack item;
    public final List<String> commands;

    public ItemObject(ItemStack item, List<String> commands) {
        this.item = item;
        this.commands = commands;
    }
}
