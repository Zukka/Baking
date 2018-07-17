package com.example.android.baking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Step;
import com.example.android.baking.utils.RecipeJsonConstants;

import java.util.Iterator;
import java.util.List;

public class StepDetailActivity extends AppCompatActivity {

    private int stepID;
    private int recipeID;
    private AppDatabase mDb;
    private TextView description;
    private Button mButtonNext;
    private Button mButtonPrev;
    private List<Step> stepsLoaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState == null) {
            Intent detailsIntent = getIntent();
            recipeID = detailsIntent.getIntExtra(RecipeJsonConstants.RECIPE, 0);
            stepID = detailsIntent.getIntExtra("selectedStep", 0);
        } else {
            recipeID = savedInstanceState.getInt(RecipeJsonConstants.RECIPE);
            stepID = savedInstanceState.getInt("selectedStep");
        }
        description = findViewById(R.id.instructionText);

        mButtonNext = findViewById(R.id.buttonNext);
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stepID += 1;
                UpdateUI(stepsLoaded);
            }
        });
        mButtonPrev = findViewById(R.id.buttonPrevious);
        mButtonPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stepID -= 1;
                UpdateUI(stepsLoaded);
            }
        });
        new LoadStep().execute(recipeID);
    }

    private void UpdateUI(List<Step> steps) {
        mButtonPrev.setEnabled(true);
        mButtonNext.setEnabled(true);
        for (Step step: steps) {
            if (step.getStepId().equals(String.valueOf(stepID))) {
                description.setText(step.getDescription());

                if (Integer.parseInt(step.getStepId()) == 1)
                    mButtonPrev.setEnabled(false);
                if (Integer.parseInt(step.getStepId()) == steps.size())
                    mButtonNext.setEnabled(false);

                return;
            }
        }
        description.setText(String.valueOf(stepID) + " - " + getString(R.string.missedInstruction));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RecipeJsonConstants.RECIPE, recipeID);
        outState.putInt("selectedStep", stepID);
    }

    public class LoadStep extends AsyncTask<Integer, Void, List<Step>> {

        @Override
        protected List<Step> doInBackground(Integer... params) {
            int recipeID = params[0];
            List<Step> stepsData;
            stepsData = mDb.stepsDao().retrieveSteps(recipeID);

            return stepsData;
        }

        @Override
        protected void onPostExecute(List<Step> steps) {
            super.onPostExecute(steps);
            if (steps != null) {
                stepsLoaded = steps;
                UpdateUI(steps);
            } else {
                Toast.makeText(StepDetailActivity.this, getString(R.string.queryFailed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
