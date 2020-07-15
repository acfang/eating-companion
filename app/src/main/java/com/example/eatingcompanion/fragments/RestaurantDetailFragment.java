package com.example.eatingcompanion.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpSearchResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.models.Category;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantDetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

    private ImageView ivRestaurant;
    private TextView tvRestaurantName;
    private RatingBar rbRestaurant;
    private TextView tvReviews;
    private TextView tvAddress;
    private TextView tvDistance;
    private TextView tvPrice;
    private TextView tvRestaurantType;
    private TextView tvPhone;
    private ImageView ivPhoto0;
    private ImageView ivPhoto1;
    private ImageView ivPhoto2;

    public RestaurantDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivRestaurant = view.findViewById(R.id.ivRestaurant);
        tvRestaurantName = view.findViewById(R.id.tvRestaurantName);
        rbRestaurant = view.findViewById(R.id.rbRestaurant);
        tvReviews = view.findViewById(R.id.tvReviews);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvRestaurantType = view.findViewById(R.id.tvRestaurantType);
        tvPhone = view.findViewById(R.id.tvPhone);
        ivPhoto0 = view.findViewById(R.id.ivPhoto0);
        ivPhoto1 = view.findViewById(R.id.ivPhoto1);
        ivPhoto2 = view.findViewById(R.id.ivPhoto2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final YelpService yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurantDetail("Bearer " + getString(R.string.yelp_api_key), getArguments().getString("id")).enqueue(new Callback<YelpDetailResponse>() {
            @Override
            public void onResponse(Call<YelpDetailResponse> call, Response<YelpDetailResponse> response) {
                Log.i(TAG, "onResponse " + response);
                if (response.body() == null) {
                    Log.e(TAG, "Did not receive valid response body from Yelp API");
                    return;
                }
                Glide.with(getContext()).load(response.body().getImageUrl()).into(ivRestaurant);
                tvRestaurantName.setText(response.body().getName());
                rbRestaurant.setRating((float) response.body().getRating());
                String numReviews = response.body().getNumReviews() + " Reviews";
                tvReviews.setText(numReviews);
                String address = response.body().getLocation().getAddress() + "\n" +
                        response.body().getLocation().getCity() + ", " +
                        response.body().getLocation().getState() + " " +
                        response.body().getLocation().getZipCode();
                tvAddress.setText(address);
                tvDistance.setText(response.body().getDistance());
                tvPrice.setText(response.body().getPrice());
                String concatenatedCategories = "";
                List<Category> categories = response.body().getCategories();
                for (int i = 0; i < categories.size(); i++) {
                    concatenatedCategories += ", " + categories.get(i).getTitle();
                }
                concatenatedCategories = concatenatedCategories.substring(2);
                tvRestaurantType.setText(concatenatedCategories);
                tvPhone.setText(response.body().getPhone());
                List photos = response.body().getPhotos();
            }

            @Override
            public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                Log.i(TAG, "onFailure " + t);
            }
        });
    }
}
