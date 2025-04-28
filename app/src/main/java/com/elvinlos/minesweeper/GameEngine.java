package com.elvinlos.minesweeper;

import android.content.Context;
import android.app.Activity;
import android.content.ContextWrapper;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.elvinlos.minesweeper.util.Generator;
import com.elvinlos.minesweeper.views.grid.Cell;
import com.elvinlos.minesweeper.views.grid.Grid;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class GameEngine {
    public Activity activity;
    public ImageButton ResetButton;
    public static int BOMB_NUMBER = 16 ;
    public static int WIDTH = 100 ;
    public static int HEIGHT = 100;
    public static String Diff;
    public TextView TimerText;
    public TextView FlagText;
    public int flagsLeft;
    private static boolean gameWon = false;
    private static boolean allowTouch = true;
    private final Handler handler = new Handler();
    private static GameEngine instance;
    private int seconds = 0;
    private boolean timerRunning = false;
    private Grid gridview;
    public void setGridview(Grid gridview){
        this.gridview = gridview;
    }


    private final Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine() {
    }

    public void createGrid(Context context) {
        if (gridview != null)
            gridview.updateView();
        int[][] GenerateGrid = Generator.generate(BOMB_NUMBER, WIDTH, HEIGHT);
        allowTouch = true;
        setGrid(context, GenerateGrid);

        initHeader();
    }
    public void initHeader() {
        activity = getActivity(MinesweeperGrid[0][0].getContext());
        FlagText = activity.findViewById(R.id.FlagText);
        flagsLeft = BOMB_NUMBER;
        displayFlagNumber();
        ResetButton =  activity.findViewById(R.id.resetButton);
        ResetButton.setOnClickListener(v -> restartGame());
        TimerText = activity.findViewById(R.id.timerText);
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
        if (!allowTouch) return;

        if(!timerRunning) startTimer();

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
        if (cell.isFlagged()) return;

        // normal
        if (cell.isClicked()) return;

        cell.setClicked();

        if (cell.isBomb()) {
            onGameLost();
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

    private void onGameLost() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x, y).setRevealed();
                getCellAt(x, y).setFlagged(false);
            }
        }
        ResetButton.setBackground(activity.getDrawable(R.drawable.face_lose));
        allowTouch = false;
        stopTimer();
    }

    private void onGameWon() {
        // Reveal all cells when the game is won
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x, y).setRevealed();
            }
        }
        allowTouch = false;
        saveHighscore();
        ResetButton.setBackground(activity.getDrawable(R.drawable.face_win));
        stopTimer();
    }

    public List<Integer> loadHighscores() {
        List<Integer> highscores = new ArrayList<>();
        if (activity == null) return highscores;

        String key = Diff;

        String json = activity.getSharedPreferences("Highscore_saves", Context.MODE_PRIVATE)
                .getString(key, null);

        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    highscores.add(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return highscores;
    }


    private void saveHighscore() {
        if (activity == null) return;

        List<Integer> highscores = loadHighscores();
        highscores.add(seconds);
        Collections.sort(highscores); // Fastest times first

        // Only keep top 10 scores
        if (highscores.size() > 10) {
            highscores = highscores.subList(0, 10);
        }

        // Save as JSON
        JSONArray jsonArray = new JSONArray();
        for (int score : highscores) {
            jsonArray.put(score);
        }

        String key = Diff;

        activity.getSharedPreferences("Highscore_saves", Context.MODE_PRIVATE)
                .edit()
                .putString(key, jsonArray.toString())
                .apply();
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
        resetTimer();
        Context currentContext = MinesweeperGrid[0][0].getContext();
        ResetButton.setBackground(activity.getDrawable(R.drawable.reset_button_click));
        createGrid(currentContext);
    }

    public void flag(int x, int y) {
        if (getCellAt(x,y).isRevealed()){
            return;
        }
        boolean isFlagged = getCellAt(x, y).isFlagged();

        if (!isFlagged && flagsLeft == 0) {
            // No more flags available, don't allow placing new flag
            Toast.makeText(activity, "Hết số cờ để đặt mìn!", Toast.LENGTH_SHORT).show();
            return;
        }

        getCellAt(x, y).setFlagged(!isFlagged);
        getCellAt(x, y).invalidate();

        // Update flagsLeft
        if (isFlagged) {
            flagsLeft++;// Removed a flag
        } else {
            flagsLeft--;// Placed a flag
        }

        displayFlagNumber();
        // Check for win after placing/removing a flag
        checkForWin();
    }

    private void checkForWin() {
        boolean allNonBombCellsRevealed = true;

        boolean allBombsFlagged = true;
        int totalFlags = 0;

        if (gameWon) {
            return;
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Cell cell = getCellAt(x, y);

                if (cell.isFlagged()) {
                    totalFlags++;
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

        if (allNonBombCellsRevealed && (allBombsFlagged && totalFlags == BOMB_NUMBER)) {
            gameWon = true;
            onGameWon();
        }
    }

    public void onDestroy() {
        this.activity = null;
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (seconds < 999) {
                seconds++;
                String timeFormatted = String.format(Locale.getDefault(),"%03d", seconds);
                TimerText.setText(timeFormatted);
                handler.postDelayed(this, 1000);
            } else {
                stopTimer(); // Stop when reaching 999
            }
        }
    };
    public void startTimer() {
        // Start the timer by posting the Runnable for the first time
        timerRunning = true;
        handler.post(timerRunnable);  // Start the timer
    }

    public void resetTimer() {
        timerRunning = false;
        handler.removeCallbacks(timerRunnable);
        seconds = 0;
        TimerText.setText("000");
    }
    public void stopTimer() {
        // Stop the timer by removing the Runnable from the Handler
        timerRunning = false;
        handler.removeCallbacks(timerRunnable);  // Stop updating the time
    }
    public void displayFlagNumber() {
        String flagTextFormatted = String.format("%03d", flagsLeft);
        FlagText.setText(flagTextFormatted);
    }
}