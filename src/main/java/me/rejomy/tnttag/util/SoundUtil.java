package me.rejomy.tnttag.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static Sound getSound(String name) {
        Sound sound;
        try {
            sound = Sound.valueOf(name);
        } catch (IllegalArgumentException exception) {
            sound = null;
        }

        return sound;
    }
}
