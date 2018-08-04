package com.udacity.classroom.bakingrecipe.IdlingResource;

import android.os.Handler;
import android.support.annotation.Nullable;

public class RecipeDownloadDelayer {

    private static final int DELAY_MILLIS = 3000;

    public interface DelayerCallback {
        void onDone(String text);
    }

    public static void downloadRecipe(final String message, final DelayerCallback callback,
                               @Nullable final RecipeIdlingResource idlingResource) {
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onDone(message);
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }
        }, DELAY_MILLIS);
    }
}
