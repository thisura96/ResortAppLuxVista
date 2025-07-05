package com.example.luxevistaresortapp.Data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;

import java.util.List;

@Dao
public interface RoomAvailabilityDao {
    // Insert a single availability record, replacing if it exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAvailability(RoomAvailability availability);

    // Insert multiple availability records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllAvailability(List<RoomAvailability> availabilityList);

    // Get all availability records
    @Query("SELECT * FROM room_availability")
    List<RoomAvailability> getAllAvailableRooms();

    // Get availability records for a specific date
    @Query("SELECT * FROM room_availability WHERE availableDate = :date")
    List<RoomAvailability> getAvailableRoomsForDate(String date);

    // Check availability for a room in a date range (used in ConfirmBookingFragment)
    @Query("SELECT * FROM room_availability WHERE roomId = :roomId AND availableDate >= :startDate AND availableDate < :endDate")
    List<RoomAvailability> checkAvailability(int roomId, String startDate, String endDate);

    @Query("SELECT * FROM room_availability WHERE roomId = :roomId")
    List<RoomAvailability> getAllBookingsForRoom(int roomId);
    // Get all booked dates for a specific room
    @Query("SELECT * FROM room_availability WHERE roomId = :roomId")
    List<RoomAvailability> getDatesForRoom(int roomId);

    // Delete availability records for a specific room
    @Query("DELETE FROM room_availability WHERE roomId = :roomId")
    void deleteAvailabilityForRoom(int roomId);

    // Delete availability records for a specific room and date range
    @Query("DELETE FROM room_availability WHERE roomId = :roomId AND availableDate >= :startDate AND availableDate < :endDate")
    void deleteAvailabilityForDateRange(int roomId, String startDate, String endDate);
    @Query("DELETE FROM room_availability WHERE roomId = :roomId AND availableDate >= :checkInDate AND availableDate < :checkOutDate")
    void deleteAvailabilityForRoomAndDates(int roomId, String checkInDate, String checkOutDate);
    // Delete all availability records
    @Query("DELETE FROM room_availability")
    void deleteAll();
}
