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

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.adapters.UsersAdapter;
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

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvNearbyUsers = view.findViewById(R.id.rvNearbyUsers);
        rvNearbyChats = view.findViewById(R.id.rvNearbyChats);
        allUsers = new ArrayList<>();
        allChats = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), allUsers);
        chatsAdapter = new ChatsAdapter(getContext(), allChats);
        //adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvNearbyUsers.setAdapter(usersAdapter);
        rvNearbyChats.setAdapter(chatsAdapter);
        rvNearbyUsers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNearbyChats.setLayoutManager(new LinearLayoutManager(getContext()));

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
                Log.i(TAG, "Number of chats: " + chats.size());
                for (i = 0; i < chats.size(); i++) {
                    alreadyIn.add(chats.get(i).getId());
                }
            }
        });


        ParseQuery<User> userQuery = ParseQuery.getQuery(User.class);
        userQuery.include(User.KEY_CITY);
        userQuery.include(User.KEY_STATE);
        userQuery.whereEqualTo(User.KEY_CITY, ((User) ParseUser.getCurrentUser()).getCity());
        userQuery.whereEqualTo(User.KEY_STATE, ((User) ParseUser.getCurrentUser()).getState());
        userQuery.setLimit(20);
        userQuery.addDescendingOrder(Chat.KEY_CREATED_AT);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying nearby users", e);
                    return;
                }
                Log.i(TAG, "Number of users: " + users.size());
                allUsers.addAll(users);
                usersAdapter.notifyDataSetChanged();
            }
        });

        ParseQuery<Chat> chatsQuery = ParseQuery.getQuery(Chat.class);
        chatsQuery.include(Chat.KEY_TIME);
        chatsQuery.whereGreaterThanOrEqualTo(Chat.KEY_TIME, new Date());
        chatsQuery.whereNotContainedIn(Chat.KEY_ID, alreadyIn);
        chatsQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(final List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying nearby chats", e);
                    return;
                }
                for (i = 0; i < chats.size(); i++) {
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
                            if (response.body().getLocation().getCity().equals(user.getCity()) && response.body().getLocation().getState().equals(user.getState())) {
                                allChats.add(chats.get(i));
                                chatsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                            Log.i(TAG, "onFailure query restaurants for nearby chats" + t);
                        }
                    });
                }

            }
        });
    }
}