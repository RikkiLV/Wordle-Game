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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;

public class GameFragment extends Fragment {

    private EditText etGuessInput;
    private Button btnSubmitGuess;
    private TextView tvResult;
    private GridView gridPreviousGuesses;
    private GuessAdapter gridAdapter;
    private String targetWord;
    private ArrayList<String> previousGuesses;
    private Dictionary dict;
    private static final String PREFS_NAME = "WordleGamePrefs";
    private static final String KEY_TARGET_WORD = "targetWord";
    private static final String KEY_PREVIOUS_GUESSES = "previousGuesses";
    private static final String KEY_INPUT_TEXT = "inputText";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        etGuessInput = view.findViewById(R.id.etGuessInput);
        btnSubmitGuess = view.findViewById(R.id.btnSubmitGuess);
        tvResult = view.findViewById(R.id.tvResult);
        gridPreviousGuesses = view.findViewById(R.id.gridPreviousGuesses);

        // Init Dictionary
        dict = new Dictionary(getContext());
        targetWord = dict.returnRandomWord(); // Generate the hidden target word
        Log.i("Wordle", targetWord);

        // Initialize GridView adapter and previousGuesses list
        previousGuesses = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_TARGET_WORD)) {
            // Restore saved state
            targetWord = sharedPreferences.getString(KEY_TARGET_WORD, "");
            String previousGuessesSerialized = sharedPreferences.getString(KEY_PREVIOUS_GUESSES, "");
            previousGuesses = deserializeGuesses(previousGuessesSerialized);
            etGuessInput.setText(sharedPreferences.getString(KEY_INPUT_TEXT, ""));
        } else {
            // Generate a new target word
            targetWord = dict.returnRandomWord();
        }
        gridAdapter = new GuessAdapter(getContext(), previousGuesses);
        gridPreviousGuesses.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ValidateGuessTask().execute(etGuessInput.getText().toString().toUpperCase());
            }
        });

        // Set OnEditorActionListener for Enter key handling
        etGuessInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                submitGuess();
                return true;
            }
            return false;
        });


        return view;
    }

    // Enter key handling
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

            if (userGuess.length() != 5) {
                message = "Please enter a 5-letter word.";
                return null;
            } else if (!dict.isValidWord(userGuess)) {
                message = "Invalid word.";
                return null;
            }
            message = null; // Reset message
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
                tvResult.setText(message);
                etGuessInput.setText(""); // Clear input field for the next guess
                return;
            }

            for (int i = 0; i < 5; i++) {
                previousGuesses.add(userGuess.charAt(i) + ":" + result[i]);
            }
            gridAdapter.notifyDataSetChanged(); // Notify adapter of data change

            if (userGuess.equals(targetWord)) {
                tvResult.setText("Congratulations! You guessed the word.");
                btnSubmitGuess.setEnabled(false); // Disable submit button after winning
            } else {
                tvResult.setText("");
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

        editor.apply();
    }

}