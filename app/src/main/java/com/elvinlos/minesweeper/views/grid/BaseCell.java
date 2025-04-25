package com.elvinlos.minesweeper.views.grid;

import android.content.Context;
import android.view.View;

import com.elvinlos.minesweeper.GameEngine;

public abstract class BaseCell extends View {

    private int value;

    private boolean IsBomb;
    private boolean IsRevealed;
    private boolean IsClicked;
    private boolean IsFlagged;

    private int x, y;
    private int position;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        IsBomb = false;
        IsRevealed = false;
        IsClicked = false;
        IsFlagged = false;

        if(value == -1){
            IsBomb = true;
        }

        this.value = value;
    }

    public boolean isBomb() {
        return IsBomb;
    }

    public void setBomb(boolean bomb) {
        IsBomb = bomb;
    }

    public boolean isRevealed() {
        return IsRevealed;
    }

    public void setRevealed(boolean revealed) {
        IsRevealed = revealed;
    }

    public boolean isClicked() {
        return IsClicked;
    }

    public void setClicked() {
        this.IsClicked = true;
        this.IsRevealed = true;

        invalidate();
    }

    public boolean isFlagged() {
        return IsFlagged;
    }

    public void setFlagged(boolean flagged) {
        IsFlagged = flagged;
    }

    public int getXPos() {
        return x;
    }

    public int getYPos() {
        return y;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;

        x = position % GameEngine.WIDTH;
        y = position / GameEngine.HEIGHT;

        invalidate();
    }


    public BaseCell (Context context){
        super(context);
    }
}
