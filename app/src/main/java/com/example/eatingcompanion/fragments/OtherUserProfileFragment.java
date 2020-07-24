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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.PostsAdapter;
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

public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";

    private ImageView ivProfilePicture;
    private ImageView ivCoverPicture;
    private TextView tvFirstName;
    private TextView tvUsername;
    private TextView tvBio;
    private User user;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        ivCoverPicture = view.findViewById(R.id.ivCoverPicture);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        rvPosts = view.findViewById(R.id.rvPosts);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

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
                    .circleCrop()
                    .into(ivCoverPicture);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.default_avatar)
                    .fitCenter()
                    .circleCrop()
                    .into(ivCoverPicture);
        }
        tvFirstName.setText(user.getName());
        String username = "@" + user.getUsername();
        tvUsername.setText(username);
        tvBio.setText(user.getBio());

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
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