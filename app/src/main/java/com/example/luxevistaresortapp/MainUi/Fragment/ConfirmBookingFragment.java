package com.example.luxevistaresortapp.MainUi.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.luxevistaresortapp.Data.database.AppDatabase;
import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ConfirmBookingFragment extends Fragment {
    private TextView tvRoomNumber, tvGuests, tvPrice, tvBookingDate, tvCheckIn, tvCheckOut, tvServices, tvGuestsDetail;
    private TextView tvAmount, tvServicesCost, tvTax, tvTotal;
    private ImageView roomImage, backArrow;
    private Button btnContinue;
    private AppDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_booking, container, false);

        // Initialize views
        tvRoomNumber = view.findViewById(R.id.tvRoomNumber);
        tvGuests = view.findViewById(R.id.tvGuests);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvBookingDate = view.findViewById(R.id.tvBookingDate);
        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvCheckOut = view.findViewById(R.id.tvCheckOut);
        tvServices = view.findViewById(R.id.tvServices);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvServicesCost = view.findViewById(R.id.tvServicesCost);
        tvTax = view.findViewById(R.id.tvTax);
        tvTotal = view.findViewById(R.id.tvTotal);
        roomImage = view.findViewById(R.id.roomImage);
        backArrow = view.findViewById(R.id.backArrow);
        btnContinue = view.findViewById(R.id.btnContinue);
        tvGuestsDetail = view.findViewById(R.id.tvGuestsDetail);

        // Initialize database
        database = AppDatabase.getInstance(requireContext());

        // Get data from arguments
        if (getArguments() != null) {
            Rooms room = getArguments().getParcelable("room");
            String checkInDate = getArguments().getString("checkInDate");
            String checkOutDate = getArguments().getString("checkOutDate");
            int quantity = getArguments().getInt("quantity");
            ArrayList<String> selectedServices = getArguments().getStringArrayList("selectedServices");

            // Populate UI
            if (room != null) {
                tvRoomNumber.setText(room.roomNumber);
                // Load room image
                List<String> imageUrls = room.imageUrls;
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    String resourceName = imageUrls.get(0);
                    int resourceId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
                    Glide.with(this)
                            .load(resourceId != 0 ? resourceId : R.drawable.default_image)
                            .transform(new RoundedCorners(24))
                            .into(roomImage);
                } else {
                    roomImage.setImageResource(R.drawable.default_image);
                }
                // Price in USD
                double usdPrice = room.price;
                tvPrice.setText(String.format("$%.2f / %d people", usdPrice, quantity));
                // Calculate amount, services cost, and total
                double amount = usdPrice * quantity;
                int serviceCount = selectedServices != null ? selectedServices.size() : 0;
                double servicesCost = serviceCount * 25.0; // $25 per service
                double tax = 12.0; // Fixed tax in USD
                double total = amount + servicesCost + tax;
                tvAmount.setText(String.format("$%.2f x %d", usdPrice, quantity));
                tvServicesCost.setText(String.format("$%.2f (%d x $25)", servicesCost, serviceCount));
                tvTax.setText(String.format("$%.2f", tax));
                tvTotal.setText(String.format("$%.2f", total));

                // Continue to Checkout button
                btnContinue.setOnClickListener(v -> {
                    Log.d("ConfirmBookingFragment", "Attempting booking for room " + room.id + " from " + checkInDate + " to " + checkOutDate);
                    new Thread(() -> {
                        try {
                            // Check if the room is available
                            boolean isAvailable = checkRoomAvailability(room.id, checkInDate, checkOutDate);
                            System.out.println("isAvailable: " + isAvailable);

                            if (isAvailable) {
                                // No conflicts, proceed with booking
                                updateRoomAndAvailability(room, checkInDate, checkOutDate);
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Booking confirmed!", Toast.LENGTH_SHORT).show();
                                    getParentFragmentManager().popBackStack("RoomListFragment", 0);
                                });
                            } else {
                                // Conflicts exist, handle replacement
                                List<RoomAvailability> conflicts = database.roomAvailabilityDao()
                                        .checkAvailability(room.id, checkInDate, checkOutDate);
                                String conflictDates = conflicts.stream()
                                        .map(booking -> booking.availableDate)
                                        .collect(Collectors.joining(", "));
                                Log.d("ConfirmBookingFragment", "Conflicts found for room " + room.id + ": " + conflictDates);

                                // Get all existing bookings for the room to identify non-overlapping dates
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                sdf.setLenient(false);
                                Date newCheckIn = sdf.parse(checkInDate);
                                Date newCheckOut = sdf.parse(checkOutDate);

                                // Get all RoomAvailability records for the room to find the full booking range
                                List<RoomAvailability> allBookings = database.roomAvailabilityDao().getAllBookingsForRoom(room.id);
                                if (allBookings.isEmpty()) {
                                    throw new IllegalStateException("No bookings found for room " + room.id + " despite conflict.");
                                }

                                // Find the earliest and latest booking dates
                                String earliestBookingDate = allBookings.stream()
                                        .map(booking -> booking.availableDate)
                                        .min(String::compareTo)
                                        .orElse(checkInDate);
                                String latestBookingDate = allBookings.stream()
                                        .map(booking -> booking.availableDate)
                                        .max(String::compareTo)
                                        .orElse(checkOutDate);

                                // Parse the full booking range
                                Date existingCheckIn = sdf.parse(earliestBookingDate);
                                Date existingCheckOut = sdf.parse(latestBookingDate);
                                Calendar calendar = Calendar.getInstance();

                                // Collect remaining dates (non-overlapping with new booking)
                                List<String> remainingDates = new ArrayList<>();
                                calendar.setTime(existingCheckIn);
                                // Calculate the day before newCheckOut
                                Calendar tempCal = Calendar.getInstance();
                                tempCal.setTime(newCheckOut);
                                tempCal.add(Calendar.DAY_OF_MONTH, -1); // Subtract one day
                                Date newCheckOutMinusOne = tempCal.getTime();

                                while (calendar.getTime().before(existingCheckOut)) {
                                    String dateStr = sdf.format(calendar.getTime());
                                    Date currentDate = sdf.parse(dateStr);
                                    if (currentDate.before(newCheckIn) || currentDate.after(newCheckOutMinusOne)) {
                                        remainingDates.add(dateStr);
                                    }
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                }

                                // Run replacement in a transaction
                                database.runInTransaction(() -> {
                                    // Delete conflicting bookings
                                    database.roomAvailabilityDao().deleteAvailabilityForRoomAndDates(room.id, checkInDate, checkOutDate);

                                    // Insert new booking
                                    try {
                                        updateRoomAndAvailability(room, checkInDate, checkOutDate);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }

                                    // Insert remaining dates from the original booking
                                    List<RoomAvailability> remainingAvailabilities = new ArrayList<>();
                                    for (String date : remainingDates) {
                                        remainingAvailabilities.add(new RoomAvailability(room.id, date));
                                    }
                                    if (!remainingAvailabilities.isEmpty()) {
                                        database.roomAvailabilityDao().insertAllAvailability(remainingAvailabilities);
                                        Log.d("ConfirmBookingFragment", "Inserted " + remainingAvailabilities.size() + " remaining availability records for room " + room.id);
                                    }
                                });

                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Booking confirmed!", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, new BookSuccessFragment())
                                            .commit();
                                });
                            }
                        } catch (ParseException e) {
                            Log.e("ConfirmBookingFragment", "Date processing error: " + e.getMessage());
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        } catch (Exception e) {
                            Log.e("ConfirmBookingFragment", "Booking error: " + e.getMessage(), e);
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    }).start();
                });
            }
            tvGuests.setText(String.format("%d people", quantity));
            tvCheckIn.setText(checkInDate != null ? checkInDate : "yyyy-MM-dd");
            tvCheckOut.setText(checkOutDate != null ? checkOutDate : "yyyy-MM-dd");
            // Set booking date to current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            tvBookingDate.setText(sdf.format(new Date()));
            // Display services
            if (selectedServices != null && !selectedServices.isEmpty()) {
                tvServices.setText(String.join(", ", selectedServices));
            } else {
                tvServices.setText("None");
            }
        }

        // Back button
        backArrow.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private boolean checkRoomAvailability(int roomId, String checkInDate, String checkOutDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false); // Strict date parsing

        // Validate date format
        if (!isValidDateFormat(checkInDate)) {
            throw new ParseException("Invalid check-in date format. Use yyyy-MM-dd (e.g., 2025-07-05).", 0);
        }
        if (!isValidDateFormat(checkOutDate)) {
            throw new ParseException("Invalid check-out date format. Use yyyy-MM-dd (e.g., 2025-07-07).", 0);
        }

        Date start = sdf.parse(checkInDate);
        Date end = sdf.parse(checkOutDate);

        // Validate that checkInDate is before checkOutDate
        if (start.after(end) || start.equals(end)) {
            throw new ParseException("Check-in date (" + checkInDate + ") must be before check-out date (" + checkOutDate + ").", 0);
        }

        // Validate that checkInDate is not before today (compare dates only, not time)
        Calendar currentCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        // Truncate time portion to compare only dates
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        if (startCal.getTime().before(currentCal.getTime())) {
            throw new ParseException("Check-in date (" + checkInDate + ") cannot be before today.", 0);
        }

        // Check if there are any bookings for the room in the given date range
        List<RoomAvailability> existingBookings = database.roomAvailabilityDao()
                .checkAvailability(roomId, checkInDate, checkOutDate);

        // Log conflicting dates for debugging
        if (!existingBookings.isEmpty()) {
            StringBuilder conflictDates = new StringBuilder();
            for (RoomAvailability booking : existingBookings) {
                conflictDates.append(booking.availableDate).append(", ");
            }
            Log.d("ConfirmBookingFragment", "Conflicting dates for room " + roomId + ": " + conflictDates);
        }

        return existingBookings.isEmpty();
    }

    private boolean isValidDateFormat(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private void updateRoomAndAvailability(Rooms room, String checkInDate, String checkOutDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);

        // Validate date format
        if (!isValidDateFormat(checkInDate)) {
            throw new ParseException("Invalid check-in date format. Use yyyy-MM-dd (e.g., 2025-07-05).", 0);
        }
        if (!isValidDateFormat(checkOutDate)) {
            throw new ParseException("Invalid check-out date format. Use yyyy-MM-dd (e.g., 2025-07-07).", 0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(checkInDate));
        Date endDate = sdf.parse(checkOutDate);

        // Validate date range
        if (calendar.getTime().after(endDate) || calendar.getTime().equals(endDate)) {
            throw new ParseException("Check-in date (" + checkInDate + ") must be before check-out date (" + checkOutDate + ").", 0);
        }

        // Run database operations in a transaction
        database.runInTransaction(() -> {
            // Update room status to "Booked"
            room.status = "Booked";
            database.roomDao().updateRoom(room);

            // Insert availability records for each day in the range [checkInDate, checkOutDate)
            List<RoomAvailability> availabilities = new ArrayList<>();
            while (calendar.getTime().before(endDate)) {
                String dateStr = sdf.format(calendar.getTime());
                availabilities.add(new RoomAvailability(room.id, dateStr));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            database.roomAvailabilityDao().insertAllAvailability(availabilities);
            Log.d("ConfirmBookingFragment", "Inserted " + availabilities.size() + " availability records for room " + room.id);
        });
    }
}