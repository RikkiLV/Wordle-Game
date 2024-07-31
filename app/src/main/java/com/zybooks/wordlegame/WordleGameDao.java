package com.zybooks.wordlegame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordleGameDao {

    @Insert
    void insert(WordleGame wordleGame);

    @Query("SELECT * FROM wordle_game")
    List<WordleGame> getAllGames();

    @Query("DELETE FROM wordle_game")
    void deleteAll();
}
