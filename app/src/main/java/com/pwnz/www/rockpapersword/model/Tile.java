package com.pwnz.www.rockpapersword.model;

import android.graphics.Color;
import android.graphics.Rect;

public class Tile {

    private Rect rect;
    private int color;
    private int width, height;
    private boolean isOccupied = false;


    public Tile(){}

    public Tile(int left, int right, int top, int bottom, int r, int g, int b) {
        color = Color.rgb(r,g,b);
        rect = new Rect(left, top, right, bottom);
        setOccupied(false);

        //these values would be unchanged
        width = right-left;
        height = bottom-top;

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
