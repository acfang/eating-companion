package com.example.eatingcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpSearchResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.RestaurantsAdapter;
import com.example.eatingcompanion.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantsFragment extends Fragment {

    public static final String TAG = "RestaurantsFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

    private RecyclerView rvRestaurants;
    private RestaurantsAdapter adapter;
    private List<Restaurant> allRestaurants;
    private EditText etSearchTerm;
    private EditText etLocation;
    private Button btnSearch;

    public RestaurantsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRestaurants = view.findViewById(R.id.rvRestaurants);
        etSearchTerm = view.findViewById(R.id.etSearchTerm);
        etLocation = view.findViewById(R.id.etLocation);
        btnSearch = view.findViewById(R.id.btnSearch);
        allRestaurants = new ArrayList<>();
        adapter = new RestaurantsAdapter(getContext(), allRestaurants);
        rvRestaurants.setAdapter(adapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final YelpService yelpService = retrofit.create(YelpService.class);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick search button");
                String searchTerm = etSearchTerm.getText().toString();
                String location = etLocation.getText().toString();

                yelpService.searchRestaurants("Bearer " + getString(R.string.yelp_api_key), searchTerm, location).enqueue(new Callback<YelpSearchResponse>() {
                    @Override
                    public void onResponse(Call<YelpSearchResponse> call, Response<YelpSearchResponse> response) {
                        Log.i(TAG, "onResponse " + response);
                        if (response.body() == null) {
                            Log.e(TAG, "Did not receive valid response body from Yelp API");
                            return;
                        }
                        allRestaurants.addAll(response.body().getRestaurants());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<YelpSearchResponse> call, Throwable t) {
                        Log.i(TAG, "onFailure " + t);
                    }
                });
            }
        });
    }
}