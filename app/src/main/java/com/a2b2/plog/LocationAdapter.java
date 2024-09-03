package com.a2b2.plog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//public class DepartureAdapter extends RecyclerView.Adapter<DepartureAdapter.ViewHolder> {
//
//    private List<LocalSearchDocument> documentList;
//    private OnItemClickListener listener;
//
//    public interface OnItemClickListener {
//        void onItemClick(LocalSearchDocument document);
//    }
//
//    public DepartureAdapter(List<LocalSearchDocument> documentList, OnItemClickListener listener) {
//        this.documentList = documentList;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(android.R.layout.simple_list_item_2, parent, false);
//        return new ViewHolder(view, listener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.bind(documentList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return documentList != null ? documentList.size() : 0;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView placeNameTextView;
//        TextView addressTextView;
//
//        public ViewHolder(View itemView, OnItemClickListener listener) {
//            super(itemView);
//            placeNameTextView = itemView.findViewById(android.R.id.text1);
//            addressTextView = itemView.findViewById(android.R.id.text2);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION && listener != null) {
//                        listener.onItemClick(documentList.get(position));
//                    }
//                }
//            });
//        }
//
//        public void bind(LocalSearchDocument document) {
//            if (document != null) {
//                placeNameTextView.setText(document.getPlaceName());
//                addressTextView.setText(document.getAddressName());
//            }
//        }
//    }
//}

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Place> documentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Place document);
    }

    public LocationAdapter(List<Place> documentList, OnItemClickListener listener) {
        this.documentList = documentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place document = documentList.get(position);
        holder.bind(document);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(document);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentList != null ? documentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView;
        TextView addressTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(android.R.id.text1);
            addressTextView = itemView.findViewById(android.R.id.text2);
        }

        public void bind(Place document) {
            placeNameTextView.setText(document.getPlaceName());
            addressTextView.setText(document.getAddress());
        }
    }
}
