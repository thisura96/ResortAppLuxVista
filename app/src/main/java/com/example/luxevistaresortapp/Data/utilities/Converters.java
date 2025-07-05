package com.example.luxevistaresortapp.Data.utilities;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;
public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        return value != null ? Arrays.asList(value.split(",")) : null;
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return list != null ? String.join(",", list) : null;
    }
}