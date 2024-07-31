package com.zybooks.wordlegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class GuessAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> guesses;

    public GuessAdapter(Context context, ArrayList<String> guesses) {
        this.context = context;
        this.guesses = guesses;
    }

    public void reset(ArrayList<String> newGuesses){
        guesses = newGuesses;
    }

    @Override
    public int getCount() {
        return guesses.size();
    }

    @Override
    public Object getItem(int position) {
        return guesses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView tvGridItem = convertView.findViewById(R.id.tvGridItem);
        String[] guessInfo = guesses.get(position).split(":");

        tvGridItem.setText(guessInfo[0]);
        String color = guessInfo[1];

        switch (color) {
            case "green":
                tvGridItem.setBackgroundColor(context.getResources().getColor(R.color.green));
                break;
            case "yellow":
                tvGridItem.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                break;
            case "gray":
                tvGridItem.setBackgroundColor(context.getResources().getColor(R.color.gray));
                break;
        }

        return convertView;
    }
}
