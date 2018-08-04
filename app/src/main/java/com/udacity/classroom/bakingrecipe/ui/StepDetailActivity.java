package com.udacity.classroom.bakingrecipe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.udacity.classroom.bakingrecipe.R;

public class StepDetailActivity extends AppCompatActivity implements StepFragment.OnButtonClickListener {

    public static final String EXTRA_POSITION = "extraPosition";

    private int mRecipeId;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            Toast.makeText(this, "Step is not available.", Toast.LENGTH_SHORT).show();
        } else {
            String recipeName = intent.getStringExtra(DetailActivity.EXTRA_NAME);
            mRecipeId = intent.getIntExtra(DetailActivity.EXTRA_RECIPE_ID, 0);
            mPosition = intent.getIntExtra(EXTRA_POSITION, 0);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }
        }

        if (savedInstanceState == null) initializeFragment();

    }

    @Override
    public void onButtonClicked(int position) {
        StepFragment stepFragment = new StepFragment();
        stepFragment.setRecipeId(mRecipeId);
        stepFragment.setPosition(position);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_container, stepFragment)
                .commit();
    }

    private void initializeFragment() {
        StepFragment stepFragment = new StepFragment();
        stepFragment.setRecipeId(mRecipeId);
        stepFragment.setPosition(mPosition);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.step_container, stepFragment)
                .commit();
    }
}
