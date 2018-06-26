package com.pwnz.www.rockpapersword.controller;

import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.model.Board;

public class GameManager {

    private final int SCREEN_BOARD_PADDING_FACTOR = 2;
    private final int HEIGHT_DIV = 10;
    private final int WIDTH_DIV  = 7;
    public int canvasW, canvasH;

    private Board board;
    private GamePanel panel;

    public GameManager(Board board, GamePanel panel) {
        this.board = board;
        this.panel = panel;
        this.canvasH = board.getCanvasH();
        this.canvasW = board.getCanvasW();
        this.panel.setManager(this);
        initBoard();
    }

    public Board getBoard() {
        return board;
    }

    public void initBoard() {
        getBoard().initBoard(SCREEN_BOARD_PADDING_FACTOR, HEIGHT_DIV, WIDTH_DIV);
    }
}
