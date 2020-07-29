package com.example.eatingcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.adapters.UsersAdapter;
import com.example.eatingcompanion.databinding.FragmentUsersBinding;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyFragment extends Fragment {

    public static final String TAG = "NearbyFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

    private RecyclerView rvNearbyUsers;
    private RecyclerView rvNearbyChats;
    private UsersAdapter usersAdapter;
    private ChatsAdapter chatsAdapter;
    private List<User> allUsers;
    private List<Chat> allChats;
    private int i;
    private List<String> alreadyIn;

    FragmentUsersBinding binding;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvNearbyUsers = binding.rvNearbyUsers;
        rvNearbyChats = binding.rvNearbyChats;
        allUsers = new ArrayList<>();
        allChats = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), allUsers);
        chatsAdapter = new ChatsAdapter(getContext(), allChats);
        rvNearbyUsers.setAdapter(usersAdapter);
        rvNearbyChats.setAdapter(chatsAdapter);
        rvNearbyUsers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNearbyChats.setLayoutManager(new LinearLayoutManager(getContext()));
        alreadyIn = new ArrayList<>();

        ParseRelation<ParseObject> chatsIn = ((User) ParseUser.getCurrentUser()).getRelation(User.KEY_CHAT);
        ParseQuery query = chatsIn.getQuery();
        query.setLimit(20);
        query.addDescendingOrder(Chat.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new chats", e);
                    return;
                }
                for (i = 0; i < chats.size(); i++) {
                    alreadyIn.add(chats.get(i).getId());
                }
            }
        });

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        userQuery.include(User.KEY_CITY);
//        userQuery.include(User.KEY_STATE);
//        userQuery.whereEqualTo(User.KEY_CITY, ((User) ParseUser.getCurrentUser()).getCity());
//        userQuery.whereEqualTo(User.KEY_STATE, ((User) ParseUser.getCurrentUser()).getState());
        userQuery.setLimit(20);
        userQuery.addDescendingOrder(Chat.KEY_CREATED_AT);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying nearby users", e);
                    return;
                }
                Log.i(TAG, "Number of users: " + objects.size());
                for (int index = 0; index < objects.size(); index++) {
                    allUsers.add((User) objects.get(index));
                }
                usersAdapter.notifyDataSetChanged();
            }
        });

        ParseQuery<Chat> chatsQuery = ParseQuery.getQuery(Chat.class);
        chatsQuery.include(Chat.KEY_TIME);
        //chatsQuery.whereGreaterThanOrEqualTo(Chat.KEY_TIME, new Date());
        chatsQuery.whereNotContainedIn(Chat.KEY_ID, alreadyIn);
        chatsQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(final List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying nearby chats", e);
                    return;
                }
                Log.i(TAG, "Number of chats: " + chats.size());
                for (i = 0; i < chats.size(); i++) {
                    final Chat chat = chats.get(i);
                    String restaurantId = chats.get(i).getRestaurantId();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final YelpService yelpService = retrofit.create(YelpService.class);

                    yelpService.getRestaurantDetail("Bearer " + getContext().getString(R.string.yelp_api_key), restaurantId).enqueue(new Callback<YelpDetailResponse>() {
                        @Override
                        public void onResponse(Call<YelpDetailResponse> call, Response<YelpDetailResponse> response) {
                            Log.i(TAG, "onResponse " + response);
                            if (response.body() == null) {
                                Log.e(TAG, "Did not receive valid response body from Yelp API");
                                return;
                            }
                            User user = (User) ParseUser.getCurrentUser();
                            Log.i(TAG, "restaurant city: " + response.body().getLocation().getCity());
                            Log.i(TAG, "restaurant state: " + response.body().getLocation().getState());
                            Log.i(TAG, "user city: " + user.getCity());
                            Log.i(TAG, "user state: " + user.getState());
                            if (response.body().getLocation().getCity().equals(user.getCity()) && response.body().getLocation().getState().equals(user.getState())) {
                                allChats.add(chat);
                                chatsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                            Log.i(TAG, "onFailure query restaurants for nearby chats" + t);
                        }
                    });

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "sleep");
                        }
                    }, 10000);
                }
                Log.i(TAG, "allChats size: " + allChats.size());
            }
        });
    }
}