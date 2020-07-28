package com.example.eatingcompanion.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.InfoUsersAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class InfoDialogFragment extends DialogFragment {
    public static final String TAG = "InfoDialogFragment";

    private static Button btnRestaurantDetail;
    private static ImageView ivClose;
    private static RecyclerView rvUsers;
    private static InfoUsersAdapter adapter;
    private static List<User> allUsers;
    private static Chat chat;
    private static String restaurantId;

    public InfoDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static InfoDialogFragment newInstance(Chat input, String id) {
        InfoDialogFragment frag = new InfoDialogFragment();
        chat = input;
        restaurantId = id;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.RIGHT);
        return inflater.inflate(R.layout.fragment_info_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        btnRestaurantDetail = view.findViewById(R.id.btnRestaurantDetail);
        ivClose = view.findViewById(R.id.ivClose);
        rvUsers = view.findViewById(R.id.rvUsers);
        allUsers = new ArrayList<>();
        adapter = new InfoUsersAdapter(getContext(), allUsers);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRestaurantDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new RestaurantDetailFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("id", restaurantId);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                dismiss();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ParseRelation<ParseObject> usersIn = chat.getRelation(Chat.KEY_USERS);
        ParseQuery query = usersIn.getQuery();
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying users in chat", e);
                    return;
                }
                Log.i(TAG, "Number of users: " + objects.size());
                allUsers.addAll(objects);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable != null) {
                    Log.e(TAG, "Error when querying users in chat", throwable);
                    return;
                }
                Log.i(TAG, "Number of users: " + 1);
                allUsers.addAll((List<User>) o);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
