package com.example.eatingcompanion;

import com.example.eatingcompanion.models.Category;
import com.example.eatingcompanion.models.Hours;
import com.example.eatingcompanion.models.Location;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YelpDetailResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("display_phone")
    private String phone;
    @SerializedName("rating")
    private double rating;
    @SerializedName("price")
    private String price;
    @SerializedName("categories")
    private List<Category> categories;
    @SerializedName("distance")
    private double distance;
    @SerializedName("location")
    private Location location;
    @SerializedName("review_count")
    private int numReviews;
    @SerializedName("photos")
    private List photos;
//    TODO: retrieve restaurant hours
    @SerializedName("hours")
    private List<Hours> hours;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getDistance() {
        double milesPerMeter = 0.000621371;
        return String.format("%.2f", distance * milesPerMeter) + " mi";
    }

    public Location getLocation() {
        return location;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public List getPhotos() {
        return photos;
    }

    public List<Hours> getHours() {
        return hours;
    }
}
