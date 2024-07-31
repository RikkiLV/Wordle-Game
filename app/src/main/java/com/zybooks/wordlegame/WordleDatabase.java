package com.zybooks.wordlegame;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WordleGame.class}, version = 1, exportSchema = false)
public abstract class WordleDatabase extends RoomDatabase {

    public abstract WordleGameDao wordleGameDao();

    private static volatile WordleDatabase INSTANCE;

    public static WordleDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordleDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WordleDatabase.class, "wordle_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
