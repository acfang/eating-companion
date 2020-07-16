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
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "MessageDialogFragment";

    EditText etMessage;
    Button btnMessage;

    Message message;

    public MessageDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface MessageDialogListener {
        void onFinishMessageDialog(Message message);
    }

    public static MessageDialogFragment newInstance(Chat chat) {
        MessageDialogFragment frag = new MessageDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("chat", chat);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT);
        return inflater.inflate(R.layout.fragment_message_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btnMessage = (Button) view.findViewById(R.id.btnMessage);

        // Show soft keyboard automatically and request focus to field
        etMessage.requestFocus();

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBody = etMessage.getText().toString();
                Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                message.setChat((Chat) getArguments().getSerializable("chat"));
                message.setBody(messageBody);
                message.saveInBackground(new SaveCallback() {
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

        btnMessage.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            // Return input text back to activity through the implemented listener
            MessageDialogListener listener = (MessageDialogListener) getActivity();
            listener.onFinishMessageDialog(message);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}
