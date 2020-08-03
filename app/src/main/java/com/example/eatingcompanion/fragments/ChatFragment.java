package com.example.eatingcompanion.fragments;

import android.graphics.Canvas;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eatingcompanion.EndlessRecyclerViewScrollListener;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.databinding.FragmentChatBinding;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.User;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

    private RecyclerView rvChats;
    private ChatsAdapter adapter;
    private List<Chat> allChats;
    private Chat deleted;
    private String snackbarText;
    private int position;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    private SpinKitView spinKit;

    FragmentChatBinding binding;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = binding.rvChats;
        swipeContainer = binding.swipeContainer;
        spinKit = binding.spinKit;
        allChats = new ArrayList<>();
        adapter = new ChatsAdapter(getContext(), allChats);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT);
        rvChats.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(linearLayoutManager);
        Sprite wanderingCubes = new WanderingCubes();
        spinKit.setIndeterminateDrawable(wanderingCubes);
        spinKit.setVisibility(View.VISIBLE);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allChats.clear();
                adapter.clear();
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
                        allChats.addAll(chats);
                        adapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextData();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvChats.addOnScrollListener(scrollListener);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (direction == ItemTouchHelper.LEFT) {
                    position = viewHolder.getAdapterPosition();
                    deleted = allChats.get(position);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final YelpService yelpService = retrofit.create(YelpService.class);

                    yelpService.getRestaurantDetail("Bearer " + getContext().getString(R.string.yelp_api_key), deleted.getRestaurantId()).enqueue(new Callback<YelpDetailResponse>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<YelpDetailResponse> call, Response<YelpDetailResponse> response) {
                            Log.i(TAG, "onResponse " + response);
                            if (response.body() == null) {
                                Log.e(TAG, "Did not receive valid response body from Yelp API");
                                return;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy, hh:mma", Locale.US);
                            String date = sdf.format(deleted.getTime());
                            snackbarText = response.body().getName() + ", " + date;
                            allChats.remove(position);
                            adapter.notifyItemRemoved(position);
                            Log.i(TAG, "Just before snackbar");
                            Snackbar.make(rvChats, "Left \"" + snackbarText + "\"", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            allChats.add(position, deleted);
                                            adapter.notifyItemInserted(position);
                                            Log.i(TAG, "undo delete action taken");
                                        }
                                    }).show();
                            ParseRelation<Chat> chatsIn = ParseUser.getCurrentUser().getRelation(User.KEY_CHAT);
                            chatsIn.remove(deleted);
                            try {
                                ParseUser.getCurrentUser().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                            Log.i(TAG, "onFailure query restaurants for nearby chats" + t);
                        }
                    });
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.closedRed))
                        .addActionIcon(R.drawable.ic_delete_outline_24px_outlined)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvChats);

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
                allChats.addAll(chats);
                adapter.notifyDataSetChanged();
                spinKit.setVisibility(View.GONE);
            }
        });
    }

    private void loadNextData() {
        ParseRelation<ParseObject> chatsIn = ((User) ParseUser.getCurrentUser()).getRelation(User.KEY_CHAT);
        ParseQuery query = chatsIn.getQuery();
        query.setSkip(adapter.getItemCount());
        query.setLimit(20);
        query.addDescendingOrder(Chat.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allChats.addAll(chats);
                adapter.notifyDataSetChanged();
            }
        });
    }
}