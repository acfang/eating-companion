package com.example.eatingcompanion.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }
}
