package com.example.android.baking.utils;

import android.content.Context;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeJsonUtils {

    public static List<Recipe> getRecipeFromJson(Context context, String jsonResponse) throws JSONException {

        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        JSONArray recipeJson = new JSONArray(jsonResponse);
        JSONArray recipeSteps;
        JSONObject ingredientObject;
        JSONObject stepObject;

        List<Recipe> parsedRecipeData = new ArrayList<>();

        mDb.stepsDao().nukeTable();
        mDb.ingredientsDao().nukeTable();
        for (int index = 0; index < recipeJson.length(); index++) {
            JSONObject recipeObject = recipeJson.getJSONObject(index);
            int mRecipeId =(int)recipeObject.get(RecipeJsonConstants.RECIPE_ID);
            Recipe recipe = new Recipe(
                    (int)recipeObject.get(RecipeJsonConstants.RECIPE_ID),
                    recipeObject.get(RecipeJsonConstants.RECIPE_NAME).toString(),
                    recipeObject.get(RecipeJsonConstants.RECIPE_SERVING).toString(),
                    recipeObject.get(RecipeJsonConstants.RECIPE_IMAGE).toString());
            parsedRecipeData.add(recipe);

            JSONArray recipeIngredients = recipeObject.getJSONArray(RecipeJsonConstants.RECIPE_INGREDIENTS);
            for (int ingredientsIndex = 0; ingredientsIndex < recipeIngredients.length(); ingredientsIndex++) {
                ingredientObject = recipeIngredients.getJSONObject(ingredientsIndex);
                Ingredient ingredient = new Ingredient(
                        mRecipeId,
                        ingredientObject.get(RecipeJsonConstants.INGREDIENT_QUNATITY).toString(),
                        ingredientObject.get(RecipeJsonConstants.INGREDIENT_MEASURE).toString(),
                        ingredientObject.get(RecipeJsonConstants.INGREDIENT_NAME).toString()
                );
                mDb.ingredientsDao().insertIngredient(ingredient);
            }
            recipeSteps = recipeObject.getJSONArray(RecipeJsonConstants.RECIPE_STEPS);
            for (int stepIndex = 0; stepIndex < recipeSteps.length(); stepIndex++) {
               stepObject = recipeSteps.getJSONObject(stepIndex);
               Step step = new Step(
                        mRecipeId,
                        stepObject.get(RecipeJsonConstants.STEP_ID).toString(),
                        stepObject.get(RecipeJsonConstants.STEP_SHORT_DESCRIPTION).toString(),
                        stepObject.get(RecipeJsonConstants.STEP_DESCRIPTION).toString(),
                        stepObject.get(RecipeJsonConstants.STEP_VIDEO_URL).toString(),
                        stepObject.get(RecipeJsonConstants.STEP_THUMBNAIL_URL).toString()
                );
                mDb.stepsDao().insertStep(step);
            }
        }
        return parsedRecipeData;
    }

    private void RecipeSteps() {

    }
}
