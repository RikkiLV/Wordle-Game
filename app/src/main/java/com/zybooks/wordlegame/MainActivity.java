package com.zybooks.wordlegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etGuessInput;
    private Button btnSubmitGuess;
    private TextView tvResult;
    private GridView gridPreviousGuesses;
    private ArrayAdapter<String> gridAdapter;
    private String targetWord;
    private ArrayList<String> previousGuesses;

    private Dictionary dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply the selected theme
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGuessInput = findViewById(R.id.etGuessInput);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);
        tvResult = findViewById(R.id.tvResult);
        gridPreviousGuesses = findViewById(R.id.gridPreviousGuesses);
        // Init Dictionary
        dict = new Dictionary(this);
        targetWord = dict.returnRandomWord(); // Generate the hidden target word
        Log.i("Wordle", targetWord);

        // Initialize GridView adapter and previousGuesses list
        previousGuesses = new ArrayList<>();
        gridAdapter = new ArrayAdapter<>(this, R.layout.grid_item, R.id.tvGridItem, previousGuesses);
        gridPreviousGuesses.setAdapter(gridAdapter);


        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });

        gridPreviousGuesses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String previousGuess = previousGuesses.get(position);
                etGuessInput.setText(previousGuess); // Set clicked item's text to input field
            }
        });
    }

    // Theme handling
    private void applyTheme() {
        boolean isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_theme", false);
        if (isDarkTheme) {
            // Apply dark theme
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Apply light theme
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    // Menu click handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkGuess() {
        String userGuess = etGuessInput.getText().toString().toUpperCase();

        if (userGuess.length() != 5) {
            tvResult.setText("Please enter a 5-letter word.");
            return;
        }
        else if (!dict.isValidWord(userGuess)) {
            tvResult.setText("Invalid word.");
            return;
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

        tvResult.setText(result.toString());

        // Add each letter of the guess separately to previousGuesses list
        for (int i = 0; i < 5; i++) {
            previousGuesses.add(String.valueOf(userGuess.charAt(i)));
        }

        gridAdapter.notifyDataSetChanged(); // Notify adapter of data change

        if (result.toString().equals(targetWord)) {
            tvResult.setText("Congratulations! You guessed the word.");
            btnSubmitGuess.setEnabled(false); // Disable submit button after winning
        }

        etGuessInput.setText(""); // Clear input field for the next guess
    }
    // Custom Adapter for GridView
    private static class GridAdapter extends ArrayAdapter<String> {

        private final List<String> items;
        private final LayoutInflater inflater;

        public GridAdapter(MainActivity context, List<String> items) {
            super(context, R.layout.grid_item, items);
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.grid_item, parent, false);
            }

            TextView textView = view.findViewById(R.id.tvGridItem);
            textView.setText(items.get(position));

            return view;
        }

    }

}