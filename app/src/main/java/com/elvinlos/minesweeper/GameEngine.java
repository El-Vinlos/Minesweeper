package com.elvinlos.minesweeper;

import android.content.Context;

import com.elvinlos.minesweeper.util.Generator;
import com.elvinlos.minesweeper.views.grid.Cell;

public class GameEngine {
    private static GameEngine instance;
    public static final int BOMB_NUMBER = 10;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    private Context context;

    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance() {
        if (instance == null)
        {
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine() {}

    public void createGrid(Context context)
    {
        this.context = context;

        int[][] GenerateGrid = Generator.generate(BOMB_NUMBER, WIDTH, HEIGHT);
        PrintGrid.print(GenerateGrid, WIDTH, HEIGHT);
        setGrid(context,GenerateGrid);
    }

    private void setGrid(final Context context, final int[][] grid){
        for (int x = 0; x < WIDTH; x++){
            for (int y = 0; y < HEIGHT; y ++){
                if (MinesweeperGrid[x][y] == null){
                    MinesweeperGrid[x][y] = new Cell(context, y * HEIGHT + x );
                }
                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }

    public Cell getCellAt(int position) {
        int x = position % WIDTH;
        int y = position / HEIGHT;

        return MinesweeperGrid[x][y];
    }

    public Cell getCellAt( int x, int y) {
        return MinesweeperGrid[x][y];
    }
    public void click(int x, int y) {
        if (!isValidPosition(x, y)) return;

        Cell cell = getCellAt(x, y);
        if (cell.isClicked()) return;

        cell.setClicked();

        if (cell.isBomb()) {
            onGamelost();
            return;
        }

        if (cell.getValue() != 0) return;

        for (int xt = -1; xt <= 1; xt++) {
            for (int yt = -1; yt <= 1; yt++) {
                if (xt != yt) {
                    click(x + xt, y + yt);
                }
            }
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    private void onGamelost() {

    }

    public void flag(int x, int y) {
        boolean isFlagged = getCellAt(x,y).isFlagged();
        getCellAt(x,y).setFlagged(!isFlagged);
        getCellAt(x,y).invalidate();
    }
}
