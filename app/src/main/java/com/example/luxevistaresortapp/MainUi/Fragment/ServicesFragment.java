package com.example.luxevistaresortapp.MainUi.Fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.luxevistaresortapp.Data.database.Model.ServiceItem;
import com.example.luxevistaresortapp.MainUi.Adapter.ServiceAdapter;
import com.example.luxevistaresortapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<ServiceItem> allItems;

    private Button tabAll, tabFoods, tabOutdoor, tabDoubleRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabAll = view.findViewById(R.id.tabAll);
        tabFoods = view.findViewById(R.id.tabFoods);
        tabOutdoor = view.findViewById(R.id.tabOutdoor);
        tabDoubleRoom = view.findViewById(R.id.tabDoubleRoom);

        // Sample Data
        allItems = Arrays.asList(
                new ServiceItem("Alahakath Menu", "Food", "$190", R.drawable.service_11),
                new ServiceItem("Dinner Cosine", "Food", "$190", R.drawable.service_12),
                new ServiceItem("Cuisine Special", "Food", "$120", R.drawable.service_13),
                new ServiceItem("Family Feast", "Food", "$140", R.drawable.service_14),
                new ServiceItem("Luxury Dine-in", "Food", "$140", R.drawable.service_15),
                new ServiceItem("Chef’s Table", "Food", "$140", R.drawable.service_16),
                new ServiceItem("Romantic Dinner", "Food", "$140", R.drawable.service_17),


                new ServiceItem("Dolphin Watching", "Outdoor", "$200", R.drawable.service_1),
                new ServiceItem("Jetskiing", "Outdoor", "$220", R.drawable.service_2),
                new ServiceItem("Open Bar", "Outdoor", "$180", R.drawable.service_3),
                new ServiceItem("Paragliding", "Outdoor", "$250", R.drawable.service_4),
                new ServiceItem("Surfing", "Outdoor", "$210", R.drawable.service_5),
                new ServiceItem("Whale Watching", "Outdoor", "$230", R.drawable.service_6),

                new ServiceItem("Peace Meditation", "Meditation", "$50", R.drawable.service_7)
        );

        adapter = new ServiceAdapter(allItems);
        recyclerView.setAdapter(adapter);

        // Tab Click Events
        tabAll.setOnClickListener(v -> {
            updateTabUI(tabAll);
            filterItems("All");
        });

        tabFoods.setOnClickListener(v -> {
            updateTabUI(tabFoods);
            filterItems("Food");
        });

        tabOutdoor.setOnClickListener(v -> {
            updateTabUI(tabOutdoor);
            filterItems("Outdoor");
        });

        tabDoubleRoom.setOnClickListener(v -> {
            updateTabUI(tabDoubleRoom);
            filterItems("Meditation");
        });

        // Initial UI State
        updateTabUI(tabAll);
        filterItems("All");

        return view;
    }

    private void filterItems(String category) {
        if (category.equals("All")) {
            adapter.updateList(allItems);
        } else {
            List<ServiceItem> result = new ArrayList<>();
            for (ServiceItem item : allItems) {
                if (item.getCategory().equalsIgnoreCase(category)) {
                    result.add(item);
                }
            }
            adapter.updateList(result);
        }
    }

    private void updateTabUI(Button selectedTab) {
        int selectedColor = getResources().getColor(R.color.button_color);       // #4361EE
        int unselectedColor = getResources().getColor(android.R.color.white);    // White

        int selectedTextColor = getResources().getColor(R.color.white);          // White text on selected
        int unselectedTextColor = getResources().getColor(R.color.button_color); // Blue text on unselected

        Button[] allTabs = {tabAll, tabFoods, tabOutdoor, tabDoubleRoom};

        for (Button tab : allTabs) {
            if (tab == selectedTab) {
                tab.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                tab.setTextColor(selectedTextColor);
            } else {
                tab.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
                tab.setTextColor(unselectedTextColor);
            }
        }

        Log.d("BOOK_FRAGMENT", "Updated tab UI, selected: " + selectedTab.getText());
    }
}
