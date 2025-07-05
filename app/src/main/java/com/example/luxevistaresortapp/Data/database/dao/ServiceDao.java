package com.example.luxevistaresortapp.Data.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.luxevistaresortapp.Data.database.Model.Service;

import java.util.List;

@Dao
public interface ServiceDao {
    @Insert
    void insert(Service service);

    @Query("SELECT * FROM services")
    List<Service> getAllServices();

    @Query("UPDATE services SET price = :price WHERE id = :id")
    void updateServicePrice(int id, int price);

    @Query("DELETE FROM services WHERE id = :id")
    void deleteService(int id);
}