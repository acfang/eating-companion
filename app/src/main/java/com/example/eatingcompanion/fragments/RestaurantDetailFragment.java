package com.example.eatingcompanion.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.models.Category;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.DailyHours;
import com.example.eatingcompanion.models.Restaurant;
import com.example.eatingcompanion.models.User;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
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
    private TextView tvHours;
    private EditText etSetTime;
    private Button btnCreateChat;
    private Spinner spinner;
    private static final String[] paths = {"Today", "Tomorrow", "In 2 Days", "In 3 Days", "In 4 Days", "In 5 Days", "In 6 Days", "In a Week"};
    private int spinnerChoice;

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
        tvHours = view.findViewById(R.id.tvHours);
        etSetTime = view.findViewById(R.id.etSetTime);
        btnCreateChat = view.findViewById(R.id.btnCreateChat);
        spinner = view.findViewById(R.id.spinner);

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
                Glide.with(getContext()).load(response.body().getImageUrl()).centerCrop().into(ivRestaurant);
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
                Glide.with(getContext()).load(photos.get(0)).centerCrop().into(ivPhoto0);
                Glide.with(getContext()).load(photos.get(1)).centerCrop().into(ivPhoto1);
                Glide.with(getContext()).load(photos.get(2)).centerCrop().into(ivPhoto2);
                String concatenatedHours = "";
                List<DailyHours> dailyHours = response.body().getHours().get(0).getOpenHours();
                for (int i = 0; i < dailyHours.size(); i++) {
                    concatenatedHours += dailyHours.get(i).getDailyHours();
                }
                tvHours.setText(concatenatedHours);
            }

            @Override
            public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                Log.i(TAG, "onFailure " + t);
            }
        });

        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setTime = etSetTime.getText().toString();
                Chat chat = new Chat();
                ParseRelation<User> usersIn = chat.getRelation(Chat.KEY_USERS);
                usersIn.add((User)ParseUser.getCurrentUser());
                chat.setRestaurantId(getArguments().getString("id"));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, spinnerChoice);
                calendar.set(Calendar.HOUR, Integer.parseInt(setTime.substring(0,2)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(setTime.substring(3)));
                chat.setTime(calendar.getTime());
                try {
                    chat.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseRelation<Chat> chatsIn = ParseUser.getCurrentUser().getRelation(User.KEY_CHAT);
                chatsIn.add(chat);

                // go to chat fragment
                Log.i(TAG, "Chat created");
                Fragment fragment;
                fragment = new MessagesFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putSerializable("chat", chat);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerChoice = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerChoice = 0;
            }
        });
    }
}
