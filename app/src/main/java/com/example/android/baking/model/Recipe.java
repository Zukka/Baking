package com.example.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    private int Id;
    private String Title;
    private String Servings;
    private String Image;

    public Recipe(int Id, String Title, String Servings, String Image) {
        this.Id = Id;
        this.Title = Title;
        this.Servings = Servings;
        this.Image = Image;
    }

    public int getId() { return Id; }
    public String getTitle() { return Title; }
    public String getServings() { return Servings; }
    public String getImage() { return Image; }

    public void setId(int id) { Id = id; }
    public void setTitle(String title) { Title = title; }
    public void setServings(String servings) { Servings = servings; }
    public void setImage(String image) { Image = image; }

    public Recipe(Parcel in) {
        this.Id = in.readInt();
        this.Title = in.readString();
        this.Servings = in.readString();
        this.Image = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Id);
        dest.writeString(this.Title);
        dest.writeString(this.Servings);
        dest.writeString(this.Image);
    }
}
