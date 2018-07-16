package com.example.android.baking;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.utils.NetworkConstants;
import com.example.android.baking.utils.NetworkUtils;
import com.example.android.baking.utils.RecipeJsonUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadRecipes();
    }

    private void DownloadRecipes() {

        String buildURLString = NetworkConstants.RECIPE_SCHEME + NetworkConstants.RECIPE_HOST + NetworkConstants.RECIPE_PATH;
        new RequestRecipes().execute(buildURLString);
    }

    public class RequestRecipes extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(String... param) {
            if (param.length == 0)
                return null;

            try {
                URL url = new URL(param[0]);

                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

                System.out.println(jsonResponse);

                return RecipeJsonUtils.getRecipeFromJson(MainActivity.this, jsonResponse);
            } catch (Exception e) {
                return null;
            }


        }
    }
}
