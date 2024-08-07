package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Route route);
    }

    private List<Route> routeList;
    private OnItemClickListener itemClickListener;

    public RouteAdapter(List<Route> routeList, OnItemClickListener itemClickListener) {
        this.routeList = routeList;
        this.itemClickListener = itemClickListener;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        public TextView originTextView;
        public TextView destinationTextView;
        public TextView distanceTextView;
        public TextView timeTextView;

        public RouteViewHolder(View itemView) {
            super(itemView);
            originTextView = itemView.findViewById(R.id.textViewOrigin);
            destinationTextView = itemView.findViewById(R.id.textViewDestination);
            distanceTextView = itemView.findViewById(R.id.textViewDistance);
            timeTextView = itemView.findViewById(R.id.textViewTime);
        }
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.originTextView.setText(route.getOrigin());
        holder.destinationTextView.setText(route.getDestination());
        holder.distanceTextView.setText(route.getDistance());
        holder.timeTextView.setText(route.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }
}
