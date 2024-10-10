package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PloggerAdapter extends RecyclerView.Adapter<PloggerAdapter.PloggingViewHolder> {

    private List<RealtimePloggerItem> ploggingItems;

    public PloggerAdapter(List<RealtimePloggerItem> ploggingItems) {
        //this.ploggingItems = ploggingItems;
        this.ploggingItems = (ploggingItems != null) ? ploggingItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public PloggingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plogger, parent, false);
        return new PloggingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PloggingViewHolder holder, int position) {
        RealtimePloggerItem item = ploggingItems.get(position);
        holder.icon.setImageResource(item.getIcon());
        holder.distance.setText(item.getNickname());
    }

    @Override
    public int getItemCount() {
        return ploggingItems.size();
    }

    public static class PloggingViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView distance;

        public PloggingViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.runner);
            distance = itemView.findViewById(R.id.nickname);
        }
    }
}
