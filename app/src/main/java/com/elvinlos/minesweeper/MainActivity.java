package com.elvinlos.minesweeper;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.elvinlos.minesweeper.views.grid.Grid;

public class MainActivity extends AppCompatActivity {
    public static int WIDTH = 9;
    public static int HEIGHT = 9;
    public static int BOMB_NUMBER = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String difficulty = getIntent().getStringExtra("difficulty");

        // Adjust game parameters based on difficulty
        switch (difficulty != null ? difficulty : "dễ") {
            case "dễ":
                WIDTH = 9;
                HEIGHT = 9;
                BOMB_NUMBER = 12;
                break;
            case "trung bình":
                WIDTH = 11;
                HEIGHT = 18;
                BOMB_NUMBER = 40;
                break;
            case "khó":
                WIDTH = 16;
                HEIGHT = 26;
                BOMB_NUMBER = 80;
                break;
            case "cực khó":
                WIDTH = 19;
                HEIGHT = 31;
                BOMB_NUMBER = 176;
                break;
        }

        GameEngine.WIDTH = WIDTH;
        GameEngine.HEIGHT = HEIGHT;
        GameEngine.BOMB_NUMBER = BOMB_NUMBER;
        Grid grid = findViewById(R.id.minesweeper_gridview);
        GameEngine.getInstance().setGridview(grid);
        GameEngine.getInstance().createGrid(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameEngine.getInstance().onDestroy();
    }
}