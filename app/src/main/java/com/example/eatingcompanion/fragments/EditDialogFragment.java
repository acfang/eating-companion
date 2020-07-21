package com.example.eatingcompanion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.eatingcompanion.R;

public class EditDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "CommentDialogFragment";

    EditText etSettings;
    Button btnSettings;

    Comment comment;

    public EditDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface EditDialogListener {
        void onFinishCommentDialog(Comment comment);
    }

    public static EditDialogFragment newInstance(String item) {
        EditDialogFragment frag = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etSettings = (EditText) view.findViewById(R.id.etSettings);
        btnSettings = (Button) view.findViewById(R.id.btnSettings);



        // Show soft keyboard automatically and request focus to field
        etSettings.requestFocus();

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentBody = etSettings.getText().toString();
                Comment comment = new Comment();
                comment.setUser(ParseUser.getCurrentUser());
                comment.setPost((Post) getArguments().getSerializable("post"));
                comment.setBody(commentBody);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving comment", e);
                        }
                        Log.i(TAG, "Comment save was successful!");
                    }
                });
                dismiss();
            }
        });

        btnComment.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            // Return input text back to activity through the implemented listener
            CommentDialogListener listener = (CommentDialogListener) getActivity();
            listener.onFinishCommentDialog(comment);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}
