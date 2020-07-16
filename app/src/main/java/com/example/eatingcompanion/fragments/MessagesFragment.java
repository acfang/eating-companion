package com.example.eatingcompanion.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.MessagesAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class MessagesFragment extends Fragment {

    public static final String TAG = "MessagesFragment";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private EditText etMessage;
    private Button btnMessage;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btnMessage = (Button) view.findViewById(R.id.btnMessage);
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        rvMessages.setLayoutManager(llm);

        // query messages in the chat, look for the latest one
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CHAT);
        query.whereEqualTo(Message.KEY_CHAT, (Chat) getArguments().getSerializable("chat"));
        query.addDescendingOrder(Message.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying messages", e);
                    return;
                }
                allMessages.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

//        // Show soft keyboard automatically and request focus to field
//        etMessage.requestFocus();

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
                            Log.e(TAG, "Error while saving message", e);
                        }
                        Log.i(TAG, "message save was successful!");
                    }
                });
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(etMessage.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                view.clearFocus();
                rvMessages.requestFocus();
                etMessage.setText("");
            }
        });

    }
}