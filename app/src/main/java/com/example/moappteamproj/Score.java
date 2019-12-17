package com.example.moappteamproj;

public class Score {

    public int getCurrent() {
        return current;
    }

    public int getBest() {
        return best;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setBest(int best) {
        this.best = best;
    }

    private int current=0;
    private int best=0;
}
