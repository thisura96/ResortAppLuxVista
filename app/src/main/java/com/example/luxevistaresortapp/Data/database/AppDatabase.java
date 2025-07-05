package com.example.luxevistaresortapp.Data.database;


import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;

import com.example.luxevistaresortapp.Data.database.Model.Service;
import com.example.luxevistaresortapp.Data.database.Model.User;
import com.example.luxevistaresortapp.Data.database.dao.RoomAvailabilityDao;
import com.example.luxevistaresortapp.Data.database.dao.RoomDao;
import com.example.luxevistaresortapp.Data.database.dao.ServiceDao;
import com.example.luxevistaresortapp.Data.database.dao.UserDao;
import com.example.luxevistaresortapp.Data.utilities.Constants;
import com.example.luxevistaresortapp.Data.utilities.Converters;


@Database(entities = {User.class, Rooms.class, Service.class, RoomAvailability.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract UserDao userDao();

    public abstract RoomDao roomDao();

    public abstract ServiceDao serviceDao();
    public abstract RoomAvailabilityDao roomAvailabilityDao();
}

