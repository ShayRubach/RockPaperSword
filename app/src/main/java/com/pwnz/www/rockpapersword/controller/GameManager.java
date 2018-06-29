package com.pwnz.www.rockpapersword.controller;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.RPSMatchResult;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.Tile;

public class GameManager {

    public static final int TEAM_A_TURN = 0;
    public static final int TEAM_B_TURN = 1;

    private final int SCREEN_BOARD_PADDING_FACTOR = 2;
    private final int HEIGHT_DIV = 10;
    private final int WIDTH_DIV  = 7;
    private Soldier focusedSoldier = null;
    private Soldier AISoldier = null;
    private Soldier potentialInitiator = null;
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
            System.out.println("____POTENTIAL____, AISoldier="+AISoldier);
            potentialInitiator = AISoldier;
            teamTurn = TEAM_B_TURN;
        }
        //USER:
        else if(board.getClickedSoldier(x,y) != null) {
            focusedSoldier = board.getClickedSoldier(x, y);
            clearHighlights();
            hasFocusedSoldier = true;
            board.displaySoldierPath(focusedSoldier);
        }
        else if(hasFocusedSoldier){
            Tile newTile = board.getTileAt(x, y);

            if(newTile != null){
                moveSoldier(focusedSoldier, newTile);
                System.out.println("__POTENTIAL__, focusedSoldier="+focusedSoldier);
                potentialInitiator = focusedSoldier;
                clearHighlights();
                hasFocusedSoldier = false;
                teamTurn = TEAM_A_TURN;
            }
        }

        lookForPotentialMatch(potentialInitiator);
    }

    //after a move has been initiated, we wish to check the surrounding soldiers for a possible match.
    private void lookForPotentialMatch(Soldier potentialInitiator) {
        Soldier opponent;
        RPSMatchResult matchResult;

        System.out.println("lookForPotentialMatch: called, ");

        if(potentialInitiator == null)
            return;

        opponent = board.getFirstSurroundingOpponent(potentialInitiator);
        System.out.println("getFirstSurroundingOpponent: Done, ");
        System.out.println("lookForPotentialMatch: opponent="+opponent);

        if(opponent != null) {
            matchResult = match(potentialInitiator, opponent);
            System.out.println("MATCH RESULT: " + matchResult);
        }
    }

    private RPSMatchResult match(Soldier aiSoldier, Soldier opponent) {
        switch (aiSoldier.getSoldierType()){
            case STONE:
                switch (opponent.getSoldierType()){
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    case STONE:         return RPSMatchResult.TIE;
                    case SWORDMASTER:   return RPSMatchResult.TEAM_A_WON_THE_MATCH;
                    case PEPPER:        return RPSMatchResult.TEAM_B_WON_THE_MATCH;
                    case SHIELDON:      return RPSMatchResult.BOTH_ELIMINATED;
                }
                break;
            case PEPPER:
                switch (opponent.getSoldierType()){
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    case STONE:         return RPSMatchResult.TEAM_A_WON_THE_MATCH;
                    case PEPPER:        return RPSMatchResult.TIE;
                    case SWORDMASTER:   return RPSMatchResult.TEAM_B_WON_THE_MATCH;
                    case SHIELDON:      return RPSMatchResult.BOTH_ELIMINATED;
                }
                break;
            case SWORDMASTER:
                switch (opponent.getSoldierType()){
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    case PEPPER:        return RPSMatchResult.TEAM_A_WON_THE_MATCH;
                    case SWORDMASTER:   return RPSMatchResult.TIE;
                    case STONE:         return RPSMatchResult.TEAM_B_WON_THE_MATCH;
                    case SHIELDON:      return RPSMatchResult.BOTH_ELIMINATED;
                }
                break;
            case SHIELDON:
                switch (opponent.getSoldierType()){
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    default:            return RPSMatchResult.BOTH_ELIMINATED;
                }
            case ASHES:
                switch (opponent.getSoldierType()){
                    case ASHES:         return RPSMatchResult.BOTH_ELIMINATED;
                    default:            return RPSMatchResult.REVEAL_TEAM_B;
                }
            case KING:
                switch (opponent.getSoldierType()){
                    //todo: solve king vs king
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    default:            return RPSMatchResult.TEAM_B_WINS_THE_GAME;
                }

            //todo: implement this? random a weapon maybe?
            case LASSO:
                break;

        }
        return RPSMatchResult.TIE;
    }

    private void playAsAI() {
        AISoldier = board.getRandomSoldier();
        Tile tile = board.getTraversalTile();
        moveSoldier(AISoldier, tile);
        clearHighlights();
        hasFocusedSoldier = false;
    }


    private void moveSoldier(Soldier focusedSoldier, Tile tile) {
        focusedSoldier.getTile().setOccupied(false);
        focusedSoldier.setTile(tile);
        focusedSoldier.getTile().setOccupied(true);
        focusedSoldier.getTile().setCurrSoldier(focusedSoldier);


    }

    private void clearHighlights() {
        board.getPathArrows().clear();

        for(Soldier s : getBoard().getSoldierTeamB()){
            if(s.isHighlighted())
                s.removeHighlight();
        }
    }

}
