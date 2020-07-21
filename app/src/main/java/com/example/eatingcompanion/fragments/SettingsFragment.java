package com.example.eatingcompanion.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.eatingcompanion.models.Restaurant;
import com.example.eatingcompanion.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";
    private ImageView ivProfilePicture;
    private ImageView ivCoverPicture;
    private TextView tvFirstName;
    private TextView tvUsername;
    private TextView tvBio;
    private Button btnLogout;
    private Button btnChangeProfile;
    private Button btnChangeCover;
    private Button btnChangeLocation;
    private Button btnChangeName;
    private Button btnChangePassword;
    private Button btnChangeBio;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        ivCoverPicture = view.findViewById(R.id.ivCoverPicture);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile);
        btnChangeCover = view.findViewById(R.id.btnChangeCover);
        btnChangeLocation = view.findViewById(R.id.btnChangeLocation);
        btnChangeName = view.findViewById(R.id.btnChangeName);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeBio = view.findViewById(R.id.btnChangeBio);
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

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change profile button clicked");
                Fragment fragment;
                fragment = new PictureFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("photoType", "profilePicture");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        btnChangeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change cover button clicked");
                Fragment fragment;
                fragment = new PictureFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("photoType", "coverPicture");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change location button clicked");
                Fragment fragment;
                fragment = new EditProfileFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("item", "location");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change name button clicked");
                Fragment fragment;
                fragment = new EditProfileFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("item", "name");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change password button clicked");
                Fragment fragment;
                fragment = new EditProfileFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("item", "password");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        btnChangeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change bio button clicked");
                Fragment fragment;
                fragment = new EditProfileFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("item", "bio");
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });
    }
}