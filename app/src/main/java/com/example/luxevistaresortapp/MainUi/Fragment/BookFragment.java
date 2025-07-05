package com.example.luxevistaresortapp.MainUi.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luxevistaresortapp.Data.database.AppExecutors;
import com.example.luxevistaresortapp.Data.database.CrudOperations;
import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.MainUi.Adapter.RoomAdapter;
import com.example.luxevistaresortapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BookFragment extends Fragment {
    private CrudOperations crudOperations;
    private RoomAdapter roomAdapter;
    private RecyclerView rvRooms;
    private View fabSelectDate;
    private int selectedRoomId = -1;
    private ProgressBar progressBar;
    private Button tabAll, tabOceanView, tabSingleRoom, tabDoubleRoom;
    private TextView calendarText;
    private String selectDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        fabSelectDate = view.findViewById(R.id.calendarBox);
        progressBar = view.findViewById(R.id.progress_bar);
        crudOperations = new CrudOperations(requireContext());

        calendarText = view.findViewById(R.id.calendarText);

        // Reference tab buttons
        tabAll = view.findViewById(R.id.tabAll);
        tabOceanView = view.findViewById(R.id.tabOceanView);
        tabSingleRoom = view.findViewById(R.id.tabSingleRoom);
        tabDoubleRoom = view.findViewById(R.id.tabDoubleRoom);

        // Set up tab click listeners
        tabAll.setOnClickListener(v -> {
            updateTabUI(tabAll, true);
            loadRoomsByType(null); // null for all rooms
        });

        tabSingleRoom.setOnClickListener(v -> {
            updateTabUI(tabSingleRoom, true);
            loadRoomsByType("1"); // Single Room
        });
        tabDoubleRoom.setOnClickListener(v -> {
            updateTabUI(tabDoubleRoom, true);
            loadRoomsByType("2"); // Double Room
        });
        tabOceanView.setOnClickListener(v -> {
            updateTabUI(tabOceanView, true);
            loadRoomsByType("3"); // Ocean View
        });
        // Set initial tab state
        updateTabUI(tabAll, true);



        selectDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        calendarText.setText(selectDate);
        Log.d("BOOK_FRAGMENT", "Initial date set: " + selectDate);


        rvRooms = view.findViewById(R.id.recyclerView);
        rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        roomAdapter = new RoomAdapter(getContext(), null, getParentFragmentManager(), selectDate);
        rvRooms.setAdapter(roomAdapter);

        // Set up RecyclerView item click listener for booking
        roomAdapter.setOnItemClickListener(room -> {
            selectedRoomId = room.id;
            Log.d("BOOK_FRAGMENT", "Selected room ID: " + selectedRoomId);
        });

        // Initialize rooms and load for the current date
        loadRoomsByType(null);

        fabSelectDate.setOnClickListener(view1 -> {
            showDatePickerDialog();
        });

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    selectDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
                    calendarText.setText(selectDate);
                    Log.d("BOOK_FRAGMENT", "Date selected: " + selectDate);

                    // Re-initialize rooms and load for the selected date
                    loadRoomsByType(null);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadRoomsByType(String type) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        Log.d("BOOK_FRAGMENT", "Loading rooms for date: " + selectDate + ", type: " + (type == null ? "All" : type));

        AppExecutors.getInstance().diskIO().execute(() -> {
            // 1) Get all availabilities for the selected date
            List<RoomAvailability> availabilityList = crudOperations.getAvailableRoomsForDate(selectDate);
            Log.d("LOAD_ROOMS", "Available rooms count for date " + selectDate + ": " + availabilityList.size());

            if (availabilityList.isEmpty()) {
                Log.w("LOAD_ROOMS", "No available rooms for date " + selectDate);
                requireActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    roomAdapter.setRoomList(Collections.emptyList());
                });
                return;
            }

            // 2) Collect unique roomIds
            List<Integer> roomIds = new ArrayList<>();
            for (RoomAvailability avail : availabilityList) {
                if (!roomIds.contains(avail.roomId)) {
                    roomIds.add(avail.roomId);
                }
            }

            // 3) Fetch rooms by IDs and filter by type
            List<Rooms> rooms = new ArrayList<>();
            for (int id : roomIds) {
                Rooms room = crudOperations.getRoomById(id);
                if (room != null && (type == null || room.type.equals(type))) {
                    rooms.add(room);
                } else if (room == null) {
                    Log.w("LOAD_ROOMS", "Room not found for ID: " + id);
                }
            }

            // 4) Update UI
            requireActivity().runOnUiThread(() -> {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.d("LOAD_ROOMS", "Updating UI with " + rooms.size() + " rooms for type: " + (type == null ? "All" : type));
                roomAdapter.setRoomList(rooms);
            });

            // 5) Post-load check for room persistence
            List<Rooms> allRooms = crudOperations.getAllRooms();
            Log.d("LOAD_ROOMS_CHECK", "Total rooms after load: " + allRooms.size());
            if (allRooms.isEmpty()) {
                Log.e("LOAD_ROOMS_CHECK", "No rooms found after load! Possible external deleteAllRooms call.");
            } else {
                for (Rooms r : allRooms) {
                    Log.d("LOAD_ROOMS_CHECK", "Room persists: " + r);
                }
            }
        });
    }

    private void updateTabUI(Button selectedTab, boolean isSelected) {
        // Define color state lists
        int selectedColor = R.color.button_color; // #4361EE
        int unselectedColor = android.R.color.white;
        int selectedTextColor = R.color.white;
        int unselectedTextColor = R.color.button_color;

        // Reset all tabs
        tabAll.setBackgroundTintList(getResources().getColorStateList(selectedTab == tabAll ? selectedColor : unselectedColor));
        tabAll.setTextColor(getResources().getColorStateList(selectedTab == tabAll ? selectedTextColor : unselectedTextColor));
        tabOceanView.setBackgroundTintList(getResources().getColorStateList(selectedTab == tabOceanView ? selectedColor : unselectedColor));
        tabOceanView.setTextColor(getResources().getColorStateList(selectedTab == tabOceanView ? selectedTextColor : unselectedTextColor));
        tabSingleRoom.setBackgroundTintList(getResources().getColorStateList(selectedTab == tabSingleRoom ? selectedColor : unselectedColor));
        tabSingleRoom.setTextColor(getResources().getColorStateList(selectedTab == tabSingleRoom ? selectedTextColor : unselectedTextColor));
        tabDoubleRoom.setBackgroundTintList(getResources().getColorStateList(selectedTab == tabDoubleRoom ? selectedColor : unselectedColor));
        tabDoubleRoom.setTextColor(getResources().getColorStateList(selectedTab == tabDoubleRoom ? selectedTextColor : unselectedTextColor));

        Log.d("BOOK_FRAGMENT", "Updated tab UI, selected: " + selectedTab.getText());
    }


}