package com.elvinlos.minesweeper.views.grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.elvinlos.minesweeper.GameEngine;

public class Grid extends GridView {
    public Grid(Context context, AttributeSet attrs){
        super(context,attrs);

        setNumColumns(GameEngine.WIDTH);
        setAdapter(new GridAdapter());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static class GridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            GameEngine.getInstance();
            GameEngine.getInstance();
            return GameEngine.WIDTH * GameEngine.HEIGHT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return GameEngine.getInstance().getCellAt(position);
        }
    }
}
