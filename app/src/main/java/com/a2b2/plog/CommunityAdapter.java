package com.a2b2.plog;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<CommunityList> communityList;
    private Context context;
    public CommunityAdapter(List<CommunityList> communityList, Context context){
        this.communityList = communityList;
        this.context = context;}
    @NonNull
    @Override
    public CommunityAdapter.CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.CommunityViewHolder holder, int position) {

        CommunityList communityItem = communityList.get(position);
        holder.badge.setImageResource(communityItem.getBadge());
        holder.nickname.setText(communityItem.getUserNickname());
        holder.date.setText(communityItem.getTime());
        holder.title.setText(communityItem.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityPostShowActivity.class);
            intent.putExtra("postId", communityItem.getPostId()); // postId를 Intent로 전달
            context.startActivity(intent);
        });
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_green_square);
        // 홀수 포지션일 때 배경색 변경
        if (position % 2 == 1) {  // 포지션이 홀수인지 확인
            background.setColor(ContextCompat.getColor(context, R.color.gray));
        } else {
            background.setColor(ContextCompat.getColor(context, R.color.green)); // 기본 색상 또는 짝수 색상
        }
        holder.itemView.setBackground(background);
    }
    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        public ImageView badge;
        public TextView nickname, title,date;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.badge);
            nickname = itemView.findViewById(R.id.nickname);
            title = itemView.findViewById(R.id.community_title);
            date = itemView.findViewById(R.id.date);
        }
    }
}
