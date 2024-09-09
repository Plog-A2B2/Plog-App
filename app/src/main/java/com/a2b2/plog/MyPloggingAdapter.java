package com.a2b2.plog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyPloggingAdapter extends RecyclerView.Adapter<MyPloggingAdapter.MyPloggingViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MyPloggingItem activity);
    }

    private List<MyPloggingItem> myPloggingItemList;
    private OnItemClickListener itemClickListener;

    public MyPloggingAdapter(List<MyPloggingItem> myPloggingItemList, MyPloggingActivity itemClickListener, Context context) {
        this.myPloggingItemList = myPloggingItemList;
        this.itemClickListener = itemClickListener;
    }

    public static class MyPloggingViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView timeTextView;
        public TextView distanceTextView;
        public TextView trashSumTextView;

        public MyPloggingViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            trashSumTextView = itemView.findViewById(R.id.trashSumTextView);
        }
    }

    @Override
    public MyPloggingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_plogging, parent, false);
        return new MyPloggingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPloggingViewHolder holder, int position) {
        MyPloggingItem activity = myPloggingItemList.get(position);

        // 날짜 설정
        holder.dateTextView.setText(activity.getPloggingDate());

        // 거리 설정 (km 단위로 표시)
        holder.distanceTextView.setText(activity.getDistance() + "km");

        // 초를 시, 분으로 변환
        int timeInSeconds = activity.getTime();
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        String timeFormatted = String.format("%dh %dm", hours, minutes);
        holder.timeTextView.setText(timeFormatted);

        // 쓰레기 총 개수 설정 (임의의 값 사용)
        holder.trashSumTextView.setText("주운 쓰레기 총 개수 : "+ activity.getTrashSum() + "개");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPloggingItemList.size();
    }
}
