package com.pwnz.www.rockpapersword.model;

public class Board {
    int cols, rows;
    Tile[][] board;
    Soldier[][] soldiers;

    public Board(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        board = new Tile[cols][rows];
        soldiers = new Soldier[cols][rows];
    }
}
