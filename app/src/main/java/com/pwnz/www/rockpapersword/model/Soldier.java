package com.pwnz.www.rockpapersword.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.pwnz.www.rockpapersword.R;

import java.util.ArrayList;

public class Soldier {

    private static ArrayList<SoldierType> soldierTypes = initSoldierTypesList();
    private static int pickedTypesCount = 0;

    private Bitmap soldierBitmap;
    private Rect rectPosition;
    private boolean isVisible;
    private boolean isHighlighted = false;
    private SoldierType soldierType;
    private int nonHighlightedSpriteSource, highlightedSpriteSource;
    private int animationSprite;
    private int team;


    public Soldier() {}

    public Soldier(Bitmap mSoldierBitmap, Rect mRectPosition, boolean isVisible, SoldierType mSoldierType) {
        this.soldierBitmap = mSoldierBitmap;
        this.rectPosition = mRectPosition;
        this.isVisible = isVisible;
        this.soldierType = mSoldierType;
    }

    private static ArrayList<SoldierType> initSoldierTypesList(){
        soldierTypes = new ArrayList<>();

        allocateType(3, SoldierType.STONE);
        allocateType(3, SoldierType.SWORDMASTER);
        allocateType(3, SoldierType.PEPPER);
        allocateType(1, SoldierType.KING);
        allocateType(1, SoldierType.ASHES);
        allocateType(1, SoldierType.SHIELDON);
        allocateType(1, SoldierType.LASSO);

        //todo: add randomed one here instead of this
        allocateType(1, SoldierType.STONE);

        return soldierTypes;
    }

    @Override
    public String toString() {
        return "\n\nSoldier { "+ "\n" +
                "\tRectPosition = " + rectPosition + "\n" +
                "\tisVisible = " + isVisible + "\n" +
                "\tSoldierType = " + soldierType + "\n" +
                '}';
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

    private static void allocateType(int i, SoldierType type) {
        while(i-- != 0)
            soldierTypes.add(type);
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

    public Rect getRectPosition() {
        return rectPosition;
    }

    public void setRectPosition(Rect rectPosition) {
        if(this == null)
            System.out.println("________________________________________________________");
        this.rectPosition = rectPosition;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public static SoldierType pickAvailableSoldierType() {
        if(soldierTypes.isEmpty())
            return null;

        if( pickedTypesCount == soldierTypes.size()) {
            pickedTypesCount = 0;
        }

        return soldierTypes.get(pickedTypesCount++);
    }

    public void setSoldierAnimationSpriteByType() {
        //todo: change the animation sprite for each individual case

        switch (getSoldierType()){
            case STONE:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case SWORDMASTER:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case PEPPER:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case ASHES:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case KING:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case SHIELDON:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
            case LASSO:
                if(getTeam() == Board.TEAM_A)
                    setOnAITeam();
                else
                    setOnPlayerTeam(R.drawable.attack_1, R.drawable.attack_1_highlighted);
                break;
        }

    }

    private void setOnAITeam() {
        setNonHighlightedSpriteSource(R.drawable.attack_1_ai);
        //setHighlightedSpriteSource(R.drawable.attack_1_ai);
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
}
