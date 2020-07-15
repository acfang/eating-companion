package com.example.eatingcompanion;

import com.example.eatingcompanion.models.Restaurant;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<YelpAPIResponse> searchRestaurants(@Header("Authorization") String authHeader,
                                       @Query("term") String searchTerm,
                                       @Query("location") String location);
}
