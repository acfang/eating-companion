package com.example.eatingcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eatingcompanion.databinding.ActivitySignUpBinding;
import com.example.eatingcompanion.fragments.PictureFragment;
import com.example.eatingcompanion.models.User;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";
    private EditText etFirstName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etCity;
    private EditText etState;
    private ImageView ivProfilePicture;
    private EditText etBio;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etFirstName = binding.etFirstName;
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        etCity = binding.etCity;
        etState = binding.etState;
        ivProfilePicture = binding.ivProfilePicture;
        etBio = binding.etBio;
        btnSignUp = binding.btnSignUp;

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick sign up button");
                String firstName = etFirstName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String city = etCity.getText().toString();
                String state = etState.getText().toString();
                String bio = etBio.getText().toString();
                signUpUser(firstName, username, password, city, state, bio);
            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Set profile button clicked");
                Fragment fragment;
                fragment = new PictureFragment();
                // create bundle of post info to send to detail fragment
                Bundle args = new Bundle();
                args.putString("photoType", "profilePicture");
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });
    }

    private void signUpUser(String firstName, String username, String password, String city, String state, String bio) {
        Log.i(TAG, "Attempting to sign up user " + username);
        // Create the ParseUser
        User user = new User();
        // Set core properties
        user.setName(firstName);
        user.setUsername(username);
        user.setPassword(password);
        user.setCity(city);
        user.setState(state);
        user.setBio(bio);
        ParseACL parseACL = new ParseACL(user);
        parseACL.setPublicReadAccess(true);
        parseACL.setPublicWriteAccess(true);
        user.setACL(parseACL);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.i(TAG, "Sign up successful");
                    goMainActivity();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Issue with sign up", e);
                    return;
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
