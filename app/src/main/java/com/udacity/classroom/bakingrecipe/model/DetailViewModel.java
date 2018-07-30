package com.udacity.classroom.bakingrecipe.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;

public class DetailViewModel extends ViewModel {

    private LiveData<BakingRecipeEntry> recipe;

    public DetailViewModel(AppDatabase database, int recipeId) {
        recipe = database.bakingRecipeDao().loadRecipeById(recipeId);
    }

    public LiveData<BakingRecipeEntry> getRecipe() {
        return recipe;
    }
}
