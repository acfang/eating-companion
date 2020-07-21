package com.example.eatingcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class OtherUserProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private ImageView ivCoverPicture;
    private TextView tvFirstName;
    private TextView tvUsername;
    private TextView tvBio;
    private User user;

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
    }
}