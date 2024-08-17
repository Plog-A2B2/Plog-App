package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<CommunityItem> communityList;
    public CommunityAdapter(List<CommunityItem> communityList){this.communityList = communityList;}
    @NonNull
    @Override
    public CommunityAdapter.CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.CommunityViewHolder holder, int position) {

        CommunityItem communityItem = communityList.get(position);
        holder.badge.setImageResource(communityItem.getBadge());
        holder.nickname.setText(communityItem.getNickname());
        holder.date.setText(communityItem.getDate());
        holder.title.setText(communityItem.getTitle());


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        public ImageView badge;
        public TextView nickname, title,date;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.badge);
            nickname = itemView.findViewById(R.id.nickname);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);

        }
    }
}
