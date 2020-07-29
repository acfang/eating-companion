package com.example.eatingcompanion.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.EndlessRecyclerViewScrollListener;
import com.example.eatingcompanion.LoginActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.PostsAdapter;
import com.example.eatingcompanion.databinding.FragmentProfileBinding;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.Post;
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
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfilePicture = binding.ivProfilePicture;
        ivCoverPicture = binding.ivCoverPicture;
        tvFirstName = binding.tvFirstName;
        tvUsername = binding.tvUsername;
        tvBio = binding.tvBio;
        btnLogout = binding.btnLogout;
        btnEdit = binding.btnEdit;
        rvPosts = binding.rvPosts;
        swipeContainer = binding.swipeContainer;
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);
        User user = (User) ParseUser.getCurrentUser();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allPosts.clear();
                adapter.clear();
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.whereEqualTo(Post.KEY_USER, (User) ParseUser.getCurrentUser());
                query.setLimit(20);
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

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextData();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

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
                getFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, (User) ParseUser.getCurrentUser());
        query.setLimit(20);
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

    private void loadNextData() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setSkip(adapter.getItemCount());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}