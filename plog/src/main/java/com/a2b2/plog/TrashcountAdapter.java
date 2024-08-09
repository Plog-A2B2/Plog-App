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

    private List<Integer> tData;
    private String[] trashTypes;

    public TrashcountAdapter(List<Integer> data, String[] trashTypes) {
        this.tData = data;
        this.trashTypes = trashTypes;
    }

    @NonNull
    @Override
    public TrashcountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trashcount, parent, false);
        return new TrashcountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashcountViewHolder holder, int position) {
        int count = tData.get(position);
        holder.cnt.setText(String.valueOf(count));
        holder.trashtype.setText(trashTypes[position]);
        holder.min.setOnClickListener(v -> {
            if (count > 0) {
                tData.set(position, count - 1);
                notifyItemChanged(position);
                updateTotal();
            }

        });
        holder.plus.setOnClickListener(v -> {
            tData.set(position, count + 1);
            notifyItemChanged(position);
            updateTotal();
        });
    }

    @Override
    public int getItemCount() {
        return trashTypes.length;
    }
    public int getTotalCount() {
        int total = 0;
        for (int count : tData) {
            total += count;
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
            cnt = itemView.findViewById(R.id.cnt);
            plus = itemView.findViewById(R.id.plus);

        }
    }
    public List<Integer> getData() {
        return tData;
    }

    public void setData(List<Integer> data) {
        this.tData = data;
        notifyDataSetChanged();

    }
}
