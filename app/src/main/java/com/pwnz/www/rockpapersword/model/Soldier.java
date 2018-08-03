package com.pwnz.www.rockpapersword.model;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Soldier extends AnimationHandler {

    private static ArrayList<SoldierType> startingSoldierTypes = initStartingSoldierTypesList();
    private static ArrayList<SoldierType> uniqueSoldierTypes = initUniqueSoldierTypesList();


    private static int pickedTypesCount = 0;
    private Bitmap soldierBitmap, soldierNonHighlightedBitmap, soldierHighlightedBitmap, soldierRevealedBitmap;
    private Tile tile;
    private boolean isVisible;
    private boolean isHighlighted = false;
    private SoldierType soldierType;
    private int team;
    private int tileOffset;
    private boolean isRevealed;

    public Soldier() {}

    /**
     * creates a static (non-changed) list that will hold the starting team for each side.
     * @return none
     */
    private static ArrayList<SoldierType> initStartingSoldierTypesList(){
        startingSoldierTypes = new ArrayList<>();
        allocateType(3, SoldierType.STONE, startingSoldierTypes );
        allocateType(3, SoldierType.SWORDMASTER, startingSoldierTypes );
        allocateType(3, SoldierType.PEPPER, startingSoldierTypes );
        allocateType(1, SoldierType.KING, startingSoldierTypes );
        allocateType(1, SoldierType.ASHES, startingSoldierTypes );
        allocateType(1, SoldierType.SHIELDON, startingSoldierTypes );
        //todo: implemets laso logic first. meanwhile shieldon is replacing it
        //allocateType(1, SoldierType.LASSO, startingSoldierTypes );
        allocateType(1, SoldierType.SHIELDON, startingSoldierTypes);

        //todo: add randomed one here instead of this
        allocateType(1, SoldierType.STONE, startingSoldierTypes );

        return startingSoldierTypes;
    }

    /**
     * picks an available soldier from the static list of starting soldiers
     * @return none
     * @see #initStartingSoldierTypesList()
     */
    public static SoldierType pickAvailableSoldierType() {
        if(startingSoldierTypes.isEmpty())
            return null;

        if( pickedTypesCount == startingSoldierTypes.size()) {
            pickedTypesCount = 0;
        }
        return startingSoldierTypes.get(pickedTypesCount++);
    }

    /**
     * this list holds uniquely 1 soldier of each type
     * @return the reference to the list
     */
    private static ArrayList<SoldierType> initUniqueSoldierTypesList() {
        uniqueSoldierTypes = new ArrayList<>();
        allocateType(1, SoldierType.STONE, uniqueSoldierTypes);
        allocateType(1, SoldierType.SWORDMASTER, uniqueSoldierTypes);
        allocateType(1, SoldierType.PEPPER, uniqueSoldierTypes);
        allocateType(1, SoldierType.KING, uniqueSoldierTypes);
        allocateType(1, SoldierType.ASHES, uniqueSoldierTypes);
        allocateType(1, SoldierType.SHIELDON, uniqueSoldierTypes);
        allocateType(1, SoldierType.LASSO, uniqueSoldierTypes);
        return uniqueSoldierTypes;
    }

    /**
     * uniquely pick a soldier from the unique list
     * @param i index of the soldier chosen from list
     * @see #uniqueSoldierTypes
     * @see #initUniqueSoldierTypesList()
     * @return the chosen object from list
     */
    public static SoldierType pickUniqueSoldierType(int i) {

        if(uniqueSoldierTypes.isEmpty()) {
            System.out.println("uniqueSoldierTypes is empty");
            return null;
        }

        return uniqueSoldierTypes.get(i);
    }

    private static void allocateType(int i, SoldierType type, ArrayList<SoldierType> soldierTypesList) {
        while(i-- != 0)
            soldierTypesList.add(type);
    }

    @Override
    public String toString() {
        return "Soldier { " +
                " Type=" + soldierType +
                " | Team = " + (team == Board.TEAM_A ? "A" : "B") +
                " | Tile=" + tile +
                " | isVisible = " + isVisible + " }";
    }

    public void highlight() {
        setSoldierBitmap(getSoldierHighlightedBitmap());
        isHighlighted = true;
    }

    public void removeHighlight() {
        if(hasBeenRevealed())
            setSoldierBitmap(getSoldierRevealedBitmap());
        else
            setSoldierBitmap(getSoldierNonHighlightedBitmap());
        isHighlighted = false;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public int getTileOffset() {
        return tileOffset;
    }

    public void setTileOffset(int tileOffset) {
        this.tileOffset = tileOffset;
    }

    public SoldierType getSoldierType() {
        return soldierType;
    }

    public void setSoldierType(SoldierType soldierType) {
        this.soldierType = soldierType;
    }

    public Bitmap getSoldierBitmap() {
        return soldierBitmap;
    }

    public void setSoldierBitmap(Bitmap bitmap) {
        this.soldierBitmap = bitmap;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public Bitmap getSoldierHighlightedBitmap() {
        return soldierHighlightedBitmap;
    }

    public void setSoldierHighlightedBitmap(Bitmap soldierHighlightedBitmap) {
        this.soldierHighlightedBitmap = soldierHighlightedBitmap;
    }

    public Bitmap getSoldierRevealedBitmap() {
        return soldierRevealedBitmap;
    }

    public void setSoldierRevealedBitmap(Bitmap soldierRevealedBitmap) {
        this.soldierRevealedBitmap = soldierRevealedBitmap;
    }

    public Bitmap getSoldierNonHighlightedBitmap() {
        return soldierNonHighlightedBitmap;
    }

    public void setSoldierNonHighlightedBitmap(Bitmap soldierNonHighlightedBitmap) {
        this.soldierNonHighlightedBitmap = soldierNonHighlightedBitmap;
    }

    public boolean hasBeenRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public static ArrayList<SoldierType> getUniqueSoldierTypes() {
        return uniqueSoldierTypes;
    }


    public void getBitmapsByType() {
        if(getTeam() == Board.TEAM_A){
            setSoldierNonHighlightedBitmap(GameStorage.getSoldierBitmapByType(SoldierType.DEAULT, team, false));
            setSoldierHighlightedBitmap(getSoldierNonHighlightedBitmap());
            setSoldierRevealedBitmap(GameStorage.getSoldierBitmapByType(getSoldierType(), team, false));
        }
        else{
            setSoldierHighlightedBitmap(GameStorage.getSoldierBitmapByType(getSoldierType(), team, true));
            setSoldierNonHighlightedBitmap(GameStorage.getSoldierBitmapByType(getSoldierType(), team, false));
            setSoldierRevealedBitmap(getSoldierNonHighlightedBitmap());
        }
        setSoldierBitmap(getSoldierNonHighlightedBitmap());
    }

}
