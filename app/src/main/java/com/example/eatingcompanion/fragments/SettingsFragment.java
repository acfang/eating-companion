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

import com.example.eatingcompanion.LoginActivity;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.Restaurant;
import com.parse.ParseUser;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";
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
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile);
        btnChangeCover = view.findViewById(R.id.btnChangeCover);
        btnChangeLocation = view.findViewById(R.id.btnChangeLocation);
        btnChangeName = view.findViewById(R.id.btnChangeName);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeBio = view.findViewById(R.id.btnChangeBio);

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

            }
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnChangeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}