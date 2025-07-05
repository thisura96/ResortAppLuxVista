package com.example.luxevistaresortapp.MainUi.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.Data.utilities.Constants;
import com.example.luxevistaresortapp.MainUi.Fragment.RoomDetailsFragment;
import com.example.luxevistaresortapp.R;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Rooms> roomList;
    private OnItemClickListener onItemClickListener;

    private Context context;
    private FragmentManager fragmentManager;

    private String selectedDate;

    public RoomAdapter(Context context, List<Rooms> roomList, FragmentManager fragmentManager, String selectedDate) {
        this.context = context;
        this.roomList = roomList;
        this.fragmentManager = fragmentManager;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Rooms room = roomList.get(position);

        List<String> imageUrls = room.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String resourceName = imageUrls.get(0); // First image
            int resourceId = context.getResources().getIdentifier(
                    resourceName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                Glide.with(context)
                        .load(resourceId)
                        .transform(new RoundedCorners(24)) // Rounded corners
                        .error(R.drawable.default_image)
                        .into(holder.img_room);
            } else {
                holder.img_room.setImageResource(R.drawable.default_image);
            }
        } else {
            holder.img_room.setImageResource(R.drawable.default_image);
        }

        holder.tvRoomNumber.setText(room.getRoomNumber());
        holder.tvType.setText(Constants.getRoomType(room.getType()));
        holder.tvPrice.setText("$" + room.getPrice());
        holder.tvStatus.setText(room.getStatus());
        holder.cardView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(room);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            RoomDetailsFragment fragment = new RoomDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("room", room);
            bundle.putString("selectedDate",selectedDate);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null); // Allow back navigation
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    public void setRoomList(List<Rooms> roomList) {
        this.roomList = roomList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Rooms room);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvRoomNumber, tvType, tvPrice, tvStatus;

        ImageView img_room;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_room);
            img_room = itemView.findViewById(R.id.img_room);
            tvRoomNumber = itemView.findViewById(R.id.tv_room_number);
            tvType = itemView.findViewById(R.id.tv_room_type);
            tvPrice = itemView.findViewById(R.id.tv_room_price);
            tvStatus = itemView.findViewById(R.id.tv_room_status);
        }
    }
}