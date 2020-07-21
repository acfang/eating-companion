package com.example.eatingcompanion.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LocationDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "LocationDialogFragment";

    EditText etCity;
    EditText etState;
    Button btnSettings;

    public LocationDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static LocationDialogFragment newInstance() {
        LocationDialogFragment frag = new LocationDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.RIGHT);
        return inflater.inflate(R.layout.fragment_location_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCity = (EditText) view.findViewById(R.id.etCity);
        etState = (EditText) view.findViewById(R.id.etState);
        btnSettings = (Button) view.findViewById(R.id.btnSettings);

        // Show soft keyboard automatically and request focus to field
        etCity.requestFocus();

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = (User) ParseUser.getCurrentUser();
                user.setCity(etCity.getText().toString());
                user.setState(etState.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving location", e);
                        }
                        Log.i(TAG, "Location save was successful!");
                    }
                });
                dismiss();
            }
        });

        btnSettings.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}
