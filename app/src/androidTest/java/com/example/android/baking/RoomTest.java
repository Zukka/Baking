package com.example.android.baking;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.baking.data.AppDatabase;
import com.example.android.baking.data.IngredientsDao;
import com.example.android.baking.data.StepsDao;
import com.example.android.baking.model.Step;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(AndroidJUnit4.class)
public class RoomTest {
    private StepsDao mStepsDao;
    private AppDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mStepsDao = mDb.stepsDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeStepAndCheckListSize() throws Exception {
        Random random = new Random();
        int  randomIndex = random.nextInt(50) + 1;
        for (int index = 0; index < randomIndex; index++) {
            Step step = new Step(index, "StepID" + index, "shortDesc", "Desc", "Video", "-");
            mStepsDao.insertStep(step);
        }
        List<Step> stepRetrived = mStepsDao.getAllSteps();
        assertThat(stepRetrived.size(), equalTo(mStepsDao.getAllSteps().size()));
    }

    @Test
    public void writeStepAndReadInList() throws Exception {
        Step step = new Step(1, "MyFirstStepID", "shortDesc", "Desc", "Video", "-");
        mStepsDao.insertStep(step);
        List<Step> stepRetrived = mStepsDao.getAllSteps();
        assertThat(stepRetrived.get(0).getStepId(), equalTo("MyFirstStepID"));
    }
}
