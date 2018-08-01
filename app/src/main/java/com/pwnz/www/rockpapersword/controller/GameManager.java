package com.pwnz.www.rockpapersword.controller;

import android.content.Context;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Managing the game logic and decisions. Fetches data from Board and deliver to GamePanel (UI Thread).
 * Responsible for calling of the initializations of the needed objects from Board before game starts.
 * Acts as light mvc "Controller" between the Board (model) and GamePanel (view)
 */
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
    private boolean isMenuOpen = false;
    private boolean inTie= false;
    private SoldierType newWeaponChoice = null;
    private int teamTurn;
    public int canvasW, canvasH;
    private String matchFighters = null;

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
        shuffleBothTeams();
        randTeamTurn();

    }

    /**
     * shuffle the tiles of all soldiers to create diversity
     */
    private void shuffleBothTeams() {
        board.shuffleTeams(board.getSoldierTeamA());
        board.shuffleTeams(board.getSoldierTeamB());
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

        if(isInTie()){
            handleTie(event.getX(), event.getY());
            lookForPotentialMatch(potentialInitiator);
            return;
        }
        //Player has focused (clicked) a soldier but yet tried to moved
        if(board.getClickedSoldier(x,y) != null) {
            markSelectedSoldier(event.getX(), event.getY());
        }
        else if(menuButtonWasPressed(x, y)){
            setMenuOpen(true);
        }
        else if(menuOpen()){
            panel.pause();
            if(resumeWasPressed(x, y)){
                setMenuOpen(false);
            }
            if(backToMenuWasPressed(x, y)){
                setMenuOpen(false);
            }
            panel.resume();
        }
        //Player has a legit focused soldier and attempted to move to new suggested (arrow) tile
        else if(hasFocusedSoldier && !menuOpen()){
            handlePlayerMovementRequest(event.getX(), event.getY());
            setTurnThinkingTimeSleep(900);
        }

        //look for another potential match after player has made a move
        if(possibleMatch) {
            lookForPotentialMatch(potentialInitiator);
            if(isInTie())
                return;
        }

        //A.I will instantly play after Player's turn
        if(teamTurn == TEAM_A_TURN && !menuOpen()){
            handleAIMovementRequest();
        }

        //look for another potential match after AI has made a move
        if(possibleMatch) {
            setTurnThinkingTimeSleep(500);
            lookForPotentialMatch(potentialInitiator);
        }
    }

    private void handleTie(float x, float y) {
        //todo: remove debug prints eventually.
        Log.d("TIE_DBG", "handling click after tie.\n");
        Log.d("TIE_DBG", "BEFORE REFRESHING WEAPONS.\n");
        Log.d("TIE_DBG", "potential = " + potentialInitiator + "\n");
        Log.d("TIE_DBG", "opponent  = " + opponent + "\n");
        panel.pause();
        Log.d("TIE_DBG", "picking new weapon..\n");
        newWeaponChoice = board.getNewPickedWeapon(x, y);
        Log.d("TIE_DBG", "picked = " + newWeaponChoice + "\n");

        refreshSoldierType(opponent, newWeaponChoice);
        refreshSoldierType(potentialInitiator, randWeapon(newWeaponChoice));

        Log.d("TIE_DBG", "AFTER REFRESHING WEAPONS.\n");
        Log.d("TIE_DBG", "potential = " + potentialInitiator + "\n");
        Log.d("TIE_DBG", "opponent  = " + opponent + "\n");

        possibleMatch = true;   //this will proc another fight
        setInTie(false);
        panel.resume();
    }

    /**
     * handles the AI automatic movement on its turn
     */
    private void handleAIMovementRequest() {
        panel.pause();

        clearHighlights();
        playAsAI();
        potentialInitiator = AISoldier;
        possibleMatch = true;
        teamTurn = TEAM_B_TURN;
        panel.resetClock();

        panel.resume();
        MainMenuActivity.getSoundEffects().play(R.raw.move_enemy, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
    }

    /**
     * handles the movement request from a player. validates the new tiles and indicates if potential
     * match is possible or not.
     * @param x clicked x pos
     * @param y clicked y pos
     */
    private void handlePlayerMovementRequest(float x, float y) {
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

    /**
     * highlights and set the focused soldier at any current time
     * @param x soldier x pos
     * @param y soldier y pos
     */
    private void markSelectedSoldier(float x, float y) {
        panel.pause();
        focusedSoldier = board.getClickedSoldier(x, y);
        clearHighlights();
        hasFocusedSoldier = true;
        possibleMatch = false;
        board.displaySoldierPath(focusedSoldier);
        panel.resume();
    }

    /**
     * after tie event, both soldiers are given with new weapons. this method updates its view resources.
     * @param soldier trivial
     * @param newWeaponChoice trivial
     */
    private void refreshSoldierType(Soldier soldier, SoldierType newWeaponChoice) {
        soldier.setSoldierType(newWeaponChoice);
        soldier.setSoldierAnimationSpriteByType();
        soldier.initBitmapsByType(getAppResources());
        soldier.setSoldierBitmap(soldier.getSoldierRevealedBitmap());
    }

    private boolean resumeWasPressed(float x, float y) {
        return board.resumeWasPressed(x, y);
    }

    private boolean backToMenuWasPressed(float x, float y) {
        return board.backToMenuWasPressed(x, y);
    }

    private boolean menuButtonWasPressed(float x, float y) {
        return board.menuButtonWasPressed(x, y);
    }

    /**
     * //initiate a fake "thinking time" before next turn. simulate a 'real' game.
     * @param sleepTime time to wait in milliseconds.
     */
    public void setTurnThinkingTimeSleep(int sleepTime){
        try {
            Thread.currentThread().sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * After a move has been initiated, we wish to check the surrounding soldiers for a possible match.
     * @param initiator the original initiator who moved and started the match.
     *                           if it is not the AI, we force swap it with the @opponent to be able
     *                           to simplify the switch cases on match. this doesn't change logic.
     * @return none
     */
    private void lookForPotentialMatch(Soldier initiator) {
        Log.d("TIE_DBG", "lookForPotentialMatch called");
        RPSMatchResult matchResult;

        if(initiator == null) {
            return;
        }

        if(!isInTie())
            opponent = board.getFirstSurroundingOpponent(initiator);

        if(opponent != null) {

            if( initiator.getTeam() != Board.TEAM_A){
                //swap refrences
                Soldier temp = initiator;
                initiator = opponent;
                opponent = temp;
            }

            Log.d("TIE_DBG", "POT = " + initiator + "\n");
            Log.d("TIE_DBG", "VS. \n");
            Log.d("TIE_DBG", "OPP =" + opponent + "\n");

            panel.pause();
            panel.stopClock();
            setMatchOn(true);

            updateMatchFighters(initiator, opponent);
            matchResult = match(initiator, opponent);

            Log.d("MEGA_DBG","matchResult: " + matchResult + "\n");

            handleMatchResult(matchResult, initiator);

            //safely reassign potentialInitiator to its initiator reference. this was a bug for some reason.
            potentialInitiator = initiator;
            possibleMatch = false;
            panel.resume();
        }
    }

    /**
     * decides what actions should be taken following the match result
     * @param matchResult
     * @param initiator
     */
    private void handleMatchResult(RPSMatchResult matchResult, Soldier initiator) {
        Tile newTile;
        boolean alreadyEliminated = false;
        switch (matchResult){
            case TIE:
                Log.d("TIE_DBG", "calling rematch.\n");
                setInTie(true);
                initiator.setRevealed(true);
                initiator.setSoldierBitmap(initiator.getSoldierRevealedBitmap());
                break;
            case BOTH_ELIMINATED:
                eliminateBoth(initiator, opponent);
                break;

            case TEAM_A_WON_THE_MATCH:
                newTile = opponent.getTile();
                Log.d("MEGA_DBG", "moving " + initiator + "\nto" + newTile);
                eliminateSoldier(opponent);
                alreadyEliminated = true;
                moveSoldier(initiator, newTile);
            case REVEAL_TEAM_A:
                Log.d("MEGA_DBG", "revealing A\n");
                initiator.setRevealed(true);
                initiator.setSoldierBitmap(initiator.getSoldierRevealedBitmap());
                if(!alreadyEliminated)
                    eliminateSoldier(opponent);
                break;

            case TEAM_B_WON_THE_MATCH:
                newTile = initiator.getTile();
                eliminateSoldier(initiator);
                alreadyEliminated = true;
                moveSoldier(opponent, newTile);
                Log.d("MEGA_DBG", "moving " + opponent + "\nto\n" + newTile);
            case REVEAL_TEAM_B:
                Log.d("MEGA_DBG", "revealing B\n");
                opponent.setRevealed(true);
                if(!alreadyEliminated)
                    eliminateSoldier(initiator);
                break;

            case TEAM_A_WINS_THE_GAME:
                finishGame(Board.TEAM_A);
                break;
            case TEAM_B_WINS_THE_GAME:
                finishGame(Board.TEAM_B);
                break;
        }
    }

    private void updateMatchFighters(Soldier teamASoldier, Soldier teamBSoldier){


        matchFighters = "ashes_vs_king"; return;

        //todo: turn this on when all animations are ready
        /*
        switch (teamASoldier.getSoldierType()){
            case SWORDMASTER:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "swordmaster_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "swordmaster_vs_shieldon"; return;
                    case STONE:         matchFighters = "swordmaster_vs_stone"; return;
                    case KING:          matchFighters = "swordmaster_vs_king"; return;
                    case PEPPER:        matchFighters = "swordmaster_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "swordmaster_vs_swordmaster"; return;
                }
            case PEPPER:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "pepper_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "pepper_vs_shieldon"; return;
                    case STONE:         matchFighters = "pepper_vs_stone"; return;
                    case KING:          matchFighters = "pepper_vs_king"; return;
                    case PEPPER:        matchFighters = "pepper_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "pepper_vs_swordmaster"; return;
                }
            case KING:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "king_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "king_vs_shieldon"; return;
                    case STONE:         matchFighters = "king_vs_stone"; return;
                    case KING:          matchFighters = "king_vs_king"; return;
                    case PEPPER:        matchFighters = "king_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "king_vs_swordmaster"; return;
                }
            case STONE:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "stone_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "stone_vs_shieldon"; return;
                    case STONE:         matchFighters = "stone_vs_stone"; return;
                    case KING:          matchFighters = "stone_vs_king"; return;
                    case PEPPER:        matchFighters = "stone_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "stone_vs_swordmaster"; return;
                }
            case SHIELDON:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "shieldon_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "shieldon_vs_shieldon"; return;
                    case STONE:         matchFighters = "shieldon_vs_stone"; return;
                    case KING:          matchFighters = "shieldon_vs_king"; return;
                    case PEPPER:        matchFighters = "shieldon_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "shieldon_vs_swordmaster"; return;
                }
            case ASHES:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = "ashes_vs_ashes"; return;
                    case SHIELDON:      matchFighters = "ashes_vs_shieldon"; return;
                    case STONE:         matchFighters = "ashes_vs_stone"; return;
                    case KING:          matchFighters = "ashes_vs_king"; return;
                    case PEPPER:        matchFighters = "ashes_vs_pepper"; return;
                    case SWORDMASTER:   matchFighters = "ashes_vs_swordmaster"; return;
                }
        }

        //default test:
        matchFighters = "ashes_vs_king"; return;

        */
    }

    /**
     * Go to the list that holds all of the soldiers objects who are used in drawMatch(), look
     * for the types to be displayed in match, and put them on the beginning of the list for a
     * fast get() on the UI thread later.
     * @param soldier   the soldier reference from the board from the SoldierType that
     *                  should be on the match()
     * @param list      holds the pre-made soldiers that used in drawMatch.
     * @param pairIndex the index to be swapped to.
     */
    private void findAndSwap(Soldier soldier, ArrayList<Soldier> list, int pairIndex) {
        for (int i = 0; i < list.size() && !list.isEmpty(); i++) {
            if(list.get(i).getSoldierType() == soldier.getSoldierType()){
                Collections.swap(list, i,pairIndex);
            }
        }
    }

    /**
     * updates the finish game flag and set the winner team.
     * @param team
     */
    private void finishGame(int team) {
        winningTeam = team;
    }

    private SoldierType randWeapon(SoldierType playerChosenWeapon){
        Random rand = new Random();
        SoldierType type;
        int i;

        do {
            i = rand.nextInt(Soldier.getUniqueSoldierTypes().size() - 1);
            type = Soldier.getUniqueSoldierTypes().get(i);
        } while(type == SoldierType.KING || type == SoldierType.SHIELDON || type == SoldierType.ASHES || type == playerChosenWeapon );

        return Soldier.getUniqueSoldierTypes().get(i);
    }

    private void eliminateBoth(Soldier potentialInitiator, Soldier opponent){
        getBoard().eliminateBoth(potentialInitiator, opponent);
    }

    private void eliminateSoldier(Soldier soldier){
        getBoard().eliminateSoldier(soldier);
    }

    /**
     * RPS logic. determines the winner according to the pair dueling.
     * @param potentialInitiator    referred to as the AI soldier
     * @param opponent              referred to as the Player soldier
     * @return the result of the match
     */
    private RPSMatchResult match(Soldier potentialInitiator, Soldier opponent) {
        Log.d("MEGA_DBG","\n match:  " +  potentialInitiator + "\nVS.\n" + opponent + "\n");
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

    /**
     * forcing a random movement from the AI soldier
     * @return  none
     */
    private void playAsAI() {
        AISoldier = board.getRandomSoldier();
        Tile tile = board.getTraversalTile();

        moveSoldier(AISoldier, tile);

        //todo: add movement sound FX
        //MainMenuActivity.getSoundEffects().play(R.raw.move_enemy, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
        clearHighlights();
        hasFocusedSoldier = false;
    }

    private void moveSoldier(Soldier focusedSoldier, Tile tile) {
//        Log.d("MEGA_DBG", "MOVING " + focusedSoldier + " FROM " + focusedSoldier.getTile() + " TO " + tile);
        focusedSoldier.getTile().setOccupied(false);
        focusedSoldier.getTile().setCurrSoldier(null);
        focusedSoldier.setTile(tile);
        focusedSoldier.getTile().setOccupied(true);
        focusedSoldier.getTile().setCurrSoldier(focusedSoldier);
    }

    /**
     * cleans the pathArrows list and removes highlights from the clicked soldier
     * @return none
     */
    private void clearHighlights() {
        board.getPathArrows().clear();

        for(Soldier s : getBoard().getSoldierTeamB()){
            if(s.isHighlighted())
                s.removeHighlight();
        }
    }

    /**
     * swap turns between player and AI (in case of clock timeout for example)
     */
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

    public Resources getAppResources() {
        return panel.getResources();
    }

    public static int getWinningTeam() {
        return winningTeam;
    }

    public static void setWinningTeam(int winningTeam) {
        GameManager.winningTeam = winningTeam;
    }

    public Context getPanelContext() {
        return panel.getContext();
    }

    public boolean menuOpen() {
        return isMenuOpen;
    }

    public void setMenuOpen(boolean menuOpen) {
        isMenuOpen = menuOpen;
    }

    public boolean isInTie() {
        return inTie;
    }

    public void setInTie(boolean inTie) {
        this.inTie = inTie;
    }

    public String getMatchFighters() {
        return matchFighters;
    }
}
