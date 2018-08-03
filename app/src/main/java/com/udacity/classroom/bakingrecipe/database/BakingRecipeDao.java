package com.udacity.classroom.bakingrecipe.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface BakingRecipeDao {

    @Query("SELECT * FROM baking_recipe")
    LiveData<List<BakingRecipeEntry>> loadAllRecipes();

    @Query("SELECT * FROM baking_recipe")
    List<BakingRecipeEntry> nLoadAllRecipes();

    @Query("SELECT * FROM baking_recipe WHERE id = :id")
    LiveData<BakingRecipeEntry> loadRecipeById(int id);

    @Query("SELECT * FROM baking_recipe WHERE id = :id")
    BakingRecipeEntry nLoadRecipeById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(BakingRecipeEntry recipeEntry);

    @Update
    void updateRecipe(BakingRecipeEntry recipeEntry);

    @Delete
    void deleteRecipe(BakingRecipeEntry recipeEntry);
}
