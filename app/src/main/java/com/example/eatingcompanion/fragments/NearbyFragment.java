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

import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.adapters.UsersAdapter;
import com.example.eatingcompanion.databinding.FragmentUsersBinding;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.User;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private SpinKitView spinKitUsers;
    private SpinKitView spinKitChats;

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
        spinKitUsers = binding.spinKitUsers;
        spinKitChats = binding.spinKitChats;
        allUsers = new ArrayList<>();
        allChats = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), allUsers);
        chatsAdapter = new ChatsAdapter(getContext(), allChats);
        rvNearbyUsers.setAdapter(usersAdapter);
        rvNearbyChats.setAdapter(chatsAdapter);
        rvNearbyUsers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNearbyChats.setLayoutManager(new LinearLayoutManager(getContext()));
        alreadyIn = new ArrayList<>();
        Sprite wanderingCubes = new WanderingCubes();
        spinKitUsers.setIndeterminateDrawable(wanderingCubes);
        spinKitChats.setIndeterminateDrawable(wanderingCubes);
        spinKitUsers.setVisibility(View.VISIBLE);
        spinKitChats.setVisibility(View.VISIBLE);

        ParseRelation<ParseObject> chatsIn = ((User) ParseUser.getCurrentUser()).getRelation(User.KEY_CHAT);
        ParseQuery query = chatsIn.getQuery();
        query.setLimit(20);
        query.addDescendingOrder(Chat.KEY_CREATED_AT);
//        query.findInBackground(new FindCallback<Chat>() {
//            @Override
//            public void done(List<Chat> chats, ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Error when querying new chats", e);
//                    return;
//                }
//                for (i = 0; i < chats.size(); i++) {
//                    alreadyIn.add(chats.get(i).getObjectId());
//                    Log.i(TAG, "chat id already in: " + chats.get(i).getObjectId());
//                }
//                Log.i(TAG, "chats already in: " + alreadyIn.size());
//            }
//        });
        try {
            List results = query.find();
            for (int i = 0; i < results.size(); i++) {
                Chat chat = (Chat) results.get(i);
                alreadyIn.add(chat.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include(User.KEY_CITY);
        userQuery.include(User.KEY_STATE);
        userQuery.whereEqualTo(User.KEY_CITY, ((User) ParseUser.getCurrentUser()).getCity());
        userQuery.whereEqualTo(User.KEY_STATE, ((User) ParseUser.getCurrentUser()).getState());
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
                spinKitUsers.setVisibility(View.GONE);
            }
        });

        //Log.i(TAG, "chats already in: " + alreadyIn.size() + " " + alreadyIn.get(0));

        ParseQuery<Chat> chatsQuery = ParseQuery.getQuery(Chat.class);
        chatsQuery.include(Chat.KEY_TIME);
        chatsQuery.include(Chat.KEY_CITY);
        chatsQuery.include(Chat.KEY_STATE);
        chatsQuery.include(Chat.KEY_ID);
        chatsQuery.whereGreaterThanOrEqualTo(Chat.KEY_TIME, new Date());
        chatsQuery.whereEqualTo(Chat.KEY_CITY, ((User)ParseUser.getCurrentUser()).getCity());
        chatsQuery.whereEqualTo(Chat.KEY_STATE, ((User)ParseUser.getCurrentUser()).getState());
        chatsQuery.whereNotContainedIn(Chat.KEY_ID, alreadyIn);
        chatsQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(final List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying nearby chats", e);
                    return;
                }
                Log.i(TAG, "Number of chats: " + chats.size());
                allChats.addAll(chats);
                chatsAdapter.notifyDataSetChanged();
                spinKitChats.setVisibility(View.GONE);
            }
        });
    }
}