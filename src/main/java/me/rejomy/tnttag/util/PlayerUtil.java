package me.rejomy.tnttag.util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {
    public static void sendMessage(Player player, String message) {
        if(message.isEmpty()) {
            return;
        }

        player.sendMessage(ColorUtil.toColor(message));
    }

    public static void clearEffects(Player player) {
        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

        for(PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }
}
