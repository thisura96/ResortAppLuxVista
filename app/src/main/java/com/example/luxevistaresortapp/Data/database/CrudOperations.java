package com.example.luxevistaresortapp.Data.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.Data.database.Model.Service;
import com.example.luxevistaresortapp.Data.database.Model.User;

import java.util.List;

public class CrudOperations {
    private AppDatabase mDb;
    private final AppExecutors appExecutors;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CrudOperations(Context context) {
        mDb = AppDatabase.getInstance(context);
        appExecutors = AppExecutors.getInstance();
    }

    // Transaction management methods
    public void beginTransaction() {
        mDb.beginTransaction();
    }

    public void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();
    }

    public void endTransaction() {
        mDb.endTransaction();
    }

    public void insertUserData(User userData) {
        appExecutors.diskIO().execute(() -> {
            if (userData != null) {
                mDb.userDao().insertUserData(userData);
                Log.wtf("CRUD_OPERATION", "AN USER DATA INSERTED TO DB" + userData);
            }
        });
    }

    public User getUserByEmail(String email) {
        return mDb.userDao().findByEmail(email);
    }

    public User getUserByEmailAndPassword(String email, String password) {
        return mDb.userDao().findByEmailAndPassword(email, password);
    }

    public void deleteAllFromUserData() {
        appExecutors.diskIO().execute(() -> {
            mDb.userDao().deleteUserData();
            Log.wtf("CRUD_OPERATION", "ALL USER DATA DELETED FROM DB");
        });
    }

    public void deleteAllRooms() {
        appExecutors.diskIO().execute(() -> {
            mDb.roomDao().deleteAllRooms();
            Log.wtf("CRUD_OPERATION", "ALL ROOMS DELETED FROM DB");
        });
    }

    // Synchronous version for use in transactions
    public void deleteAllRoomsSync() {
        mDb.roomDao().deleteAllRooms();
        Log.wtf("CRUD_OPERATION", "ALL ROOMS DELETED FROM DB (sync)");
    }

    public List<Rooms> getAllRooms() {
        return mDb.roomDao().getAllRooms();
    }


    public void deleteRoom(int id) {
        appExecutors.diskIO().execute(() -> {
            mDb.roomDao().deleteRoom(id);
            Log.wtf("CRUD_OPERATION", "ROOM DELETED FROM DB");
        });
    }

    public long insertRoomSync(Rooms room) {
        return mDb.roomDao().insert(room);
    }

    public long insertAvailabilitySync(RoomAvailability availability) {
        return mDb.roomAvailabilityDao().insertAvailability(availability);
    }

    public List<RoomAvailability> getAllAvailableRooms() {
        return mDb.roomAvailabilityDao().getAllAvailableRooms();
    }

    public List<RoomAvailability> getAvailableRoomsForDate(String date) {
        return mDb.roomAvailabilityDao().getAvailableRoomsForDate(date);
    }

    public Rooms getRoomById(int roomId) {
        return mDb.roomDao().getRoomById(roomId);
    }

    public void clearAllAvailabilities() {
        appExecutors.diskIO().execute(() -> {
            mDb.roomAvailabilityDao().deleteAll();
            Log.wtf("CRUD_OPERATION", "All RoomAvailability rows deleted");
        });
    }

    public void clearAllAvailabilitiesSync() {
        mDb.roomAvailabilityDao().deleteAll();
        Log.wtf("CRUD_OPERATION", "All RoomAvailability rows deleted (sync)");
    }

    public void insertService(Service service) {
        appExecutors.diskIO().execute(() -> {
            if (service != null) {
                mDb.serviceDao().insert(service);
                Log.wtf("CRUD_OPERATION", "A SERVICE INSERTED TO DB");
            }
        });
    }

    public List<Service> getAllServices() {
        return mDb.serviceDao().getAllServices();
    }

    public void updateServicePrice(int id, int price) {
        appExecutors.diskIO().execute(() -> {
            mDb.serviceDao().updateServicePrice(id, price);
            Log.wtf("CRUD_OPERATION", "SERVICE PRICE UPDATED IN DB");
        });
    }

    public void deleteService(int id) {
        appExecutors.diskIO().execute(() -> {
            mDb.serviceDao().deleteService(id);
            Log.wtf("CRUD_OPERATION", "SERVICE DELETED FROM DB");
        });
    }

    public interface OnResultCallback<T> {
        void onResult(T result);
    }
}