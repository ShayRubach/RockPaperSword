package com.pwnz.www.rockpapersword.controller;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;

import com.pwnz.www.rockpapersword.Activities.MainMenuActivity;
import com.pwnz.www.rockpapersword.Activities.SettingsActivity;
import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.GameStorage;
import com.pwnz.www.rockpapersword.model.RPSMatchResult;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierType;
import com.pwnz.www.rockpapersword.model.Tile;

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
    private int haxCount = 0;
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
            checkHax(x,y);
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

    /**
     * EasterEgg time.
     * What could it be?
     * @param x easter egg assistant
     * @param y easter egg assistant
     */
    private void checkHax(float x, float y) {
        if(board.getClickedSoldier(x,y).getSoldierType() == SoldierType.KING)
            haxCount++;
        if(haxCount == 5){
            hax();
            haxCount = 0;
        }
    }

    private void handleTie(float x, float y) {

        panel.pause();
        newWeaponChoice = board.getNewPickedWeapon(x, y);

        //if user clicks outside the new weapon choices area, do nothing.
        if(newWeaponChoice == null){
            return;
        }

        refreshSoldierType(opponent, newWeaponChoice);
        refreshSoldierType(potentialInitiator, randWeapon(newWeaponChoice));

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
        soldier.getBitmapsByType();
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

            panel.pause();
            panel.stopClock();
            setMatchOn(true);

            //todo: this currently returns constant result due to unimplemented match animations
            updateMatchFighters(initiator, opponent);
            matchResult = match(initiator, opponent);
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
                setInTie(true);
                initiator.setRevealed(true);
                initiator.setSoldierBitmap(initiator.getSoldierRevealedBitmap());
                break;
            case BOTH_ELIMINATED:
                eliminateBoth(initiator, opponent);
                break;

            case TEAM_A_WON_THE_MATCH:
                MainMenuActivity.getSoundEffects().play(R.raw.evil_laugh_sound, SettingsActivity.sfxGeneralVolume/2, SettingsActivity.sfxGeneralVolume/2);
                newTile = opponent.getTile();
                eliminateSoldier(opponent);
                alreadyEliminated = true;
                moveSoldier(initiator, newTile);
                MainMenuActivity.getSoundEffects().play(R.raw.move_enemy, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
            case REVEAL_TEAM_A:
                initiator.setRevealed(true);
                initiator.setSoldierBitmap(initiator.getSoldierRevealedBitmap());
                if(!alreadyEliminated)
                    eliminateSoldier(opponent);
                break;

            case TEAM_B_WON_THE_MATCH:
                MainMenuActivity.getSoundEffects().play(R.raw.win_match_sound, SettingsActivity.sfxGeneralVolume/2, SettingsActivity.sfxGeneralVolume/2);
                newTile = initiator.getTile();
                eliminateSoldier(initiator);
                alreadyEliminated = true;
                moveSoldier(opponent, newTile);
                MainMenuActivity.getSoundEffects().play(R.raw.move_self, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
            case REVEAL_TEAM_B:
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


        switch (teamASoldier.getSoldierType()){
            case SWORDMASTER:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.SW_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.SW_SH; return;
                    case STONE:         matchFighters = GameStorage.SW_ST; return;
                    case KING:          matchFighters = GameStorage.SW_KI; return;
                    case PEPPER:        matchFighters = GameStorage.SW_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.SW_SW; return;
                }
            case PEPPER:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.PE_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.PE_SH; return;
                    case STONE:         matchFighters = GameStorage.PE_ST; return;
                    case KING:          matchFighters = GameStorage.PE_KI; return;
                    case PEPPER:        matchFighters = GameStorage.PE_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.PE_SW; return;
                }
            case KING:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.KI_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.KI_SH; return;
                    case STONE:         matchFighters = GameStorage.KI_ST; return;
                    case KING:          matchFighters = GameStorage.KI_KI; return;
                    case PEPPER:        matchFighters = GameStorage.KI_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.KI_SW; return;
                }
            case STONE:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.ST_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.ST_SH; return;
                    case STONE:         matchFighters = GameStorage.ST_ST; return;
                    case KING:          matchFighters = GameStorage.ST_KI; return;
                    case PEPPER:        matchFighters = GameStorage.ST_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.ST_SW; return;
                }
            case SHIELDON:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.SH_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.SH_SH; return;
                    case STONE:         matchFighters = GameStorage.SH_ST; return;
                    case KING:          matchFighters = GameStorage.SH_KI; return;
                    case PEPPER:        matchFighters = GameStorage.SH_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.SH_SW; return;
                }
            case ASHES:
                switch (teamBSoldier.getSoldierType()){
                    case ASHES:         matchFighters = GameStorage.AS_AS; return;
                    case SHIELDON:      matchFighters = GameStorage.AS_SH; return;
                    case STONE:         matchFighters = GameStorage.AS_ST; return;
                    case KING:          matchFighters = GameStorage.AS_KI; return;
                    case PEPPER:        matchFighters = GameStorage.AS_PE; return;
                    case SWORDMASTER:   matchFighters = GameStorage.AS_SW; return;
                }
        }

        matchFighters = GameStorage.KI_KI;
        return;

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

        switch (potentialInitiator.getSoldierType()){

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
                    case KING:          return RPSMatchResult.TEAM_B_WINS_THE_GAME;
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
        MainMenuActivity.getSoundEffects().play(R.raw.move_enemy, SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
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

    /**
     * EasterEgg time.
     */
    private void hax() {
        panel.pause();
        board.hax();
        panel.resume();
    }
}
