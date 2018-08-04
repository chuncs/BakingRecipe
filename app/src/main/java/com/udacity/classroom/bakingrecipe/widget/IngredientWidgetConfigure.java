package com.udacity.classroom.bakingrecipe.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.adapter.RecipeAdapter;
import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;
import com.udacity.classroom.bakingrecipe.databinding.ActivityIngredientWidgetConfigureBinding;
import com.udacity.classroom.bakingrecipe.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class IngredientWidgetConfigure extends AppCompatActivity implements RecipeAdapter.ItemClickListener {

    private static final String PREFS_ID
            = "com.udacity.classroom.bakingrecipe.widget.IngredientWidgetProvider.id";
    private static final String PREFS_NAME
            = "com.udacity.classroom.bakingrecipe.widget.IngredientWidgetProvider.name";
    private static final String PREF_PREFIX_KEY = "prefix_";

    private AppDatabase mDb;
    private ActivityIngredientWidgetConfigureBinding mWidgetBinding;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private List<BakingRecipeEntry> mRecipeEntries;
    private RecipeAdapter mRecipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        mWidgetBinding = DataBindingUtil.setContentView(this, R.layout.activity_ingredient_widget_configure);

        mWidgetBinding.recyclerViewWidget.setLayoutManager(new LinearLayoutManager(this));
        mRecipeAdapter = new RecipeAdapter(this, this);
        mWidgetBinding.recyclerViewWidget.setAdapter(mRecipeAdapter);

        mDb = AppDatabase.getInstance(this);
        mRecipeEntries = new ArrayList<>();

        initialize();

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        mWidgetBinding.recyclerViewWidget.addItemDecoration(decoration);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.configure_title));
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onItemClickListener(int itemId, int position) {
        final Context context = IngredientWidgetConfigure.this;
        String recipeName = mRecipeEntries.get(position).getName();

        saveRecipeIdPref(context, mAppWidgetId, itemId, recipeName);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        IngredientWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId, itemId, recipeName);

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initialize() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRecipeEntries = mDb.bakingRecipeDao().nLoadAllRecipes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecipeAdapter.setRecipes(mRecipeEntries);
                    }
                });
            }
        });
    }

    public static void saveRecipeIdPref(Context context, int appWidgetId, int recipeId, String recipeName) {
        SharedPreferences.Editor prefsId = context.getSharedPreferences(PREFS_ID, 0).edit();
        prefsId.putInt(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefsId.apply();

        SharedPreferences.Editor prefsName = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefsName.putString(PREF_PREFIX_KEY + appWidgetId, recipeName);
        prefsName.apply();
    }

    public static int loadRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_ID, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
    }

    public static String loadRecipeNamePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId, "");
    }
}
