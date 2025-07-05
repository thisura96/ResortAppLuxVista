package com.example.luxevistaresortapp.MainUi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luxevistaresortapp.Data.database.Model.ServiceItem;
import com.example.luxevistaresortapp.R;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> serviceList;

    public ServiceAdapter(List<ServiceItem> serviceList) {
        this.serviceList = serviceList;
    }

    public void updateList(List<ServiceItem> newList) {
        this.serviceList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem item = serviceList.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText(item.getPrice());
        holder.serviceType.setText(item.getCategory());
        holder.imageView.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price,serviceType;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.serviceImage);
            title = itemView.findViewById(R.id.serviceTitle);
            price = itemView.findViewById(R.id.servicePrice);
            serviceType  = itemView.findViewById(R.id.serviceType);
        }
    }
}
