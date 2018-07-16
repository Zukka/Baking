package com.example.android.baking;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.utils.RecipeJsonConstants;

import java.util.List;

public class IngredientsActivity extends AppCompatActivity {

    Recipe recipe;
    private RecyclerView ingredientReciclerView;
    private LinearLayoutManager ingredientLayoutManager;
    private IngredientRecycleViewAdapter ingredientRecycleViewAdapter;

    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState == null) {
            Intent detailsIntent = getIntent();
            recipe = detailsIntent.getParcelableExtra("recipe");
        } else {
            recipe = savedInstanceState.getParcelable(RecipeJsonConstants.RECIPE);
        }

        Toolbar ingredientsToolbar = findViewById(R.id.ingredients_toolbar);
        ingredientsToolbar.setTitle(getString(R.string.ingredients));
        setSupportActionBar(ingredientsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ingredientReciclerView = findViewById(R.id.ingredients_recycled_view);
        ingredientReciclerView.setHasFixedSize(true);
        ingredientLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ingredientReciclerView.setLayoutManager(ingredientLayoutManager);
        ingredientReciclerView.setItemAnimator(new DefaultItemAnimator());

        new RequestIngredients().execute(recipe.getId());

        ingredientRecycleViewAdapter = new IngredientRecycleViewAdapter(this);
        ingredientReciclerView.setAdapter(ingredientRecycleViewAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RecipeJsonConstants.RECIPE, recipe);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class RequestIngredients extends AsyncTask<Integer, Void, List<Ingredient>> {

        @Override
        protected List<Ingredient> doInBackground(Integer... params) {
            int recipeID = params[0];
            List<Ingredient> ingredientData;
            ingredientData = mDb.ingredientsDao().retriveIngredients(recipeID);

            return ingredientData;
        }

        @Override
        protected void onPostExecute(List<Ingredient> ingredients) {
            super.onPostExecute(ingredients);

            if (ingredients != null && ingredients.size() > 0) {
                ingredientRecycleViewAdapter.setIngredientData(ingredients);
                ingredientReciclerView.setAdapter(ingredientRecycleViewAdapter);
            } else {
                Toast.makeText(IngredientsActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
