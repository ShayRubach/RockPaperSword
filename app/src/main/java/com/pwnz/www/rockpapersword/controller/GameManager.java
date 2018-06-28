package com.pwnz.www.rockpapersword.controller;

import android.view.MotionEvent;

import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.Soldier;

public class GameManager {

    private final int SCREEN_BOARD_PADDING_FACTOR = 2;
    private final int HEIGHT_DIV = 10;
    private final int WIDTH_DIV  = 7;
    private Soldier focusedSoldier = null;
    private boolean hasFocusedSoldier = false;
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

    public void onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        focusedSoldier = board.getClickedSoldier(x,y);
        if(focusedSoldier != null){
            clearHighlights();
            hasFocusedSoldier = true;
            board.displaySoldierPath(focusedSoldier);
        }
        else{
            board.getMoveDirection(focusedSoldier, x, y);
        }

//        //focus on a new soldier
//        if(hasFocusedSoldier == false){
//            focusedSoldier = board.getClickedSoldier(x,y);
//            if(focusedSoldier != null){
//                hasFocusedSoldier = true;
//                board.displaySoldierPath(focusedSoldier);
//            }
//        }
//        else{
//            //check if focused soldier has been swapped
//            focusedSoldier = board.getClickedSoldier(x,y);
//
//            //user it attempting to move the soldier
//        }
//
//        focusedSoldier = board.getClickedSoldier(x,y);
//        if(focusedSoldier != null){
//            board.displaySoldierPath(focusedSoldier);
//        }



    }

    private void clearHighlights() {
        for(Soldier s : getBoard().getSoldierTeamB()){
            if(s.isHighlighted() == true)
                s.removeHighlight();
        }
    }

}
