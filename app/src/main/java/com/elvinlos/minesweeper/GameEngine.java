package com.elvinlos.minesweeper;

import android.content.Context;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.ContextWrapper;

import com.elvinlos.minesweeper.util.Generator;
import com.elvinlos.minesweeper.views.grid.Cell;

public class GameEngine {
    private static GameEngine instance;
    public int BOMB_NUMBER = (int) ((WIDTH * HEIGHT) * 0.25);
    public static final int WIDTH = 16;
    public static final int HEIGHT = 25;
    public static boolean gameWon = false;

    // Use ApplicationContext instead of Activity context
    private Context applicationContext;

    private final Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine() { }

    public void createGrid(Context context) {
        // Store only the application context, not the activity context
        this.applicationContext = context.getApplicationContext();

        int[][] GenerateGrid = Generator.generate(BOMB_NUMBER, WIDTH, HEIGHT);
        setGrid(context, GenerateGrid);
    }

    private void setGrid(final Context context, final int[][] grid) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (MinesweeperGrid[x][y] == null) {
                    MinesweeperGrid[x][y] = new Cell(context, x, y);
                }
                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }


    public Cell getCellAt(int position) {
        int x = position % WIDTH;
        int y = position / WIDTH;

        return MinesweeperGrid[x][y];
    }

    public Cell getCellAt(int x, int y) {
        return MinesweeperGrid[x][y];
    }

    public void click(int x, int y) {
        if (!isValidPosition(x, y)) return;

        Cell cell = getCellAt(x, y);

        //Chording
        if (cell.isRevealed() && cell.getValue() > 0) {
            // If clicking on a revealed numbered cell, try chording
            int flaggedCount = 0;
            for (int xt = -1; xt <= 1; xt++) {
                for (int yt = -1; yt <= 1; yt++) {
                    int newX = x + xt;
                    int newY = y + yt;
                    if (isValidPosition(newX, newY) && getCellAt(newX, newY).isFlagged()) {
                        flaggedCount++;
                    }
                }
            }

            if (flaggedCount == cell.getValue()) {
                // Reveal all unflagged adjacent cells
                for (int xt = -1; xt <= 1; xt++) {
                    for (int yt = -1; yt <= 1; yt++) {
                        int newX = x + xt;
                        int newY = y + yt;
                        if (isValidPosition(newX, newY)) {
                            Cell neighbor = getCellAt(newX, newY);
                            if (!neighbor.isFlagged() && !neighbor.isRevealed()) {
                                click(newX, newY);
                            }
                        }
                    }
                }
            }
            return;
        }

        // normal
        if (cell.isClicked()) return;

        cell.setClicked();

        if (cell.isBomb()) {
            onGamelost();
            return;
        }

        if (cell.getValue() != 0) {
            checkForWin();
            return;
        }

        // If blank cell, open adjacent cells
        for (int xt = -1; xt <= 1; xt++) {
            for (int yt = -1; yt <= 1; yt++) {
                if (!(xt == 0 && yt == 0)) {  // Skip the current cell
                    click(x + xt, y + yt);
                }
            }
        }

        checkForWin();
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    private void onGamelost() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x, y).setRevealed();
                getCellAt(x, y).setFlagged(false);
            }
        }

        askForNewGame("Bạn đã thua!");
    }

    private void onGameWon() {
        // Reveal all cells when the game is won
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x, y).setRevealed();
            }
        }

        askForNewGame("Bạn đã thắng!");
    }

    private void askForNewGame(String title) {
        // Get the current Activity context safely
        Activity activity = getActivity(MinesweeperGrid[0][0].getContext());
        if (activity == null || activity.isFinishing()) {
            return; // Don't show dialog if activity is finishing or null
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage("Bạn có muốn chơi lại không?")
                .setCancelable(false)
                .setPositiveButton("Game mới", (dialog, which) -> restartGame())
                .setNegativeButton("Thoát", (dialog, which) -> {
                    // This will close the activity
                    activity.finish();
                });

        builder.create().show();
    }

    // Helper method to safely get the current Activity
    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void restartGame() {
        gameWon = false;

        // Get the current Activity context from a Cell
        Context currentContext = MinesweeperGrid[0][0].getContext();
        createGrid(currentContext);
    }

    public void flag(int x, int y) {
        if (getCellAt(x,y).isRevealed()){
            return;
        }
        boolean isFlagged = getCellAt(x, y).isFlagged();
        getCellAt(x, y).setFlagged(!isFlagged);
        getCellAt(x, y).invalidate();

        // Check for win after placing/removing a flag
        checkForWin();
    }

    private void checkForWin() {
        // First win condition: All non-bomb cells are revealed
        boolean allNonBombCellsRevealed = true;

        // Second win condition: All bombs are correctly flagged and exactly BOMB_NUMBER flags are used
        boolean allBombsFlagged = true;
        int flaggedBombs = 0;
        int totalFlags = 0;

        if (gameWon) {
            return;
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Cell cell = getCellAt(x, y);

                // Count flags
                if (cell.isFlagged()) {
                    totalFlags++;

                    // Count correctly flagged bombs
                    if (cell.isBomb()) {
                        flaggedBombs++;
                    }
                }

                // Check if all non-bomb cells are revealed
                if (!cell.isBomb() && !cell.isRevealed()) {
                    allNonBombCellsRevealed = false;
                }

                // Check if any bomb is not flagged
                if (cell.isBomb() && !cell.isFlagged()) {
                    allBombsFlagged = false;
                }
            }
        }

        // Win if all non-bomb cells are revealed or all bombs are correctly flagged
        // and the number of flags equals the number of bombs
        if (allNonBombCellsRevealed && (allBombsFlagged && totalFlags == BOMB_NUMBER)) {
            gameWon = true;
            onGameWon();
        }
    }

    // Method to clean up resources when activity is destroyed
    public void onDestroy() {
        // Release context reference when not needed
        this.applicationContext = null;
    }
}