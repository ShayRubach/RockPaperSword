package com.pwnz.www.rockpapersword.model;

import android.graphics.Bitmap;

import com.pwnz.www.rockpapersword.R;

import java.util.ArrayList;

public class Soldier extends AnimationHandler {

    private static ArrayList<SoldierType> startingSoldierTypes = initStartingSoldierTypesList();
    private static ArrayList<SoldierType> uniqueSoldierTypes = initUniqueSoldierTypesList();


    private static int pickedTypesCount = 0;
    private static int pickedUniqueTypesCount = 0;

    private Bitmap soldierBitmap;
    private Tile tile;
    private boolean isVisible;
    private boolean isHighlighted = false;
    private SoldierType soldierType;
    private int nonHighlightedSpriteSource, highlightedSpriteSource;
    private int animationSprite;
    private int team;

    public Soldier() {}

    public Soldier(Bitmap mSoldierBitmap, Tile tile, boolean isVisible, SoldierType mSoldierType) {
        this.soldierBitmap = mSoldierBitmap;
        this.tile= tile;
        this.isVisible = isVisible;
        this.soldierType = mSoldierType;
    }

    private static ArrayList<SoldierType> initStartingSoldierTypesList(){
        startingSoldierTypes = new ArrayList<>();
        allocateType(3, SoldierType.STONE, startingSoldierTypes );
        allocateType(3, SoldierType.SWORDMASTER, startingSoldierTypes );
        allocateType(3, SoldierType.PEPPER, startingSoldierTypes );
        allocateType(1, SoldierType.KING, startingSoldierTypes );
        allocateType(1, SoldierType.ASHES, startingSoldierTypes );
        allocateType(1, SoldierType.SHIELDON, startingSoldierTypes );
        allocateType(1, SoldierType.LASSO, startingSoldierTypes );

        //todo: add randomed one here instead of this
        allocateType(1, SoldierType.STONE, startingSoldierTypes );

        return startingSoldierTypes;
    }

    @Override
    public String toString() {
        return "\n\nSoldier { "+ "\n" +
                "\tTile = " + tile+ "\n" +
                "\tisVisible = " + isVisible + "\n" +
                "\tSoldierType = " + soldierType + "\n" +
                "\tSoldierTeam = " + team + "\n" +
                '}';
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

    public int getNonHighlightedSpriteSource() {
        return nonHighlightedSpriteSource;
    }

    private static void allocateType(int i, SoldierType type, ArrayList<SoldierType> soldierTypesList) {
        while(i-- != 0)
            soldierTypesList.add(type);
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

    public static SoldierType pickAvailableSoldierType() {
        if(startingSoldierTypes.isEmpty())
            return null;

        if( pickedTypesCount == startingSoldierTypes.size()) {
            pickedTypesCount = 0;
        }

        return startingSoldierTypes.get(pickedTypesCount++);
    }

    public void setSoldierAnimationSpriteByType() {
        //todo: change the animation sprite for each individual case

        switch (getSoldierType()){
            case STONE:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.soldier_stone, R.drawable.attack_1_highlighted);
                break;
            case SWORDMASTER:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.soldier_sword, R.drawable.attack_1_highlighted);
                break;
            case PEPPER:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.pepper, R.drawable.pepper_highlighted);
                break;
            case ASHES:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.soldier_ashes, R.drawable.attack_1_highlighted);
                break;
            case KING:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.soldier_king, R.drawable.attack_1_highlighted);
                break;
            case SHIELDON:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.shieldon, R.drawable.shieldon_highlighted);
                break;
            case LASSO:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.lasso, R.drawable.lasso_highlighted);
                break;
        }

    }

    private void setOnAITeam() {
        setNonHighlightedSpriteSource(R.drawable.enemy);
        setAnimationSprite(getNonHighlightedSpriteSource());
    }

    private void setOnPlayerTeam(int sprite, int highlightedSprite) {
        setNonHighlightedSpriteSource(sprite);
        setHighlightedSpriteSource(highlightedSprite);
        setAnimationSprite(getNonHighlightedSpriteSource());
    }

    public void setNonHighlightedSpriteSource(int nonHighlightedSpriteSource) {
        this.nonHighlightedSpriteSource = nonHighlightedSpriteSource;
    }

    public void setHighlightedSpriteSource(int highlightedSpriteSource) {
        this.highlightedSpriteSource = highlightedSpriteSource;
    }

    public void setAnimationSprite(int animationSprite) {
        this.animationSprite = animationSprite;
    }

    public int getAnimationSprite() {
        return animationSprite;
    }

    public void highlight() {
        setAnimationSprite(highlightedSpriteSource);
        isHighlighted = true;
    }

    public void removeHighlight() {
        setAnimationSprite(nonHighlightedSpriteSource);
        isHighlighted = false;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public static SoldierType pickUniqueSoldierType(int i) {

        if(uniqueSoldierTypes.isEmpty()) {
            System.out.println("uniqueSoldierTypes is empty");
            return null;
        }

        return uniqueSoldierTypes.get(i);
    }

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
}
