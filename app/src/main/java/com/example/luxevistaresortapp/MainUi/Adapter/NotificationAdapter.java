package com.example.luxevistaresortapp.MainUi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevistaresortapp.Data.database.Model.ActivityItem;
import com.example.luxevistaresortapp.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<ActivityItem> data;

    public void setData(List<ActivityItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityItem item = data.get(position);
        holder.name.setText(item.getName());
        holder.status.setText(item.getStatus());
        holder.time.setText(item.getTime());
        holder.message.setText(item.getMessage());

        // Set status color: Green for "Online", Red for "Offline"
        if ("Online".equals(item.getStatus())) {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else if ("Offline".equals(item.getStatus())) {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, time, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            status = itemView.findViewById(R.id.textStatus);
            time = itemView.findViewById(R.id.textTime);
            message = itemView.findViewById(R.id.textMessage);
        }
    }
}