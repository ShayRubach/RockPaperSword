package com.pwnz.www.rockpapersword.model;

import android.graphics.Rect;

public class Tile {

    private Rect rect;
    private int color;
    private int width, height;
    private boolean isOccupied = false;
    private Soldier currSoldier = null;


    public Tile(){}

    @Override
    public String toString(){
        return "left: " + rect.left + " top:" +rect.top;
    }

    public Soldier getCurrSoldier() {
        return currSoldier;
    }

    public void setCurrSoldier(Soldier currSoldier) {
        this.currSoldier = currSoldier;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
