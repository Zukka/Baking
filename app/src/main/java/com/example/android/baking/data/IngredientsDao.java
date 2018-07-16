package com.example.android.baking.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Step;

import java.util.List;

@Dao
public interface IngredientsDao {

    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredients();

    @Insert
    void insertIngredient(Ingredient ingredient);
}
