package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {
    private List<RankItem> rankList;

    public RankAdapter(List<RankItem> rankList) {
        this.rankList = rankList;
    }

    @NonNull
    @Override
    public RankAdapter.RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false);
        //RankAdapter.RankViewHolder rvh = new RankAdapter.RankViewHolder(v);
        return new RankViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.RankViewHolder holder, int position) {
        RankItem rankItem = rankList.get(position);
        holder.rank.setText(rankItem.getRank()+"등");
        holder.username.setText(rankItem.getUsername());
        holder.rank_score.setText(rankItem.getScore());

    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    public class RankViewHolder extends RecyclerView.ViewHolder {

        public TextView rank, username, rank_score;
        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank);
            username = itemView.findViewById(R.id.rank_username);
            rank_score = itemView.findViewById(R.id.rank_score);

        }
    }
}
