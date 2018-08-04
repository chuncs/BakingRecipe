package com.udacity.classroom.bakingrecipe.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.udacity.classroom.bakingrecipe.IdlingResource.RecipeDownloadDelayer;
import com.udacity.classroom.bakingrecipe.IdlingResource.RecipeIdlingResource;
import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.adapter.RecipeAdapter;
import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;
import com.udacity.classroom.bakingrecipe.databinding.ActivityMainBinding;
import com.udacity.classroom.bakingrecipe.model.MainViewModel;
import com.udacity.classroom.bakingrecipe.service.BakingRecipeService;
import com.udacity.classroom.bakingrecipe.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener,
        RecipeDownloadDelayer.DelayerCallback {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    private static final String IDLING_RESOURCE_TESTING = "idlingResourceTesting";

    private AppDatabase mDb;
    private ActivityMainBinding mBinding;
    private RecipeAdapter mRecipeAdapter;
    private List<BakingRecipeEntry> mRecipeEntries;

    @Nullable
    private RecipeIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mBinding.recyclerViewRecipe.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mBinding.recyclerViewRecipe.setLayoutManager(new LinearLayoutManager(this));
        }
        mRecipeAdapter = new RecipeAdapter(this, this);
        mBinding.recyclerViewRecipe.setAdapter(mRecipeAdapter);

        mDb = AppDatabase.getInstance(getApplicationContext());
        mRecipeEntries = new ArrayList<>();

        requestJson();

        setupViewModel();

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        mBinding.recyclerViewRecipe.addItemDecoration(decoration);

        getIdlingResource();
    }

    private void requestJson() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BakingRecipeService service = retrofit.create(BakingRecipeService.class);

        Call<List<BakingRecipeEntry>> call = service.getBakingRecipes();

        call.enqueue(new Callback<List<BakingRecipeEntry>>() {
            @Override
            public void onResponse(Call<List<BakingRecipeEntry>> call, Response<List<BakingRecipeEntry>> response) {
                final List<BakingRecipeEntry> recipes = response.body();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (BakingRecipeEntry recipe : recipes) {
                            mDb.bakingRecipeDao().insertRecipe(recipe);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<BakingRecipeEntry>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<BakingRecipeEntry>>() {
            @Override
            public void onChanged(@Nullable List<BakingRecipeEntry> recipeEntries) {
                mRecipeEntries = recipeEntries;
                mRecipeAdapter.setRecipes(recipeEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId, int position) {
        RecipeDownloadDelayer.downloadRecipe(mRecipeEntries.get(position).getName(),
                this, mIdlingResource);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, itemId);
        intent.putExtra(DetailActivity.EXTRA_NAME, mRecipeEntries.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onDone(String text) {
        Log.d(IDLING_RESOURCE_TESTING, "Recipe " + text + " downloaded.");
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new RecipeIdlingResource();
        }
        return mIdlingResource;
    }
}
