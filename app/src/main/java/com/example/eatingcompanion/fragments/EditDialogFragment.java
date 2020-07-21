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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.eatingcompanion.R;
import com.parse.ParseException;

public class EditDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "CommentDialogFragment";

    EditText etSettings;
    Button btnSettings;

    String item;
    String text;

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
        return inflater.inflate(R.layout.fragment_edit_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etSettings = (EditText) view.findViewById(R.id.etSettings);
        btnSettings = (Button) view.findViewById(R.id.btnSettings);
        item = getArguments().getString("item");

        // Show soft keyboard automatically and request focus to field
        etSettings.requestFocus();

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentBody = etSettings.getText().toString();
                text = etSettings.getText().toString();
//                Comment comment = new Comment();
//                comment.setUser(ParseUser.getCurrentUser());
//                comment.setPost((Post) getArguments().getSerializable("post"));
//                comment.setBody(commentBody);
//                comment.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e != null) {
//                            Log.e(TAG, "Error while saving comment", e);
//                        }
//                        Log.i(TAG, "Comment save was successful!");
//                    }
//                });
//                dismiss();
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
