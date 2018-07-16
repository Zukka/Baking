package com.example.android.baking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.model.Ingredient;

import java.util.List;

public class IngredientRecycleViewAdapter extends RecyclerView.Adapter<IngredientRecycleViewAdapter.IngredientsViewHolder> {

    private List<Ingredient> mIngredientData;
    private Context mContext;

    public IngredientRecycleViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName;
        IngredientsViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_name);
        }
    }


    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredients_card, parent, false);

        IngredientRecycleViewAdapter.IngredientsViewHolder vh = new IngredientRecycleViewAdapter.IngredientsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(IngredientRecycleViewAdapter.IngredientsViewHolder holder, int position) {
        final Ingredient ingredient = mIngredientData.get(position);

        holder.ingredientName.setText(ingredient.getName());
    }

    @Override
    public int getItemCount() {
        if (null == mIngredientData) return 0;
        return mIngredientData.size();
    }

    public void setIngredientData(List<Ingredient> ingredientData) {
        mIngredientData = ingredientData;
        notifyDataSetChanged();
    }

}
