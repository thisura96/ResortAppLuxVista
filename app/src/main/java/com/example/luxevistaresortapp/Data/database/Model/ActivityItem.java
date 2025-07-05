package com.example.luxevistaresortapp.Data.database.Model;

public class ActivityItem {
    private String name;
    private String status;
    private String time;
    private String message;

    public ActivityItem(String name, String status, String time, String message) {
        this.name = name;
        this.status = status;
        this.time = time;
        this.message = message;
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public String getMessage() { return message; }
}