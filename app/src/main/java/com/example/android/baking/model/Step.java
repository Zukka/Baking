package com.example.android.baking.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

@Entity (tableName = "steps")
public class Step {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recipe_id")
    private int RecipeId;

    @ColumnInfo(name = "step_id")
    private String StepId;

    @ColumnInfo(name = "recipe_short_desc")
    private String ShortDescription;

    @ColumnInfo(name = "recipe_desc")
    private String Description;

    @ColumnInfo(name = "recipe_video_url")
    private String VideoURL;

    @ColumnInfo(name = "recipe_thumbnail_url")
    private String ThumbnailURL;

    public Step(int id, int RecipeId, String StepId, String ShortDescription, String Description, String VideoURL, String ThumbnailURL) {
        this.id = id;
        this.RecipeId = RecipeId;
        this.StepId = StepId;
        this.ShortDescription = ShortDescription;
        this.Description = Description;
        this.VideoURL = VideoURL;
        this.ThumbnailURL = ThumbnailURL;
    }

    @Ignore
    public Step(int RecipeId, String StepId, String ShortDescription, String Description, String VideoURL, String ThumbnailURL) {
        this.RecipeId = RecipeId;
        this.StepId = StepId;
        this.ShortDescription = ShortDescription;
        this.Description = Description;
        this.VideoURL = VideoURL;
        this.ThumbnailURL = ThumbnailURL;
    }

    public int getId() { return id; }
    public int getRecipeId() { return RecipeId; }
    public String getStepId() { return StepId; }
    public String getShortDescription() { return ShortDescription; }
    public String getDescription() { return Description; }
    public String getVideoURL() { return VideoURL; }
    public String getThumbnailURL() { return ThumbnailURL; }

    public void setId(int id) { this.id = id; }
    public void setRecipeId(int recipeId) { this.RecipeId = recipeId; }
    public void setStepId(String stepId) { this.StepId = stepId; }
    public void setShortDescription(String shortDescription) { this.ShortDescription = shortDescription; }
    public void setDescription(String description) { this.Description = description; }
    public void setVideoURL(String videoURL) { this.VideoURL = videoURL; }
    public void setThumbnailURL(String thumbnailURL) { this.ThumbnailURL = thumbnailURL; }

}
