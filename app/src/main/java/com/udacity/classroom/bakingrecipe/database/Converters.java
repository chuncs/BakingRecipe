package com.udacity.classroom.bakingrecipe.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    @TypeConverter
    public static List<Ingredient> fromIngredientJson(String json) {
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();

        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String fromIngredientList(List<Ingredient> ingredient) {
        return new Gson().toJson(ingredient);
    }

    @TypeConverter
    public static List<Step> fromStepJson(String json) {
        Type listType = new TypeToken<List<Step>>() {}.getType();

        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String fromStepList(List<Step> step) {
        return new Gson().toJson(step);
    }
}
