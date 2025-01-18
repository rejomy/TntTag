package me.rejomy.tnttag.util.inventory;

import me.rejomy.tnttag.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInventory {
    protected Inventory inventory;
    public Inventory getInventory() {
        return inventory;
    }
    public HashMap<Integer, ItemStack> items = new HashMap<>();

    public AbstractInventory(String name, int size) {
        inventory = Bukkit.createInventory(null, size, ColorUtil.toColor(name));
    }

    private void fill() {
        for(Map.Entry<Integer, ItemStack> map : items.entrySet()) {
            int slot = map.getKey();
            ItemStack item = map.getValue();

            inventory.setItem(slot, item);
        }
    }

}
