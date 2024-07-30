package com.zybooks.wordlegame;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {

    private EditText etGuessInput;
    private Button btnSubmitGuess;
    private TextView tvResult;
    private GridView gridPreviousGuesses;
    private ArrayAdapter<String> gridAdapter;
    private String targetWord;
    private ArrayList<String> previousGuesses;
    private Dictionary dict;

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
        gridAdapter = new ArrayAdapter<>(getContext(), R.layout.grid_item, R.id.tvGridItem, previousGuesses);
        gridPreviousGuesses.setAdapter(gridAdapter);

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ValidateGuessTask().execute(etGuessInput.getText().toString().toUpperCase());
            }
        });

        gridPreviousGuesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String previousGuess = previousGuesses.get(position);
                etGuessInput.setText(previousGuess); // Set clicked item's text to input field
            }
        });

        return view;
    }

    //background thread to validate guess
    private class ValidateGuessTask extends AsyncTask<String, Void, String> {

        private String userGuess;

        @Override
        protected String doInBackground(String... params) {
            userGuess = params[0];

            if (userGuess.length() != 5) {
                return "Please enter a 5-letter word.";
            } else if (!dict.isValidWord(userGuess)) {
                return "Invalid word.";
            }

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < 5; i++) {
                char guessChar = userGuess.charAt(i);
                char targetChar = targetWord.charAt(i);

                if (guessChar == targetChar) {
                    result.append(guessChar);
                } else if (targetWord.contains(String.valueOf(guessChar))) {
                    result.append("+");
                } else {
                    result.append("-");
                }
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (userGuess.length() == 5) {
                for (int i = 0; i < 5; i++) {
                    previousGuesses.add(String.valueOf(userGuess.charAt(i)));
                }
                gridAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            tvResult.setText(result);

            if (result.equals(targetWord)) {
                tvResult.setText("Congratulations! You guessed the word.");
                btnSubmitGuess.setEnabled(false); // Disable submit button after winning
            }

            etGuessInput.setText(""); // Clear input field for the next guess
        }
    }
}