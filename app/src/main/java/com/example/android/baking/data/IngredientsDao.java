package com.example.android.baking.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Step;

import java.util.List;

@Dao
public interface IngredientsDao {

    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE recipe_id = :recipeID")
    List<Ingredient> retriveIngredients(int recipeID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredients")
    void nukeTable();
}
