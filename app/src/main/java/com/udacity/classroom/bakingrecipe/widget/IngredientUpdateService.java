package com.udacity.classroom.bakingrecipe.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

public class IngredientUpdateService extends IntentService {

    private static final String ACTION_UPDATE_INGREDIENT_WIDGETS = "update_ingredient_widgets";

    public IngredientUpdateService() {
        super("IngredientUpdateService");
    }

    public static void startActionUpdateIngredientWidgets(Context context) {
        Intent intent = new Intent(context, IngredientUpdateService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENT_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENT_WIDGETS.equals(action)) {
                handleActionUpdateIngredientWidgets();
            }
        }
    }

    private void handleActionUpdateIngredientWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientWidgetProvider.class));
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.linearLayout_ingredient);
        IngredientWidgetProvider.updateIngredientWidgets(this, appWidgetManager, appWidgetIds);
    }
}
