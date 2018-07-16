package com.example.android.baking.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "ingredients")
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recipe_id")
    private int RecipeId;

    @ColumnInfo(name = "quantity")
    private String Quantity;

    @ColumnInfo(name = "measure")
    private String Measure;

    @ColumnInfo(name = "name")
    private String Name;

    public Ingredient(int id, int RecipeId, String Quantity, String Measure, String Name) {
        this.id = id;
        this.RecipeId = RecipeId;
        this.Quantity = Quantity;
        this.Measure = Measure;
        this.Name = Name;
    }

    @Ignore
    public Ingredient(int RecipeId, String Quantity, String Measure, String Name) {
        this.RecipeId = RecipeId;
        this.Quantity = Quantity;
        this.Measure = Measure;
        this.Name = Name;
    }


    public int getId() { return id; }
    public int getRecipeId() { return RecipeId; }
    public String getQuantity() { return Quantity; }
    public String getMeasure() { return Measure; }
    public String getName() { return Name; }

    public void setId(int id) { this.id = id; }
    public void setRecipeId(int recipeId) { this.RecipeId = recipeId; }
    public void setQuantity(String quantity) { this.Quantity = quantity; }
    public void setMeasure(String measure) { this.Measure = measure; }
    public void setName(String name) { this.Name = name; }

}
