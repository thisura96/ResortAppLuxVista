package com.example.luxevistaresortapp.Data.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constants {
    public final static String DATABASE_NAME = "lux_resort";
    public static String getRoomType(String code) {
        switch (code) {
            case "1":
                return "Single Room";
            case "2":
                return "Double Room";
            case "3":
                return "Ocean View";
            default:
                return "Unknown";
        }
    }


}

