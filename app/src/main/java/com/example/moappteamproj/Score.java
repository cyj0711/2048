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

    public int getPreviousSocre() {
        return previousSocre;
    }

    public void setPreviousSocre(int previousSocre) {
        this.previousSocre = previousSocre;
    }

    private int previousSocre = 0;  // undo 했을 때 점수도 이전 턴으로 돌리기위한 변수
    private int current=0;
    private int best=0;
}
