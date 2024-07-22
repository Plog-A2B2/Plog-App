package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {
    private List<QuestItem> questList;

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        public TextView questTextView;
        public ImageView coinImageView;
        public TextView coinNumTextView;

        public QuestViewHolder(View itemView) {
            super(itemView);
            questTextView = itemView.findViewById(R.id.questTextView);
            coinImageView = itemView.findViewById(R.id.coinImageView);
            coinNumTextView = itemView.findViewById(R.id.coinNumTextView);
        }
    }

    public QuestAdapter(List<QuestItem> questList) {
        this.questList = questList;
    }

    @Override
    public QuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quest, parent, false);
        QuestViewHolder evh = new QuestViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(QuestViewHolder holder, int position) {
        QuestItem currentItem = questList.get(position);
        holder.questTextView.setText(currentItem.getQuestText());
        holder.coinNumTextView.setText("X " + currentItem.getCoinNum());
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }
}
