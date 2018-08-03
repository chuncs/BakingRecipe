package com.udacity.classroom.bakingrecipe.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class IngredientWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(), intent);
    }
}
