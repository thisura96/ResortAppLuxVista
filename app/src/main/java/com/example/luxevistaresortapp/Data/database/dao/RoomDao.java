package com.example.luxevistaresortapp.Data.database.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.luxevistaresortapp.Data.database.Model.Rooms;

import java.util.List;

@Dao
public interface RoomDao {
    // Insert a single room, replacing if it already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Rooms room);

    // Insert multiple rooms
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Rooms> rooms);

    // Get all rooms
    @Query("SELECT * FROM rooms")
    List<Rooms> getAllRooms();

    // Get a specific room by ID
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    Rooms getRoomById(int roomId);

    // Update room status
    @Query("UPDATE rooms SET status = :status WHERE id = :id")
    void updateRoomStatus(int id, String status);

    // Update a room entity (used in ConfirmBookingFragment)
    @Update
    void updateRoom(Rooms room);

    // Delete a specific room
    @Query("DELETE FROM rooms WHERE id = :id")
    void deleteRoom(int id);

    // Delete all rooms
    @Query("DELETE FROM rooms")
    void deleteAllRooms();

    // Get rooms by status (e.g., to find available rooms)
    @Query("SELECT * FROM rooms WHERE status = :status")
    List<Rooms> getRoomsByStatus(String status);
}