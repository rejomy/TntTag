package me.rejomy.tnttag.util.item;

import me.rejomy.tnttag.Main;
import me.rejomy.tnttag.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsConfig {
    private final File file = new File(Main.getInstance().getDataFolder(), "items.yml");
    private YamlConfiguration config;
    public HashMap<Integer, ItemObject> WAITING_ITEMS = new HashMap<>();
    public HashMap<Integer, ItemObject> ENDING_ITEMS = new HashMap<>();

    public void load() {
        if(!file.exists()) {
            Main.getInstance().saveResource("items.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        fillItem(ENDING_ITEMS, "ending");
        fillItem(WAITING_ITEMS, "waiting.");
    }

    private void fillItem(HashMap<Integer, ItemObject> map, String section) {
        for(String slotSection : config.getConfigurationSection(section).getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotSection);

                try {
                    Material material = Material.valueOf(config.getString(section + "." + slotSection + ".type"));

                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();

                    String name = config.getString(section + "." + slotSection + ".name");

                    if(name != null && !name.isEmpty()) {
                        meta.setDisplayName(ColorUtil.toColor(name));
                    }

                    List<String> lore = config.getStringList(section + "." + slotSection + ".lore");

                    if(lore != null) {
                        lore.replaceAll(ColorUtil::toColor);
                        meta.setLore(lore);
                    }

                    item.setItemMeta(meta);

                    List<String> commands = config.getStringList(section + "." + slotSection + ".action");

                    if(commands != null) {
                        commands.replaceAll(ColorUtil::toColor);
                    } else {
                        commands = new ArrayList<>();
                    }

                    ItemObject itemObject = new ItemObject(item, commands);
                    map.put(slot, itemObject);
                } catch (IllegalArgumentException exception) {
                    Main.getInstance().getLogger().severe("items.yml -> ending -> " + slotSection + " -> "
                            + config.getString(section + "." + slotSection + ".type") + " is incorrect material!");
                }
            } catch (NumberFormatException exception) {
                Main.getInstance().getLogger().severe("items.yml -> " + section + " -> slot " + slotSection +
                        " is not a number!");
            }
        }
    }
}
