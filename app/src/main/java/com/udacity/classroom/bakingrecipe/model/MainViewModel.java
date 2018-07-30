package com.udacity.classroom.bakingrecipe.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<BakingRecipeEntry>> recipes;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(getApplication());
        recipes = database.bakingRecipeDao().loadAllRecipes();
    }

    public LiveData<List<BakingRecipeEntry>> getRecipes() {
        return recipes;
    }
}
