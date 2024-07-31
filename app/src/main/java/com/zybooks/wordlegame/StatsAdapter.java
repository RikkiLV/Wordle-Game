package com.zybooks.wordlegame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {

    private List<WordleGame> games;

    public StatsAdapter(List<WordleGame> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        WordleGame game = games.get(position);
        holder.wordTextView.setText(game.getTargetWord());
        holder.triesTextView.setText(String.valueOf(game.getAttempts()));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(game.getTimeTaken());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(game.getTimeTaken()) % 60;
        holder.timeTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class StatsViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView, timeTextView, triesTextView;

        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            triesTextView = itemView.findViewById(R.id.triesTextView);
        }
    }
}
