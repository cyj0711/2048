package com.example.moappteamproj;

public class Tiles {

    public int number;
    public int row; // 행 좌표
    public int col; // 열 좌표


    public Tiles(int row, int col)
    {
        this.number = 0;
        this.row=row;
        this.col=col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getNumber() {
        return number;
    }
    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
}
