package com.example.moappteamproj;

public class Tiles {

    private int number;

    public int getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(int previousNumber) {
        this.previousNumber = previousNumber;
    }

    private int previousNumber; // 이전 턴의 타일 값


    public Tiles()
    {
        this.number = 0;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }
}
