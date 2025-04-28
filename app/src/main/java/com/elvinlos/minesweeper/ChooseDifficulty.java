package com.elvinlos.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class ChooseDifficulty extends AppCompatActivity {
    private Button buttonEasy, buttonNormal, buttonHard, buttonEvil, buttonHighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        buttonEasy = findViewById(R.id.buttonEasy);
        buttonNormal = findViewById(R.id.buttonNormal);
        buttonHard = findViewById(R.id.buttonHard);
        buttonEvil = findViewById(R.id.buttonEvil);
        buttonHighscore = findViewById(R.id.buttonHighscore); // new!

        buttonEasy.setOnClickListener(v -> startMainActivity("dễ"));
        buttonNormal.setOnClickListener(v -> startMainActivity("trung bình"));
        buttonHard.setOnClickListener(v -> startMainActivity("khó"));
        buttonEvil.setOnClickListener(v -> startMainActivity("cực khó"));

        buttonHighscore.setOnClickListener(v -> openHighscoreScreen());
    }

    private void startMainActivity(String difficulty) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    private void openHighscoreScreen() {
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }
}
