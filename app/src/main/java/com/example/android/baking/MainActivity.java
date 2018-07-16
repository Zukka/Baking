package com.example.android.baking;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.baking.model.Recipe;
import com.example.android.baking.utils.NetworkConstants;
import com.example.android.baking.utils.NetworkUtils;
import com.example.android.baking.utils.RecipeJsonUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingDataProgressBar;
    private RecyclerView recipeReciclerView;
    private LinearLayoutManager mLayoutManager;
    private RecipeRecycleViewAdapter recipeRecycleViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDataProgressBar = findViewById(R.id.progressBar);
        recipeReciclerView = findViewById(R.id.home_recycled_view);
        recipeReciclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recipeReciclerView.setLayoutManager(mLayoutManager);
        recipeReciclerView.setItemAnimator(new DefaultItemAnimator());

        DownloadRecipes();

        recipeRecycleViewAdapter = new RecipeRecycleViewAdapter(this);
        recipeReciclerView.setAdapter(recipeRecycleViewAdapter);
    }

    private void DownloadRecipes() {

        String buildURLString = NetworkConstants.RECIPE_SCHEME + NetworkConstants.RECIPE_HOST + NetworkConstants.RECIPE_PATH;
        new RequestRecipes().execute(buildURLString);
    }

    private void isProgressBarVisible(boolean isVisible) {
        if (isVisible) {
            loadingDataProgressBar.setVisibility(View.VISIBLE);
        } else {
            loadingDataProgressBar.setVisibility((View.GONE));
        }
    }

    public class RequestRecipes extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            isProgressBarVisible(true);
        }

        @Override
        protected List<Recipe> doInBackground(String... param) {
            if (param.length == 0)
                return null;

            try {
                URL url = new URL(param[0]);

                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

                return RecipeJsonUtils.getRecipeFromJson(MainActivity.this, jsonResponse);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Recipe> recipeData) {
            super.onPostExecute(recipeData);

            isProgressBarVisible(false);

            if (recipeData != null && recipeData.size() > 0) {
                recipeReciclerView.setVisibility(View.VISIBLE);
                recipeRecycleViewAdapter.setRecipeData(recipeData);
                recipeReciclerView.setAdapter(recipeRecycleViewAdapter);
            } else {
                    Toast.makeText(MainActivity.this, getString(R.string.loadRecipesFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
