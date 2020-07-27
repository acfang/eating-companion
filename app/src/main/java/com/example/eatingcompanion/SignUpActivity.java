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

import com.example.eatingcompanion.fragments.PictureFragment;
import com.example.eatingcompanion.models.User;
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
        setContentView(R.layout.activity_sign_up);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etFirstName = findViewById(R.id.etFirstName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etBio = findViewById(R.id.etBio);
        btnSignUp = findViewById(R.id.btnSignUp);

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
