package com.udacity.classroom.bakingrecipe.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.udacity.classroom.bakingrecipe.R;

public class DetailActivity extends AppCompatActivity implements
        MasterListFragment.OnStepClickListener, StepFragment.OnButtonClickListener{

    public static final String EXTRA_RECIPE_ID = "extraRecipeId";
    public static final String EXTRA_NAME = "extraName";

    private int mRecipeId;
    private String mRecipeName;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            Toast.makeText(this, "Recipe is not available.", Toast.LENGTH_SHORT).show();
        } else {
            mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 0);
            mRecipeName = intent.getStringExtra(EXTRA_NAME);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mRecipeName);
            }
        }

        if (findViewById(R.id.step_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                StepFragment stepFragment = new StepFragment();
                stepFragment.setRecipeId(mRecipeId);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.step_container, stepFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onStepClick(int position, int recipeId) {
        if (mTwoPane) {
            StepFragment stepFragment = new StepFragment();
            stepFragment.setRecipeId(mRecipeId);
            stepFragment.setPosition(position);
            stepFragment.isTwoPane(mTwoPane);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_container, stepFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(EXTRA_RECIPE_ID, recipeId);
            intent.putExtra(StepDetailActivity.EXTRA_POSITION, position);
            intent.putExtra(EXTRA_NAME, mRecipeName);
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClicked(int position) {

    }
}
