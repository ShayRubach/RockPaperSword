package com.pwnz.www.rockpapersword.controller;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierMovement;
import com.pwnz.www.rockpapersword.model.Tile;

public class GameManager {

    public static final int TEAM_A_TURN = 0;
    public static final int TEAM_B_TURN = 1;

    private final int SCREEN_BOARD_PADDING_FACTOR = 2;
    private final int HEIGHT_DIV = 10;
    private final int WIDTH_DIV  = 7;
    private Soldier focusedSoldier = null;
    private Soldier AISoldier = null;
    private boolean hasFocusedSoldier = false;
    private int teamTurn;
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
        randTeamTurn();
    }

    private void randTeamTurn() {
        teamTurn = (int )(Math.random() * 1 + 0);
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

        //A.I:
        if(teamTurn == TEAM_A_TURN){
            clearHighlights();
            playAsAI();
            teamTurn = TEAM_B_TURN;
        }
        //USER:
        else if(board.getClickedSoldier(x,y) != null) {
            focusedSoldier = board.getClickedSoldier(x, y);
            clearHighlights();
            hasFocusedSoldier = true;
            board.displaySoldierPath(focusedSoldier);
        }
        else if(hasFocusedSoldier == true){
            Tile newTile = board.getTileAt(x, y);

            if(newTile != null){
                moveSoldier(focusedSoldier, newTile);
                clearHighlights();
                hasFocusedSoldier = false;
                teamTurn = TEAM_A_TURN;
            }
        }

        //todo: implement match logic
        //seekForMatch();
    }

    private void playAsAI() {
        AISoldier = board.getRandomSoldier();
        Tile tile = board.getTraversalTile();
        moveSoldier(AISoldier, tile);
        clearHighlights();
        hasFocusedSoldier = false;
    }


    private void moveSoldier(Soldier focusedSoldier, Tile tile) {
        clearTileByRectPos(focusedSoldier.getRectPosition());
        focusedSoldier.setRectPosition(tile.getRect());

    }

    private void clearTileByRectPos(Rect rectPosition) {
        //clear old tile
        Integer[] xyPos = new Integer[2];
        getBoard().rectPositionToTileIndex(rectPosition, xyPos );
        getBoard().getTiles()[xyPos[0]][xyPos[1]].setOccupied(false);
    }

    private void clearHighlights() {
        board.getPathArrows().clear();

        for(Soldier s : getBoard().getSoldierTeamB()){
            if(s.isHighlighted() == true)
                s.removeHighlight();
        }
    }

}
