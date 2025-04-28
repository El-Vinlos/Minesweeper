package com.elvinlos.minesweeper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    private Spinner difficultySpinner;
    private ListView highscoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        difficultySpinner = findViewById(R.id.difficultySpinner);
        highscoreList = findViewById(R.id.highscoreList);

        // Setup spinner options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Dễ", "Trung bình", "Khó", "Cực khó"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        difficultySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadHighscores(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Load initial
        loadHighscores(0);
    }

    private void loadHighscores(int position) {
        String difficulty = "easy"; // default

        switch (position) {
            case 0: difficulty = "easy"; break;
            case 1: difficulty = "normal"; break;
            case 2: difficulty = "hard"; break;
            case 3: difficulty = "evil"; break;
            default:
                difficulty = "easy";
                break;
        }

        GameEngine.Diff = difficulty;
        GameEngine.getInstance().activity = this;
        List<Integer> scores = GameEngine.getInstance().loadHighscores();

        List<String> displayScores = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            displayScores.add("Top " + (i + 1) + ": " + scores.get(i) + "s");
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                displayScores);
        highscoreList.setAdapter(listAdapter);
    }}