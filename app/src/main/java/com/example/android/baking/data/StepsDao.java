package com.example.android.baking.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.baking.model.Step;

import java.util.List;

@Dao
public interface StepsDao {

    @Query("SELECT * FROM steps")
    List<Step> getAllSteps();

    @Query("SELECT * FROM steps WHERE recipe_id = :recipeID ")
    List<Step> retriveSteps(int recipeID);

    @Insert
    void insertStep(Step step);
}
