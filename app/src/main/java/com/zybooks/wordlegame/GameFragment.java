package com.zybooks.wordlegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    private EditText etGuessInput;
    private Button btnSubmitGuess;
    private GridView gridPreviousGuesses;
    private GuessAdapter gridAdapter;
    private String targetWord;
    private Dictionary dict;


    // Persisting Data
    private long startTime;
    private int attempts;
    private WordleDatabase wordleDatabase;

    private static final String PREFS_NAME = "WordleGamePrefs";
    private static final String KEY_TARGET_WORD = "targetWord";
    private static final String KEY_PREVIOUS_GUESSES = "previousGuesses";
    private static final String KEY_INPUT_TEXT = "inputText";
    private static final String KEY_START_TIME = "startTime";

    private ArrayList<String> previousGuesses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        etGuessInput = view.findViewById(R.id.etGuessInput);
        btnSubmitGuess = view.findViewById(R.id.btnSubmitGuess);
        gridPreviousGuesses = view.findViewById(R.id.gridPreviousGuesses);
        wordleDatabase = WordleDatabase.getDatabase(getContext());

        // Init
        resetGame();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_TARGET_WORD)) {
            // Restore saved state
            targetWord = sharedPreferences.getString(KEY_TARGET_WORD, "");
            String previousGuessesSerialized = sharedPreferences.getString(KEY_PREVIOUS_GUESSES, "");
            previousGuesses.addAll(deserializeGuesses(previousGuessesSerialized));
            etGuessInput.setText(sharedPreferences.getString(KEY_INPUT_TEXT, ""));
            startTime = sharedPreferences.getLong(KEY_START_TIME, System.currentTimeMillis());
        }

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ValidateGuessTask().execute(etGuessInput.getText().toString().toUpperCase());
            }
        });

        // Enter key handling
        etGuessInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (btnSubmitGuess.isEnabled()){
                    submitGuess();
                }
                return true;
            }
            return false;
        });

        Log.i("Wordle", String.valueOf(startTime));
        Log.i("Wordle", targetWord);
        return view;
    }


    private void submitGuess() {
        String userGuess = etGuessInput.getText().toString().toUpperCase();
        new ValidateGuessTask().execute(userGuess);
    }

    //background thread to validate guess
    private class ValidateGuessTask extends AsyncTask<String, Void, String[]> {

        private String userGuess;
        private String message;

        @Override
        protected String[] doInBackground(String... params) {
            userGuess = params[0];
            message = null;
            if (userGuess.length() != 5) {
                message = "Please enter a 5-letter word.";
                return null;
            } else if (!dict.isValidWord(userGuess)) {
                message = "Invalid word.";
                return null;
            }
            // If reached valid guess given
            ++attempts;
            String[] result = new String[5];
            boolean[] targetWordMatched = new boolean[5]; // Keep track of which letters in the target word have been matched

            // First pass: Check for correct letters in correct positions (green)
            for (int i = 0; i < 5; i++) {
                char guessChar = userGuess.charAt(i);
                char targetChar = targetWord.charAt(i);

                if (guessChar == targetChar) {
                    result[i] = "green";
                    targetWordMatched[i] = true;
                } else {
                    result[i] = "gray"; // Initialize with gray, may change to yellow in second pass
                }
            }

            // Second pass: Check for correct letters in wrong positions (yellow)
            for (int i = 0; i < 5; i++) {
                if (result[i].equals("green")) {
                    continue;
                }

                char guessChar = userGuess.charAt(i);

                for (int j = 0; j < 5; j++) {
                    if (!targetWordMatched[j] && guessChar == targetWord.charAt(j)) {
                        result[i] = "yellow";
                        targetWordMatched[j] = true;
                        break;
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                etGuessInput.setText(""); // Clear input field for the next guess
                return;
            }

            for (int i = 0; i < 5; i++) {
                previousGuesses.add(userGuess.charAt(i) + ":" + result[i]);
            }
            gridAdapter.notifyDataSetChanged(); // Notify adapter of data change

            if (userGuess.equals(targetWord)) {
                Toast.makeText(getContext(), "Congratulations! You guessed the word.", Toast.LENGTH_LONG).show();
                btnSubmitGuess.setEnabled(false); // Disable submit button after winning
                long endTime = System.currentTimeMillis();
                long timeTaken = endTime - startTime;
                saveGame(targetWord, timeTaken, attempts);
            }


            if (attempts >= 6){
                Toast.makeText(getContext(), "You've ran out of attempts, the word was " + targetWord, Toast.LENGTH_LONG).show();
                btnSubmitGuess.setEnabled(false); // Disable submit button after winning
            }

            etGuessInput.setText(""); // Clear input field for the next guess
        }
    }

    private String serializeGuesses(ArrayList<String> guesses) {
        StringBuilder serialized = new StringBuilder();
        for (String guess : guesses) {
            serialized.append(guess);
            serialized.append(",");
        }
        return serialized.toString();
    }

    private ArrayList<String> deserializeGuesses(String serialized) {
        ArrayList<String> guesses = new ArrayList<>();
        String[] parts = serialized.split(",");
        for (String part : parts) {
            if (!part.isEmpty()) {
                guesses.add(part);
            }
        }
        return guesses;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save current target word, previous guesses, and current input text
        editor.putString(KEY_TARGET_WORD, targetWord);
        editor.putString(KEY_PREVIOUS_GUESSES, serializeGuesses(previousGuesses));
        editor.putString(KEY_INPUT_TEXT, etGuessInput.getText().toString());
        editor.putLong(KEY_START_TIME, startTime);

        editor.apply();
    }

    public void resetGame(){
        if (dict == null){
            dict = new Dictionary(getContext());
        }
        targetWord = dict.returnRandomWord(); // Generate the hidden target word
        previousGuesses = new ArrayList<>();
        // SaveData reset
        attempts = 0;
        startTime = System.currentTimeMillis();

        // Adapter stuff
        if (gridAdapter == null){
            gridAdapter = new GuessAdapter(getContext(), previousGuesses);
        }
        else {
            gridAdapter.reset(previousGuesses);
        }

        gridPreviousGuesses.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
        Log.i("Wordle", targetWord);
    }

    private void saveGame(String targetWord, long timeTaken, int attempts) {
        new Thread(() -> {
            WordleGame game = new WordleGame(targetWord, timeTaken, attempts);
            wordleDatabase.wordleGameDao().insert(game);
        }).start();
    }

}