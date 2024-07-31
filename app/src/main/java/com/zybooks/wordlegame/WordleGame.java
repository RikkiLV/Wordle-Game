package com.zybooks.wordlegame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wordle_game")
public class WordleGame {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String targetWord;
    private long timeTaken;
    private int attempts;

    public WordleGame(String targetWord, long timeTaken, int attempts) {
        this.targetWord = targetWord;
        this.timeTaken = timeTaken;
        this.attempts = attempts;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTargetWord() { return targetWord; }
    public void setTargetWord(String targetWord) { this.targetWord = targetWord; }

    public long getTimeTaken() { return timeTaken; }
    public void setTimeTaken(long timeTaken) { this.timeTaken = timeTaken; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
}
