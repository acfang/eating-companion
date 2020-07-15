package com.example.eatingcompanion.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {

    @SerializedName("name")
    private String name;
    @SerializedName("rating")
    private double rating;
    @SerializedName("price")
    private String price;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("categories")
    private List<Category> categories;
    @SerializedName("distance")
    private double distance;
    @SerializedName("location")
    private Location location;
    @SerializedName("review_count")
    private int numReviews;

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getDistance() {
        double milesPerMeter = 0.000621371;
        return "%2.f".format(String.valueOf(distance * milesPerMeter)) + " mi";
    }

    public Location getLocation() {
        return location;
    }

    public int getNumReviews() {
        return numReviews;
    }
}
