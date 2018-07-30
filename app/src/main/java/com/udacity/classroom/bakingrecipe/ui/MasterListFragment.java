package com.udacity.classroom.bakingrecipe.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.adapter.RecipeDetailAdapter;
import com.udacity.classroom.bakingrecipe.database.AppDatabase;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;
import com.udacity.classroom.bakingrecipe.database.Ingredient;
import com.udacity.classroom.bakingrecipe.database.Step;
import com.udacity.classroom.bakingrecipe.model.DetailViewModel;
import com.udacity.classroom.bakingrecipe.model.DetailViewModelFactory;

import java.util.List;

public class MasterListFragment extends Fragment implements RecipeDetailAdapter.ItemClickListener{

    private RecipeDetailAdapter mDetailAdapter;
    private AppDatabase mDb;
    private int mRecipeId;
    OnStepClickListener mCallback;

    public MasterListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mRecipeId = intent.getIntExtra(DetailActivity.EXTRA_RECIPE_ID, 0);
        mDb = AppDatabase.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDetailAdapter = new RecipeDetailAdapter(getContext(), this);
        recyclerView.setAdapter(mDetailAdapter);

        setupViewModel();

        return rootView;
    }

    private void setupViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(mDb, mRecipeId);
        final DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        viewModel.getRecipe().observe(this, new Observer<BakingRecipeEntry>() {
            @Override
            public void onChanged(@Nullable BakingRecipeEntry recipeEntry) {
                List<Ingredient> ingredients = recipeEntry.getIngredients();
                List<Step> steps = recipeEntry.getSteps();
                mDetailAdapter.setIngredients(ingredients);
                mDetailAdapter.setSteps(steps);
            }
        });
    }

    @Override
    public void onItemClickListener(int position) {
        mCallback.onStepClick(position, mRecipeId);
    }

    public interface OnStepClickListener {
        void onStepClick(int position, int recipeId);
    }

}
