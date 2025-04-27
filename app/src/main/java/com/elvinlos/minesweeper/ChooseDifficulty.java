package com.elvinlos.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class ChooseDifficulty extends AppCompatActivity {
    private Button buttonEasy, buttonNormal, buttonHard, buttonEvil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        buttonEasy = findViewById(R.id.buttonEasy);
        buttonNormal = findViewById(R.id.buttonNormal);
        buttonHard = findViewById(R.id.buttonHard);
        buttonEvil = findViewById(R.id.buttonEvil);

        buttonEasy.setOnClickListener(v -> startMainActivity("dễ"));
        buttonNormal.setOnClickListener(v -> startMainActivity("trung bình"));
        buttonHard.setOnClickListener(v -> startMainActivity("khó"));
        buttonEvil.setOnClickListener(v -> startMainActivity("cực khó"));
    }

    private void startMainActivity(String difficulty) {
        Intent intent = new Intent(ChooseDifficulty.this, MainActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        finish(); // Optional
    }
}
