package com.example.eatingcompanion.fragments;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.LoginActivity;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.PostsAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.Post;
import com.example.eatingcompanion.models.Restaurant;
import com.example.eatingcompanion.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private ImageView ivProfilePicture;
    private ImageView ivCoverPicture;
    private TextView tvFirstName;
    private TextView tvUsername;
    private TextView tvBio;
    private Button btnLogout;
    private Button btnEdit;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        ivCoverPicture = view.findViewById(R.id.ivCoverPicture);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEdit = view.findViewById(R.id.btnEdit);
        rvPosts = view.findViewById(R.id.rvPosts);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        User user = (User) ParseUser.getCurrentUser();

        ParseFile profilePicture = user.getProfilePicture();
        if (profilePicture != null) {
            Glide.with(getContext())
                    .load(profilePicture.getUrl())
                    .fitCenter()
                    .circleCrop()
                    .into(ivProfilePicture);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.default_avatar)
                    .fitCenter()
                    .circleCrop()
                    .into(ivProfilePicture);
        }
        ParseFile coverPicture = user.getCoverPicture();
        if (coverPicture != null) {
            Glide.with(getContext())
                    .load(coverPicture.getUrl())
                    .fitCenter()
                    .centerCrop()
                    .into(ivCoverPicture);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.default_avatar)
                    .fitCenter()
                    .centerCrop()
                    .into(ivCoverPicture);
        }
        tvFirstName.setText(user.getName());
        String username = "@" + user.getUsername();
        tvUsername.setText(username);
        tvBio.setText(user.getBio());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick logout button");
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick edit button");
                Fragment fragment;
                fragment = new SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, (User) ParseUser.getCurrentUser());
        query.addDescendingOrder(Message.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying posts", e);
                    return;
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}