package com.example.android.baking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.model.Step;
import com.example.android.baking.utils.RecipeJsonConstants;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    Recipe recipe;
    private RecyclerView stepsReciclerView;
    private LinearLayoutManager stepLayoutManager;
    private StepRecycleViewAdapter stepRecycleViewAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState == null) {
            Intent detailsIntent = getIntent();
            recipe = detailsIntent.getParcelableExtra("recipe");
        } else {
            recipe = savedInstanceState.getParcelable(RecipeJsonConstants.RECIPE);
        }

        stepsReciclerView = findViewById(R.id.steps_recycled_view);
        stepsReciclerView.setHasFixedSize(true);
        stepLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stepsReciclerView.setLayoutManager(stepLayoutManager);
        stepsReciclerView.setItemAnimator(new DefaultItemAnimator());

        new RequestSteps().execute(recipe.getId());

        stepRecycleViewAdapter = new StepRecycleViewAdapter(this);
        stepsReciclerView.setAdapter(stepRecycleViewAdapter);
    }

    public class RequestSteps extends AsyncTask<Integer, Void, List<Step>> {

        @Override
        protected List<Step> doInBackground(Integer... params) {
            int recipeID = params[0];

            List<Step> stepsData = mDb.stepsDao().retriveSteps(recipeID);
            System.out.println("STEP DATA: "+ stepsData.size());
            return stepsData;
        }

        @Override
        protected void onPostExecute(List<Step> steps) {
            super.onPostExecute(steps);

            if (steps != null && steps.size() > 0) {
                stepRecycleViewAdapter.setStepsData(steps);
                stepsReciclerView.setAdapter(stepRecycleViewAdapter);
            } else {
                Toast.makeText(RecipeDetailsActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
