package com.pwnz.www.rockpapersword.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Soldier {
    private Bitmap mSoldierBitmap;
    private Rect mRectPosition;
    private boolean isVisible;
    private SoldierType mSoldierType;

    public Soldier() {}

    public Soldier(Bitmap mSoldierBitmap, Rect mRectPosition, boolean isVisible, SoldierType mSoldierType) {
        this.mSoldierBitmap = mSoldierBitmap;
        this.mRectPosition = mRectPosition;
        this.isVisible = isVisible;
        this.mSoldierType = mSoldierType;
    }

    public Bitmap getmSoldierBitmap() {
        return mSoldierBitmap;
    }

    public void setmSoldierBitmap(Bitmap mSoldierBitmap) {
        this.mSoldierBitmap = mSoldierBitmap;
    }

    public SoldierType getSoldierType() {
        return mSoldierType;
    }

    public void setSoldierType(SoldierType soldierType) {
        this.mSoldierType= soldierType;
    }

    public Bitmap getSoldierBitmap() {
        return mSoldierBitmap;
    }

    public void setSoldierBitmap(Bitmap bitmap) {
        this.mSoldierBitmap = bitmap;
    }

    public Rect getRectPosition() {
        return mRectPosition;
    }

    public void setRectPosition(Rect rectPosition) {
        this.mRectPosition = rectPosition;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
