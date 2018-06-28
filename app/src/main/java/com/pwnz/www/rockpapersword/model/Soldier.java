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
    private SoldierType soldierType;
    private int animationSprite;

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

    public int getAnimationSprite() {
        return animationSprite;
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

        switch (this.getSoldierType()){
            case STONE:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case SWORDMASTER:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case PEPPER:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case ASHES:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case KING:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case SHIELDON:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
            case LASSO:
                this.setAnimationSprite(R.drawable.attack_1);
                break;
        }

    }

    public void setAnimationSprite(int animationSprite) {
        this.animationSprite = animationSprite;
    }
}
