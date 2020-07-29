package com.example.eatingcompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.example.eatingcompanion.databinding.ActivityMainBinding;
import com.example.eatingcompanion.fragments.ChatFragment;
import com.example.eatingcompanion.fragments.NearbyFragment;
import com.example.eatingcompanion.fragments.PostFragment;
import com.example.eatingcompanion.fragments.ProfileFragment;
import com.example.eatingcompanion.fragments.RestaurantsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private CheckBox cbCamera;
    private CheckBox cbProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = binding.bottomNavigation;
        cbCamera = binding.cbCamera;
        cbProfile = binding.cbProfile;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_restaurant:
                        Log.i(TAG, "Restaurant button clicked");
                        cbCamera.setChecked(false);
                        cbProfile.setChecked(false);
                        fragment = new RestaurantsFragment();
                        break;
                    case R.id.action_chat:
                        Log.i(TAG, "Chat button clicked");
                        cbCamera.setChecked(false);
                        cbProfile.setChecked(false);
                        fragment = new ChatFragment();
                        break;
                    case R.id.action_users:
                    default:
                        Log.i(TAG, "Nearby Users button clicked");
                        cbCamera.setChecked(false);
                        cbProfile.setChecked(false);
                        fragment = new NearbyFragment();
                        break;
                }
                fragmentManager.beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_restaurant);

        cbCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Settings button clicked");
                uncheckAllItems();
                cbProfile.setChecked(false);
                Fragment fragment = new PostFragment();
                fragmentManager.beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        cbProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Profile button clicked");
                uncheckAllItems();
                cbCamera.setChecked(false);
                Fragment fragment = new ProfileFragment();
                fragmentManager.beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    private void uncheckAllItems() {
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
    }

    @Override
    public void onBackPressed(){
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fragmentManager.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}