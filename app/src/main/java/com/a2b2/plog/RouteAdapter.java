package com.a2b2.plog;

import android.content.Context;
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
    private Route selectedRoute;
    private SharedPreferencesHelper prefsHelper;

    public RouteAdapter(List<Route> routeList, OnItemClickListener itemClickListener, Context context) {
        this.routeList = routeList;
        this.itemClickListener = itemClickListener;
        this.prefsHelper = new SharedPreferencesHelper(context);
        this.selectedRoute = prefsHelper.getRoute();
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
        holder.originTextView.setText("출발지: :" + route.getOrigin());
        holder.destinationTextView.setText("도착지: " + route.getDestination());
        holder.distanceTextView.setText((int) route.getDistance() + "km");
        // route.getTime()이 초 단위로 반환된다고 가정
        int timeInSeconds = route.getTime();

        // 초를 시와 분으로 변환
        int hours = timeInSeconds / 3600;  // 1시간 = 3600초
        int minutes = (timeInSeconds % 3600) / 60;  // 나머지 초를 분으로 변환

        // 형식에 맞게 문자열로 변환
        String timeFormatted = String.format("%dh %dm", hours, minutes);

        // TextView에 설정
        holder.timeTextView.setText(timeFormatted);

//        // Highlight the selected route
//        if (selectedRoute != null && selectedRoute.getId().equals(route.getId())) {
//            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.selected_route_color)); // Color for selected item
//        } else {
//            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.default_route_color)); // Default color
//        }

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
