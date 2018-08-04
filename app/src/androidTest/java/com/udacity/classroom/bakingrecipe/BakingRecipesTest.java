package com.udacity.classroom.bakingrecipe;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.classroom.bakingrecipe.ui.DetailActivity;
import com.udacity.classroom.bakingrecipe.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class BakingRecipesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecyclerViewItem_OpenDetailActivity() {
        onView(withId(R.id.recyclerViewRecipe))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.text_ingredient)).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerViewDetail)).
                perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        onView(withId(R.id.playerView)).check(matches(isDisplayed()));

    }

    @Test
    public void clickRecyclerViewItem_HasIntentWithKey() {
        Intents.init();

        onView(withId(R.id.recyclerViewRecipe))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        intended(hasExtraWithKey(DetailActivity.EXTRA_RECIPE_ID));
        intended(hasExtraWithKey(DetailActivity.EXTRA_NAME));

        Intents.release();
    }
}
