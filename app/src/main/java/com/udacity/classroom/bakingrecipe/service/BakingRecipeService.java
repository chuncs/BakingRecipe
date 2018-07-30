package com.udacity.classroom.bakingrecipe.service;

import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingRecipeService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<BakingRecipeEntry>> getBakingRecipes();

}
