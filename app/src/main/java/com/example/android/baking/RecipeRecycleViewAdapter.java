package com.example.android.baking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.model.Recipe;

import java.util.List;

public class RecipeRecycleViewAdapter extends RecyclerView.Adapter<RecipeRecycleViewAdapter.RecipesViewHolder> {

    private List<Recipe> mRecipeData;
    private Context mContext;

    public RecipeRecycleViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;
        RecipesViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
        }
    }

    @Override
    public RecipesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);

        RecipesViewHolder vh = new RecipesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecipeRecycleViewAdapter.RecipesViewHolder holder, int position) {
        final Recipe recipe = mRecipeData.get(position);

        holder.recipeName.setText(recipe.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShowDetails = new Intent(mContext, RecipeDetailsActivity.class);
                intentShowDetails.putExtra("recipe", recipe);
                mContext.startActivity(intentShowDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.size();
    }

    public void setRecipeData(List<Recipe> recipeData) {
        mRecipeData = recipeData;
        notifyDataSetChanged();
    }
}
