package com.a2b2.plog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class TrashcountAdapter extends RecyclerView.Adapter<TrashcountAdapter.TrashcountViewHolder>  {

    private List<TrashcountItem> tData;
    private String[] trashTypes;
    private OnTrashTypeUpdateListener updateListener;
    public TrashcountAdapter(List<TrashcountItem> data, String[] trashTypes,  OnTrashTypeUpdateListener updateListener) {
        this.tData = data;
        this.updateListener = updateListener;

        this.trashTypes = trashTypes;
    }
    public interface OnTrashTypeUpdateListener {
        void onTrashTypeUpdate(String trashType, int count);
    }
    @NonNull
    @Override
    public TrashcountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trashcount, parent, false);
        return new TrashcountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashcountViewHolder holder, int position) {
        TrashcountItem item = tData.get(position);
        int count = item.getCnt();

        holder.cnt.setText(String.valueOf(count));
        holder.trashtype.setText(item.getTrashtype());

        holder.min.setOnClickListener(v -> {
            if (count > 0) {
                item.setCnt(count - 1);
                notifyItemChanged(position);
                updateListener.onTrashTypeUpdate(item.getTrashtype(), count - 1);
                updateTotal();
            }
        });

        holder.plus.setOnClickListener(v -> {
            item.setCnt(count + 1);
            notifyItemChanged(position);
            updateListener.onTrashTypeUpdate(item.getTrashtype(), count + 1);
            updateTotal();
        });
    }


    @Override
    public int getItemCount() {
        return trashTypes.length;
    }
    public int getTotalCount() {
        int total = 0;
        for (TrashcountItem item : tData) {
            total += item.getCnt();
        }
        return total;
    }
    private void updateTotal() {
        // Update total count in the main activity
        if (onTotalChangeListener != null) {
            onTotalChangeListener.onTotalChange(getTotalCount());
        }
    }

    private OnTotalChangeListener onTotalChangeListener;

    public void setOnTotalChangeListener(OnTotalChangeListener listener) {
        this.onTotalChangeListener = listener;
    }

    public interface OnTotalChangeListener {
        void onTotalChange(int total);
    }
    public class TrashcountViewHolder extends RecyclerView.ViewHolder {
        ImageView min, plus;
        public TextView trashtype,cnt;
        public TrashcountViewHolder(@NonNull View itemView) {
            super(itemView);
            trashtype = itemView.findViewById(R.id.trashtype);
            min = itemView.findViewById(R.id.min);
            cnt = itemView.findViewById(R.id.cntTxt);
            plus = itemView.findViewById(R.id.plus);

        }
    }
    public List<TrashcountItem> getData() {
        return tData;
    }

    public void setData(List<TrashcountItem> data) {
        this.tData = data;
        notifyDataSetChanged();
    }
    public void updateData(List<TrashcountItem> newList) {
        this.tData.clear();
        this.tData.addAll(newList);
        notifyDataSetChanged();
    }
}
