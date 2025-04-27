package com.elvinlos.minesweeper.views.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.elvinlos.minesweeper.GameEngine;
import com.elvinlos.minesweeper.R;

public class Cell extends BaseCell implements View.OnClickListener, View.OnLongClickListener{

    public Cell (Context context, int x, int y){
        super(context);
        setPosition(x,y);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        GameEngine.getInstance().click(getXPos(), getYPos() );
    }

    @Override
    public boolean onLongClick(View v) {
        GameEngine.getInstance().flag( getXPos(), getYPos());
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Minesweeper","Cell:onDraw");

        if (isFlagged()) {
            drawFlag(canvas);
            return;
        }

        if (isRevealed() && isBomb() && !isClicked()) {
            drawBomb(canvas);
            return;
        }

        if (isClicked()) {
            if (getValue() == -1) {
                drawBombRed(canvas);
            } else {
                drawNumber(canvas);
            }
            return;
        }

        drawButton(canvas);
    }

    private void drawPressed(Canvas canvas){
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.pressed);
    }
    private void drawBombRed(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.mine_red);
        drawable.setBounds(0,0,getWidth(),getHeight());
        drawable.draw(canvas);
    }

    private void drawFlag(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.flag);
        drawable.setBounds(0,0,getWidth(),getHeight());
        drawable.draw(canvas);
    }

    private void drawButton(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.closed);
        drawable.setBounds(0,0,getWidth(),getHeight());
        drawable.draw(canvas);
    }

    private void drawBomb(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.mine);
        drawable.setBounds(0,0,getWidth(),getHeight());
        drawable.draw(canvas);
    }

    private void drawNumber(Canvas canvas)
    {
        Drawable drawable = null;

        switch (getValue())
        {
            case 0:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type0);
                break;
            case 1:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type1);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type2);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type3);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type4);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type5);
                break;
            case 6:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type6);
                break;
            case 7:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type7);
                break;
            case 8:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.type8);
                break;
        }
        drawable.setBounds(0,0,getWidth(),getHeight());
        drawable.draw(canvas);
    }
}

