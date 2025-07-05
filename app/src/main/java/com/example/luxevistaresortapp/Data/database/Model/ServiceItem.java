package com.example.luxevistaresortapp.Data.database.Model;

public class ServiceItem {
    private String title;
    private String category;
    private String price;
    private int imageResId;

    public ServiceItem(String title, String category, String price, int imageResId) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
