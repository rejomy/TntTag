package me.rejomy.tnttag.util;

public class PlayerTnt {
    private boolean hasTnt;
    public boolean isTnt() {
        return hasTnt;
    }
    public void setHasTntStatus(boolean hasTnt) {
        lastTntTime = System.currentTimeMillis();
        this.hasTnt = hasTnt;
    }
    public long lastTntTime;
    public int count;
    public boolean spectator;
}
