package com.zybooks.wordlegame;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private HashSet<String> validWords;
    private ArrayList<String> possibleSolutions;

    public Dictionary(Context context) {
        validWords = new HashSet<>();
        loadWordsFromAsset(context, "possible_answers.txt");
        possibleSolutions = new ArrayList<>(validWords);
    }

    private void loadWordsFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                validWords.add(line.trim().toLowerCase());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fast checking using the hash set for validity
    public boolean isValidWord(String word) {
        return validWords.contains(word.toLowerCase());
    }

    // Used when restarting or starting for the first time
    public String returnRandomWord(){
        Random random = new Random();
        return possibleSolutions.get(random.nextInt(validWords.size())).toUpperCase();
    }
}

