package com.udacity.classroom.bakingrecipe.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;
import com.udacity.classroom.bakingrecipe.database.Ingredient;

import java.util.Locale;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private AppDatabase mDb;
    private Context mContext;
    private int mAppWidgetId;
    private int mRecipeId;
    private BakingRecipeEntry mRecipe;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
    }

    @Override
    public void onCreate() {
        mDb = AppDatabase.getInstance(mContext);
        mRecipeId = IngredientWidgetConfigure.loadRecipeIdPref(mContext, mAppWidgetId);
    }

    @Override
    public void onDataSetChanged() {
        mRecipe = mDb.bakingRecipeDao().nLoadRecipeById(mRecipeId);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mRecipe == null) {
            return 0;
        }
        return mRecipe.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_ingredient_widget);
        Ingredient ingredient = mRecipe.getIngredients().get(position);
        String builder = String.format(Locale.getDefault(), "• %s (%s %s)",
                ingredient.getIngredient(),
                String.valueOf(ingredient.getQuantity()).replaceAll("\\.?0*$", ""),
                ingredient.getMeasure());

        views.setTextViewText(R.id.text_ingredient_widget, builder);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
