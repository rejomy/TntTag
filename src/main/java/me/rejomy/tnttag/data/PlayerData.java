package me.rejomy.tnttag.data;

import java.util.UUID;

public class PlayerData {
    public UUID uuid;
    public int rounds, wins, games;
    public double winsAndLoses;

    public PlayerData() {}

    public void updateMatchBasedStats(int roundsSurvived, boolean winner) {
        rounds += roundsSurvived;
        games++;
        if (winner) wins++;

        // Calculate deaths based on the number of games and wins.
        int loses = games - wins;
        float winsAndLoses = loses > 0 ? (float) wins / loses : wins;
        this.winsAndLoses = ((int) (winsAndLoses * 100.0)) / 100.0;
    }
}
