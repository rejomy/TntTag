package me.rejomy.tnttag.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager {
    public static final List<PlayerData> USERS = new ArrayList<>();

    public static PlayerData get(UUID uuid) {
        return USERS.stream().filter(data -> data.uuid == uuid).findAny().orElse(null);
    }
    public static void add(PlayerData data) {
        USERS.add(data);
    }
}
