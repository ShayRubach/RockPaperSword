package com.pwnz.www.rockpapersword.model;

public class Tile {

    private int mLeft, mTop, mRight, mBottom;
    private int mWidth, mHeight;
    private boolean isOccupied = false;

    public Tile(){}

    public Tile(int top, int left, int right, int btm) {
        this.mLeft = top;
        this.mTop = left;
        this.mRight = right;
        this.mBottom = btm;

        mWidth = mRight - mLeft;
        mHeight = mBottom - mTop;
    }


    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getRight() {
        return mRight;
    }

    public int getBottom() {
        return mBottom;
    }
}
