package com.example.eatingcompanion.fragments;

import android.content.ClipData;
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

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
    static int SPLASH_TIME_OUT = 7000; // snackbar shows for 7 seconds

    private RecyclerView rvChats;
    private ChatsAdapter adapter;
    private List<Chat> allChats;
    private Chat deleted;
    private String snackbarText;
    private int position;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = view.findViewById(R.id.rvChats);
        allChats = new ArrayList<>();
        adapter = new ChatsAdapter(getContext(), allChats);
        //adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvChats.setAdapter(adapter);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

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
                        }

                        @Override
                        public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                            Log.i(TAG, "onFailure query restaurants for nearby chats" + t);
                        }
                    });

                    allChats.remove(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar snackbar = Snackbar.make(rvChats, "Left \"" + snackbarText + "\"", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    allChats.add(position, deleted);
                                    adapter.notifyItemInserted(position);
                                }
                            });
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                        }
                    }, SPLASH_TIME_OUT);

                    snackbar.dismiss();
                    ParseRelation<Chat> chatsIn = ParseUser.getCurrentUser().getRelation(User.KEY_CHAT);
                    chatsIn.remove(deleted);
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
            }
        });
    }
}