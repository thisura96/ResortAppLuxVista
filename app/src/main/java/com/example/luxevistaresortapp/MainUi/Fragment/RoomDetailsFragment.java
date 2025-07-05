package com.example.luxevistaresortapp.MainUi.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.MainUi.Adapter.ImageCarouselAdapter;
import com.example.luxevistaresortapp.R;

import java.util.List;


public class RoomDetailsFragment extends Fragment {
    private TextView tvRoomTitle, tvPrice, tvPeople, tvDescription1, tvDescription2, tvDescription3;
    private ViewPager2 viewPager;
    private ImageButton btnLeftArrow, btnRightArrow;
    private Button btnBookNow;
    private TextView tvQuantity, btnMinus, btnPlus;
    private Rooms room;
    private int quantity = 1;
    private String selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_details, container, false);

        // Initialize views
        tvRoomTitle = view.findViewById(R.id.tvRoomTitle);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPeople = view.findViewById(R.id.tvPeople);
        tvDescription1 = view.findViewById(R.id.tvDescription1);
        tvDescription2 = view.findViewById(R.id.tvDescription2);
        tvDescription3 = view.findViewById(R.id.tvDescription3);
        viewPager = view.findViewById(R.id.viewPager);
        btnLeftArrow = view.findViewById(R.id.btnLeftArrow);
        btnRightArrow = view.findViewById(R.id.btnRightArrow);
        btnMinus = view.findViewById(R.id.btnMinus);
        btnPlus = view.findViewById(R.id.btnPlus);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnBookNow = view.findViewById(R.id.btnBookNow);

        // Get room data
        if (getArguments() != null) {
            room = getArguments().getParcelable("room");
            selectedDate = getArguments().getString("selectedDate");

            if (room != null) {
                populateRoomDetails();
            }
        }

        // Quantity selector
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantity();
            }
        });
        btnPlus.setOnClickListener(v -> {
            quantity++;
            updateQuantity();
        });

        // ViewPager2 navigation
        btnLeftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            }
        });
        btnRightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < viewPager.getAdapter().getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });

        // Book button
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Selected date " + selectedDate + " Booking Room " + room.getRoomNumber() + " for " + quantity + " unit(s)", Toast.LENGTH_SHORT).show();

            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable("room", room);
            bundle.putString("selectedDate", selectedDate);
            bundle.putInt("quantity", quantity);

            // Create instance of next fragment
            BookRoomFragment nextFragment = new BookRoomFragment(); // Replace with your actual fragment class name
            nextFragment.setArguments(bundle);

            // Navigate using FragmentTransaction
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_container, nextFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        return view;
    }

    private void populateRoomDetails() {
        tvRoomTitle.setText(room.getRoomNumber());
        tvPrice.setText(String.format("$. %,d", room.getPrice()));
        tvPeople.setText("1 people"); // Update if Rooms has people data
        tvDescription1.setText("Spacious room with modern amenities.");
        tvDescription2.setText("Includes free Wi-Fi and breakfast.");
        tvDescription3.setText("Perfect for business or leisure stays.");
        btnBookNow.setText(String.format("Book Now - $. %,d", room.getPrice() * quantity));

        // Set up ViewPager2
        List<String> imageUrls = room.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), imageUrls);
            viewPager.setAdapter(adapter);
        }
    }

    private void updateQuantity() {
        tvQuantity.setText(String.valueOf(quantity));
        btnBookNow.setText(String.format("Book Now - $. %,d", room.getPrice() * quantity));
    }
}