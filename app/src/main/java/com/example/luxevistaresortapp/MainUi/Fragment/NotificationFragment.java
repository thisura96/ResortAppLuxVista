package com.example.luxevistaresortapp.MainUi.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luxevistaresortapp.Data.database.Model.ActivityItem;
import com.example.luxevistaresortapp.MainUi.Adapter.NotificationAdapter;
import com.example.luxevistaresortapp.R;
import com.google.android.material.tabs.TabLayout;


public class NotificationFragment extends Fragment {
    private RecyclerView recyclerViewActivity;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);

        // Initialize TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection (e.g., update RecyclerView based on tab)
                updateRecyclerView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Initialize RecyclerView
        recyclerViewActivity = view.findViewById(R.id.recyclerViewActivity);
        recyclerViewActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter();
        recyclerViewActivity.setAdapter(adapter);

        // Initial data load
        updateRecyclerView(0);

        return view;
    }

    private void updateRecyclerView(int tabPosition) {
        // Simulate data based on tab (e.g., messages or notifications)
        if (tabPosition == 0) {
            adapter.setData(generateMessageData());
        } else {
            adapter.setData(generateNotificationData());
        }
    }

    private java.util.List<ActivityItem> generateMessageData() {
        java.util.List<ActivityItem> data = new java.util.ArrayList<>();
        data.add(new ActivityItem("Bert Pullman", "Online", "04:32 pm", "Congratulations on completing the first lesson, keep up the good work!"));
        data.add(new ActivityItem("Daniel Lawson", "Online", "04:32 pm", "Your course has been updated, you can check the new course in your study course."));
        data.add(new ActivityItem("Nguyen Shane", "Offline", "12:00 am", "Congratulations, you have completed your registration! Let's start your learning journey next."));
        data.add(new ActivityItem("Emma Wilson", "Online", "03:15 pm", "Great job on your recent quiz! Your score has been updated, and you're excelling in the program."));
        data.add(new ActivityItem("Liam Carter", "Offline", "02:45 pm", "A new module is now available for your course. Please review the content and complete the exercises by next week."));
        data.add(new ActivityItem("Olivia Bennett", "Online", "01:30 pm", "Your feedback on the last session has been noted. We’re planning an interactive Q&A session soon!"));
        data.add(new ActivityItem("Noah Parker", "Offline", "12:45 pm", "You’ve earned a badge for consistent participation. Check your profile to see your progress!"));
        data.add(new ActivityItem("Sophia Reed", "Online", "11:20 am", "A new assignment has been posted. Submit it by tomorrow to stay on track with your learning."));
        data.add(new ActivityItem("Mason Hill", "Offline", "10:50 am", "Your course certificate is ready for download. Congratulations on your achievement!"));
        data.add(new ActivityItem("Isabella Cole", "Online", "09:15 am", "Join our live webinar tomorrow at 2 PM. Register now to secure your spot!"));
        return data;
    }

    private java.util.List<ActivityItem> generateNotificationData() {
        java.util.List<ActivityItem> data = new java.util.ArrayList<>();
        data.add(new ActivityItem("Admin", "Online", "11:45 am", "We are excited to inform you about the upcoming resort event! Join us for a special evening with live music, delicious food, and exciting activities. Don't miss out on this opportunity to relax and connect with others. RSVP by 7 PM today to secure your spot."));
        data.add(new ActivityItem("Support Team", "Offline", "10:20 am", "Your recent booking request has been successfully processed. You will receive a detailed itinerary via email shortly. Please review it and let us know if there are any adjustments needed. We look forward to welcoming you to LuxeVista Resort!"));
        data.add(new ActivityItem("Manager", "Online", "09:15 am", "A new wellness package is now available! This package includes a spa session, yoga classes, and a healthy meal plan tailored to your needs. Book before the end of this week to enjoy a 15% discount. Visit our website for more details."));
        data.add(new ActivityItem("Guest Services", "Offline", "08:50 am", "Thank you for your feedback! We are working on improving our services based on your suggestions. Stay tuned for updates and special offers as a token of our appreciation for your input."));
        data.add(new ActivityItem("Event Coordinator", "Online", "07:30 am", "The pool party is scheduled for next Saturday! Expect fun games, refreshments, and a DJ. Register by Friday to participate."));
        data.add(new ActivityItem("Housekeeping", "Offline", "06:15 am", "Your room has been upgraded to a deluxe suite. Enjoy the extra space and amenities during your stay!"));
        data.add(new ActivityItem("Dining Team", "Online", "05:45 am", "Try our new seasonal menu featuring fresh, local ingredients. Visit the restaurant tonight for a special tasting event."));
        data.add(new ActivityItem("Security", "Offline", "04:30 am", "Please update your access code for added security. Follow the link in your email to complete the process."));
        data.add(new ActivityItem("Marketing", "Online", "03:15 am", "Refer a friend and get a 10% discount on your next stay. Share your unique code with friends today!"));
        data.add(new ActivityItem("Front Desk", "Offline", "02:00 am", "Your luggage has arrived and is stored safely. Collect it from the front desk at your convenience."));
        return data;
    }
}