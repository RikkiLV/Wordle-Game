package com.zybooks.wordlegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private WordleDatabase wordleDatabase;
    private RecyclerView recyclerView;
    private StatsAdapter statsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wordleDatabase = WordleDatabase.getDatabase(this);
        new Thread(() -> {
            List<WordleGame> games = wordleDatabase.wordleGameDao().getAllGames();
            runOnUiThread(() -> {
                statsAdapter = new StatsAdapter(games);
                recyclerView.setAdapter(statsAdapter);
            });
        }).start();
    }
}
