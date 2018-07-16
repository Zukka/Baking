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

    @Insert
    void insertStep(Step step);
}
