package com.example.eatingcompanion;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<YelpSearchResponse> searchRestaurants(@Header("Authorization") String authHeader,
                                               @Query("term") String searchTerm,
                                               @Query("location") String location);

    @GET("businesses/{id}")
    Call<YelpDetailResponse> getRestaurantDetail(@Header("Authorization") String authHeader, @Path("id") String restaurantId);
}
