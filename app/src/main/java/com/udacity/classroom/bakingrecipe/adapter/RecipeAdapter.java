package com.udacity.classroom.bakingrecipe.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.classroom.bakingrecipe.BR;
import com.udacity.classroom.bakingrecipe.R;
import com.udacity.classroom.bakingrecipe.database.BakingRecipeEntry;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final ItemClickListener mItemClickListener;

    private List<BakingRecipeEntry> mRecipeEntries;
    private Context mContext;

    public RecipeAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_baking_recipe, parent, false);

        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.binding.setVariable(BR.recipe, mRecipeEntries.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (mRecipeEntries == null) {
            return 0;
        }
        return mRecipeEntries.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ViewDataBinding binding;
        public RecipeViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemId = mRecipeEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(itemId, getAdapterPosition());
        }

    }

    public void setRecipes(List<BakingRecipeEntry> recipeEntries) {
        mRecipeEntries = recipeEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId, int position);
    }
}
