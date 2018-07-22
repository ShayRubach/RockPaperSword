package com.pwnz.www.rockpapersword.controller;

import android.content.res.Resources;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;

import com.pwnz.www.rockpapersword.Activities.MainMenuActivity;
import com.pwnz.www.rockpapersword.Activities.SettingsActivity;
import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.RPSMatchResult;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierType;
import com.pwnz.www.rockpapersword.model.Tile;

public class GameManager {

    public static final int TEAM_A_TURN = 0;
    public static final int TEAM_B_TURN = 1;

    private final int SCREEN_BOARD_PADDING_FACTOR = 2;
    private final int HEIGHT_DIV = 10;
    private final int WIDTH_DIV  = 7;
    private static int winningTeam = -1;
    private Soldier focusedSoldier = null;
    private Soldier AISoldier = null;
    private Soldier potentialInitiator = null;
    private Soldier opponent = null;
    private boolean hasFocusedSoldier = false;
    private boolean possibleMatch = false;
    private boolean isMatchOn = false;
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
        this.panel.setRedraw(true);
        this.board.setManager(this);
        initBoard();
        initClock();
        randTeamTurn();
    }

    private void initClock() {

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
        panel.setRedraw(true);

        //Player has focused a soldier but yet tried to moved
        if(board.getClickedSoldier(x,y) != null) {
            focusedSoldier = board.getClickedSoldier(x, y);
            clearHighlights();
            hasFocusedSoldier = true;
            possibleMatch = false;
            board.displaySoldierPath(focusedSoldier);
        }
        //Player has a legit focused soldier and attempted to move to new tile
        else if(hasFocusedSoldier){
            panel.pause();
            Tile newTile = board.getTileAt(x, y);

            //make sure tile is traversal and in legit location on screen
            if(newTile != null){
                clearHighlights();
                moveSoldier(focusedSoldier, newTile);
                MainMenuActivity.getSoundEffects().play(R.raw.move_self, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
                potentialInitiator = focusedSoldier;
                hasFocusedSoldier = false;
                possibleMatch = true;
                teamTurn = TEAM_A_TURN;

            }
            panel.resume();
        }

        //look for another potential match after player has made a move
        if(possibleMatch) {
            lookForPotentialMatch(potentialInitiator);
        }

        //A.I will instantly play after Player's turn
        if(teamTurn == TEAM_A_TURN){
            panel.pause();

            clearHighlights();
            playAsAI();
            potentialInitiator = AISoldier;
            possibleMatch = true;
            teamTurn = TEAM_B_TURN;
            panel.resetClock();

            panel.resume();
        }

        //look for another potential match after player has made a move
        if(possibleMatch) {
            lookForPotentialMatch(potentialInitiator);
        }

    }

    //after a move has been initiated, we wish to check the surrounding soldiers for a possible match.
    private void lookForPotentialMatch(Soldier potentialInitiator) {
        RPSMatchResult matchResult;

        if(potentialInitiator == null)
            return;

        opponent = board.getFirstSurroundingOpponent(potentialInitiator);

        if(opponent != null) {
            panel.pause();
            panel.stopClock();
            setMatchOn(true);
            matchResult = match(potentialInitiator, opponent);
            handleMatchResult(matchResult);
            panel.resume();
        }
    }

    private void handleMatchResult(RPSMatchResult matchResult) {
        Tile newTile = null;

        if( potentialInitiator.getTeam() != Board.TEAM_A){
            //swap refrences
            Soldier temp = potentialInitiator;
            potentialInitiator = opponent;
            opponent = temp;
        }

        Log.d("RESULT_DBG","MATCH SODLIERS: \n");
        Log.d("RESULT_DBG","POT: " + potentialInitiator + "\n");
        Log.d("RESULT_DBG","OPP: " + opponent + "\n");
        Log.d("RESULT_DBG","MATCH RESULT: " + matchResult + "\n");

        switch (matchResult){
            case TIE:
                rematch(potentialInitiator, opponent);
                break;
            case BOTH_ELIMINATED:
                eliminateBoth(potentialInitiator, opponent);
                break;
            case TEAM_A_WON_THE_MATCH:
                newTile = opponent.getTile();
                eliminateSoldier(opponent);
                moveSoldier(potentialInitiator, newTile);
                break;
            case TEAM_B_WON_THE_MATCH:
                newTile = potentialInitiator.getTile();
                eliminateSoldier(potentialInitiator);
                moveSoldier(opponent, newTile);
                break;

            case TEAM_A_WINS_THE_GAME:
                finishGame(Board.TEAM_A);
                break;
            case TEAM_B_WINS_THE_GAME:
                finishGame(Board.TEAM_B);
                break;
            case REVEAL_TEAM_A:
            case REVEAL_TEAM_B:
                //todo: change this logic, this is temporarily.
                eliminateBoth(potentialInitiator, opponent);
        }
    }

    private void finishGame(int team) {
        winningTeam = team;
    }

    private void rematch(Soldier potentialInitiator, Soldier opponent){
        //TODO: temporarily returns BOTH_ELIMINATED . Implement this shit later
        eliminateBoth(potentialInitiator, opponent);
    }
    private void eliminateBoth(Soldier potentialInitiator, Soldier opponent){
        getBoard().eliminateBoth(potentialInitiator, opponent);
    }
    private void eliminateSoldier(Soldier soldier){
        getBoard().eliminateSoldier(soldier);
    }

    private RPSMatchResult match(Soldier potentialInitiator, Soldier opponent) {

        if( potentialInitiator.getTeam() != Board.TEAM_A){
            //swap refrences
            Soldier temp = potentialInitiator;
            potentialInitiator = opponent;
            opponent = temp;
        }

        switch (potentialInitiator.getSoldierType()){
            //todo: impl this later. LASSO == STONE at the moment - @shay
            case LASSO:
            case STONE:
                switch (opponent.getSoldierType()){
                    case KING:          return RPSMatchResult.TEAM_A_WINS_THE_GAME;
                    case ASHES:         return RPSMatchResult.REVEAL_TEAM_A;
                    case LASSO:
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
                    case LASSO:
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
                    case LASSO:
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

        }
        return RPSMatchResult.TIE;
    }

    private void playAsAI() {
        AISoldier = board.getRandomSoldier();
        Tile tile = board.getTraversalTile();
        moveSoldier(AISoldier, tile);

        //todo: turn this on with a delay?
        //MainMenuActivity.getSoundEffects().play(R.raw.move_enemy, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
        clearHighlights();
        hasFocusedSoldier = false;
    }


    private void moveSoldier(Soldier focusedSoldier, Tile tile) {
        focusedSoldier.getTile().setOccupied(false);
        focusedSoldier.getTile().setCurrSoldier(null);
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

    public void swapTurns() {
        if(teamTurn == TEAM_B_TURN) {
            teamTurn = TEAM_A_TURN;
            clearHighlights();
            playAsAI();
            potentialInitiator = AISoldier;
            lookForPotentialMatch(potentialInitiator);
            teamTurn = TEAM_B_TURN;
        }
        else {
            //do nothing, we still haven't finished playing as AI.
            return;
        }
    }

    public boolean getIsMatchOn() {
        return isMatchOn;
    }

    public void setMatchOn(boolean matchOn) {
        isMatchOn = matchOn;
    }

    public Soldier getPotentialInitiator() {
        return potentialInitiator;
    }

    public Soldier getOpponent() {
        return opponent;
    }

    public Soldier getFightingSoldier(int team) {

        if(potentialInitiator == null || opponent == null)
            return null;

        //get the type of the fighting soldier of the requested team:
        SoldierType type = (team == potentialInitiator.getTeam() ) ?
                potentialInitiator.getSoldierType() : opponent.getSoldierType();

        return getBoard().getFightingSoldier(type, team);
    }

    public Resources getAppResources() {
        return panel.getResources();
    }

    public static int getWinningTeam() {
        return winningTeam;
    }
}
