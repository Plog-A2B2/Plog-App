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

public class MyCommunityAdapter extends RecyclerView.Adapter<MyCommunityAdapter.MyCommunityViewHolder> {
    private List<MyCommunityItem> MycommunityList;
    private Context context;
    public MyCommunityAdapter(List<MyCommunityItem> MycommunityList, Context context){
        this.MycommunityList = MycommunityList;
        this.context = context;}
    @NonNull
    @Override
    public MyCommunityAdapter.MyCommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new MyCommunityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCommunityAdapter.MyCommunityViewHolder holder, int position) {
        MyCommunityItem MycommunityItem = MycommunityList.get(position);
        holder.badge.setImageResource(MycommunityItem.getBadge());
        holder.nickname.setText(MycommunityItem.getNickname());
        holder.date.setText(MycommunityItem.getDate());
        holder.title.setText(MycommunityItem.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommunityPostShowActivity.class);
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
        return MycommunityList.size();
    }
    public class MyCommunityViewHolder extends RecyclerView.ViewHolder {
        public ImageView badge;
        public TextView nickname, title,date;
        public MyCommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.badge);
            nickname = itemView.findViewById(R.id.nickname);
            title = itemView.findViewById(R.id.community_title);
            date = itemView.findViewById(R.id.date);
        }
    }
}
