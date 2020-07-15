package com.example.eatingcompanion;

import com.example.eatingcompanion.models.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class YelpAPIResponse {

    @SerializedName("businesses")
    private List<Restaurant> restaurants;

    // public constructor is necessary for collections
    public YelpAPIResponse() {
        restaurants = new ArrayList<Restaurant>();
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
