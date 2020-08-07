package com.example.eatingcompanion.fragments;

import android.content.Intent;
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
import com.example.eatingcompanion.databinding.FragmentInfoDialogBinding;
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
    private static Button btnInviteLink;
    private static ImageView ivClose;
    private static RecyclerView rvUsers;
    private static InfoUsersAdapter adapter;
    private static List<User> allUsers;
    private static Chat chat;
    private static String restaurantId;

    FragmentInfoDialogBinding binding;

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
        binding = FragmentInfoDialogBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        btnRestaurantDetail = binding.btnRestaurantDetail;
        btnInviteLink = binding.btnInviteLink;
        ivClose = binding.ivClose;
        rvUsers = binding.rvUsers;
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

        btnInviteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://www.eattogether.com/invite?id=" + chat.getObjectId();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
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
        try {
            List result = query.find();
            Log.i(TAG, "result size: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                User user = (User) result.get(i);
                allUsers.add(user);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        query.findInBackground(new FindCallback() {
//            @Override
//            public void done(List objects, ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Error when querying users in chat", e);
//                    return;
//                }
//                Log.i(TAG, "Number of users: " + objects.size());
//                allUsers.addAll(objects);
//                adapter.notifyDataSetChanged();
//            }
//        });
    }
}
