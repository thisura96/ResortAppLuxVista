package com.example.luxevistaresortapp.MainUi.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.R;


import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookRoomFragment extends Fragment {
    private TextView tvCheckIn, tvCheckOut, tvTitle;
    private ImageView btnClose;
    private Button btnClear, btnContinue;
    private TextView chip1, chip2, chip3, chip4, chip5, chip6;
    private List<String> selectedServices;
    private Rooms room;
    private String checkInDate;
    private String checkOutDate;
    private int quantity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_room, container, false);

        // Initialize views
        tvTitle = view.findViewById(R.id.tvRoomTitle);
        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvCheckOut = view.findViewById(R.id.tvCheckOut);
        btnClose = view.findViewById(R.id.btnClose);
        btnClear = view.findViewById(R.id.btnClear);
        btnContinue = view.findViewById(R.id.btnContinue);
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);
        chip4 = view.findViewById(R.id.chip4);
        chip5 = view.findViewById(R.id.chip5);
        chip6 = view.findViewById(R.id.chip6);
        selectedServices = new ArrayList<>();

        // Get data from arguments
        if (getArguments() != null) {
            room = getArguments().getParcelable("room");
            checkInDate = getArguments().getString("selectedDate");
            quantity = getArguments().getInt("quantity");

            // Set room title and check-in date
            if (room != null) {
                tvTitle.setText("Book - " + room.getRoomNumber());
            }
            if (checkInDate != null) {
                tvCheckIn.setText(checkInDate);
            }
        }

        // Date picker for check-in
        tvCheckIn.setOnClickListener(v -> showDatePicker(true));

        // Date picker for check-out
        tvCheckOut.setOnClickListener(v -> showDatePicker(false));

        // Service chip selection
        setupChip(chip1, "Cosine");
        setupChip(chip2, "Binge-Jump");
        setupChip(chip3, "Para-Guiding");
        setupChip(chip4, "Campfire");
        setupChip(chip5, "Spa");
        setupChip(chip6, "Beach Tour");

        // Clear button
        btnClear.setOnClickListener(v -> clearUI());

        // Back button (Close)
        btnClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // Continue button
        btnContinue.setOnClickListener(v -> {
            if (checkOutDate == null || checkOutDate.equals("DD/MM/YYYY")) {
                Toast.makeText(getContext(), "Please select a check-out date", Toast.LENGTH_SHORT).show();
                return;
            }
            ConfirmBookingFragment fragment = new ConfirmBookingFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("room", room);
            bundle.putString("checkInDate", checkInDate);
            bundle.putString("checkOutDate", checkOutDate);
            bundle.putInt("quantity", quantity);
            bundle.putStringArrayList("selectedServices", new ArrayList<>(selectedServices));
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    if (isCheckIn) {
                        checkInDate = sdf.format(selectedDate.getTime());
                        tvCheckIn.setText(checkInDate);
                    } else {
                        checkOutDate = sdf.format(selectedDate.getTime());
                        tvCheckOut.setText(checkOutDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // Restrict past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void setupChip(TextView chip, String service) {
        chip.setOnClickListener(v -> {
            if (selectedServices.contains(service)) {
                selectedServices.remove(service);
                chip.setBackgroundResource(R.drawable.bg_chip_unselected); // Define this drawable
                chip.setTextAppearance(R.style.Chip_Unselected);
            } else {
                selectedServices.add(service);
                chip.setBackgroundResource(R.drawable.bg_chip_selected); // Define this drawable
                chip.setTextAppearance(R.style.Chip_Selected);
            }
        });
        // Initialize chip state
        if (selectedServices.contains(service)) {
            chip.setBackgroundResource(R.drawable.bg_chip_selected);
            chip.setTextAppearance(R.style.Chip_Selected);
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            chip.setTextAppearance(R.style.Chip_Unselected);
        }
    }

    private void clearUI() {
        // Reset services
        selectedServices.clear();
        chip1.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip1.setTextAppearance(R.style.Chip_Unselected);
        chip2.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip2.setTextAppearance(R.style.Chip_Unselected);
        chip3.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip3.setTextAppearance(R.style.Chip_Unselected);
        chip4.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip4.setTextAppearance(R.style.Chip_Unselected);
        chip5.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip5.setTextAppearance(R.style.Chip_Unselected);
        chip6.setBackgroundResource(R.drawable.bg_chip_unselected);
        chip6.setTextAppearance(R.style.Chip_Unselected);

        // Reset check-out date
        checkOutDate = null;
        tvCheckOut.setText("yyyy-MM-dd");
    }
}