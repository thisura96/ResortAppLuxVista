package com.example.luxevistaresortapp.Data.database.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "room_availability",
        foreignKeys = @ForeignKey(
                entity = Rooms.class,
                parentColumns = "id",
                childColumns = "roomId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("roomId")}
)
public class RoomAvailability {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int roomId;

    @NonNull
    public String availableDate;  // Format: yyyy-MM-dd

    public RoomAvailability(int roomId, @NonNull String availableDate) {
        this.roomId = roomId;
        this.availableDate = availableDate;
    }
}
