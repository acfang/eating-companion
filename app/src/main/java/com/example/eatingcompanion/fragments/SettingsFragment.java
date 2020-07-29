package com.example.eatingcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.databinding.FragmentSettingsBinding;
import com.example.eatingcompanion.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class SettingsFragment extends Fragment implements EditDialogFragment.EditDialogListener {

    public static final String TAG = "SettingsFragment";
    private ImageView ivProfilePicture;
    private ImageView ivCoverPicture;
    private TextView tvFirstName;
    private TextView tvUsername;
    private TextView tvBio;
    private Button btnChangeProfile;
    private Button btnChangeCover;
    private Button btnChangeLocation;
    private Button btnChangeName;
    private Button btnChangePassword;
    private Button btnChangeBio;

    FragmentSettingsBinding binding;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivProfilePicture = binding.ivProfilePicture;
        ivCoverPicture = binding.ivCoverPicture;
        tvFirstName = binding.tvFirstName;
        tvUsername = binding.tvUsername;
        tvBio = binding.tvBio;
        btnChangeProfile = binding.btnChangeProfile;
        btnChangeCover = binding.btnChangeCover;
        btnChangeLocation = binding.btnChangeLocation;
        btnChangeName = binding.btnChangeName;
        btnChangePassword = binding.btnChangePassword;
        btnChangeBio = binding.btnChangeBio;
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
                getFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
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
                getFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change location button clicked");
                showLocationDialog();
            }
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change name button clicked");
                showEditDialog("name");
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change password button clicked");
                showEditDialog("password");
            }
        });

        btnChangeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Change bio button clicked");
                showEditDialog("bio");
            }
        });
    }

    private void showEditDialog(String item) {
        FragmentManager fm = getFragmentManager();
        EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(item);
        editDialogFragment.show(fm, "fragment_edit_dialog");
    }

    @Override
    public void onFinishEditDialog(String item, String text) {
        if (item.equals("name")) {
            tvFirstName.setText(text);
        } else if (item.equals("bio")) {
            tvBio.setText(text);
        }
    }

    private void showLocationDialog() {
        FragmentManager fm = getFragmentManager();
        LocationDialogFragment locationDialogFragment = LocationDialogFragment.newInstance();
        locationDialogFragment.show(fm, "fragment_edit_dialog");
    }
}