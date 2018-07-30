package com.udacity.classroom.bakingrecipe.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.database.Ingredient;
import com.udacity.classroom.bakingrecipe.database.Step;

import java.util.List;
import java.util.Locale;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.RecipeDetailViewHolder> {

    private static final int VIEW_TYPE_INGREDIENT = 0;
    private static final int VIEW_TYPE_STEP = 1;
    private final ItemClickListener mItemClickListener;

    private Context mContext;
    private List<Ingredient> mIngredients;
    private List<Step> mSteps;

    public RecipeDetailAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_INGREDIENT:
                layoutId = R.layout.item_ingredient;
                break;

            case VIEW_TYPE_STEP:
                layoutId = R.layout.item_step;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        view.setFocusable(true);

        return new RecipeDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_TYPE_INGREDIENT:
                StringBuilder builder = new StringBuilder();
                for (Ingredient ingredient : mIngredients) {
                    builder.append(String.format(Locale.getDefault(), "• %s (%s %s)",
                            ingredient.getIngredient(),
                            String.valueOf(ingredient.getQuantity()).replaceAll("\\.?0*$", ""),
                            ingredient.getMeasure()));
                    builder.append(System.lineSeparator());
                }
                holder.getBinding().setVariable(BR.ingredient, builder.toString());
                holder.getBinding().executePendingBindings();
                break;

            case VIEW_TYPE_STEP:
                holder.getBinding().setVariable(BR.step, mSteps.get(position - 1));
                holder.getBinding().executePendingBindings();
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public int getItemCount() {
        return getIngredientSize() + getStepSize();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_INGREDIENT;
        } else {
            return VIEW_TYPE_STEP;
        }
    }

    private int getIngredientSize() {
        if (mIngredients == null) {
            return 0;
        } else {
            return 1;
        }
    }

    private int getStepSize() {
        if (mSteps == null) {
            return 0;
        } else {
            return mSteps.size();
        }
    }

    public class RecipeDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ViewDataBinding binding;

        public RecipeDetailViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.getRoot().setOnClickListener(this);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != 0) {
                int position = getAdapterPosition() - 1;
                mItemClickListener.onItemClickListener(position);
            }
        }
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    public void setSteps(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int position);
    }
}
