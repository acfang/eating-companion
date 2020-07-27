package com.example.eatingcompanion.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.models.Category;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.DailyHours;
import com.example.eatingcompanion.models.User;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantDetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

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
    private EditText etSetDate;
    private Button btnSetDate;
    private EditText etSetTime;
    private Button btnSetTime;
    private Button btnCreateChat;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private Date date;
    private Calendar calendar;

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
        etSetDate = view.findViewById(R.id.etSetDate);
        btnSetDate = view.findViewById(R.id.btnSetDate);
        etSetTime = view.findViewById(R.id.etSetTime);
        btnSetTime = view.findViewById(R.id.btnSetTime);
        btnCreateChat = view.findViewById(R.id.btnCreateChat);

        calendar = Calendar.getInstance();

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
                Glide.with(getContext()).load(photos.get(0))
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                        .into(ivPhoto0);
                Glide.with(getContext()).load(photos.get(1))
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                        .into(ivPhoto1);
                Glide.with(getContext()).load(photos.get(2))
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                        .into(ivPhoto2);
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

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                etSetDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                calendar.set(year, monthOfYear + 1, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int hour;
                                String amOrPm;
                                if (hourOfDay < 12) {
                                    hour = hourOfDay;
                                    amOrPm = "AM";
                                } else {
                                    hour = hourOfDay - 12;
                                    amOrPm = "PM";
                                }
                                etSetTime.setText(hour + ":" + minute + amOrPm);
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat chat = new Chat();
                ParseRelation<User> usersIn = chat.getRelation(Chat.KEY_USERS);
                usersIn.add((User)ParseUser.getCurrentUser());
                chat.setRestaurantId(getArguments().getString("id"));
                date = calendar.getTime();
                chat.setTime(date);
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
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }
}
