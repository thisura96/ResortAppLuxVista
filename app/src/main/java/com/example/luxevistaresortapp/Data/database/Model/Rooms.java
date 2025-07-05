package com.example.luxevistaresortapp.Data.database.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "rooms")
public class Rooms implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String roomNumber;
    public String type;
    public int price;
    public String status;
    public List<String> imageUrls; // Store 3 image URLs

    public Rooms(String roomNumber, String type, int price, String status, List<String> imageUrls) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.imageUrls = imageUrls;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    // Parcelable implementation
    protected Rooms(Parcel in) {
        id = in.readInt();
        roomNumber = in.readString();
        type = in.readString();
        price = in.readInt();
        status = in.readString();
        imageUrls = in.createStringArrayList();
    }

    public static final Creator<Rooms> CREATOR = new Creator<Rooms>() {
        @Override
        public Rooms createFromParcel(Parcel in) {
            return new Rooms(in);
        }

        @Override
        public Rooms[] newArray(int size) {
            return new Rooms[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(roomNumber);
        dest.writeString(type);
        dest.writeInt(price);
        dest.writeString(status);
        dest.writeStringList(imageUrls);
    }
}