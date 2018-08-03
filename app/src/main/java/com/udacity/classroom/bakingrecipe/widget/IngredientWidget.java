package com.udacity.classroom.bakingrecipe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.ui.DetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(DetailActivity.EXTRA_NAME, recipeName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.linearLayout_ingredient, pendingIntent);

        views.setTextViewText(R.id.text_recipe_name, recipeName);

        Intent idIntent = new Intent(context, IngredientWidgetService.class);
        idIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent updatePendingIntent = PendingIntent.getService(context, 0, idIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setRemoteAdapter(R.id.listView_ingredient, idIntent);
        views.setPendingIntentTemplate(R.id.listView_ingredient, updatePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateIngredientWidgets(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            int recipeId = IngredientWidgetConfigure.loadRecipeIdPref(context, appWidgetId);
            String recipeName = IngredientWidgetConfigure.loadRecipeNamePref(context, appWidgetId);

            updateAppWidget(context, appWidgetManager, appWidgetId, recipeId, recipeName);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        IngredientUpdateService.startActionUpdateIngredientWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

