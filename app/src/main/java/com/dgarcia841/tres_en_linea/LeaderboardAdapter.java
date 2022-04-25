package com.dgarcia841.tres_en_linea;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    GameServer.IPlayer[] board;

    public LeaderboardAdapter(GameServer.IPlayer[] board) {
        this.board = board;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_leaderboard_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(board[position]);
    }

    @Override
    public int getItemCount() {
        Log.d("COUNT #2: ", String.valueOf(this.board.length));
        return this.board.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_board_username);
            tvScore = (TextView) itemView.findViewById(R.id.tv_board_score);
        }

        public void set(GameServer.IPlayer player) {
            tvUsername.setText(player.username);
            tvScore.setText(String.valueOf(player.score));
        }
    }
}
