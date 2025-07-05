package com.example.luxevistaresortapp.Data.database.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "services")
public class Service {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int price;
    public int people;

    public Service() {
    }

    public Service(String name, int price, int people) {
        this.name = name;
        this.price = price;
        this.people = people;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getPeople() {
        return people;
    }
}