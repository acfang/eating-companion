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

import com.example.eatingcompanion.databinding.FragmentEditDialogBinding;
import com.example.eatingcompanion.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "EditDialogFragment";

    EditText etSettings;
    Button btnSettings;

    String item;
    String text;

    FragmentEditDialogBinding binding;

    public EditDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public interface EditDialogListener {
        void onFinishEditDialog(String item, String text);
    }

    public static EditDialogFragment newInstance(String item) {
        EditDialogFragment frag = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putString("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.RIGHT);
        binding = FragmentEditDialogBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etSettings = binding.etSettings;
        btnSettings = binding.btnSettings;
        item = getArguments().getString("item");

        if (item.equals("name")) {
            etSettings.setHint("Change name...");
        } else if (item.equals("password")) {
            etSettings.setHint("Change password...");
        } else if (item.equals("bio")) {
            etSettings.setHint("Change bio...");
        }

        // Show soft keyboard automatically and request focus to field
        etSettings.requestFocus();

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = etSettings.getText().toString();
                User user = (User) ParseUser.getCurrentUser();
                if (item.equals("name")) {
                    user.setName(text);
                } else if (item.equals("password")) {
                    user.setPassword(text);
                } else if (item.equals("bio")) {
                    user.setBio(text);
                }
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving changes", e);
                        }
                        Log.i(TAG, "Save was successful!");
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
            // Return input text back to activity through the implemented listener
            EditDialogListener listener = (EditDialogListener) getActivity();
            listener.onFinishEditDialog(item, text);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}
