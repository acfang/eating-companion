package com.example.eatingcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.adapters.ChatsAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    private RecyclerView rvChats;
    private ChatsAdapter adapter;
    private List<Chat> allChats;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = view.findViewById(R.id.rvChats);
        allChats = new ArrayList<>();
        adapter = new ChatsAdapter(getContext(), allChats);
        //adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        rvChats.setAdapter(adapter);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        ParseRelation<ParseObject> chatsIn = ((User) ParseUser.getCurrentUser()).getRelation(User.KEY_CHAT);
        ParseQuery query = chatsIn.getQuery();
        query.setLimit(20);
        query.addDescendingOrder(Chat.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> chats, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new chats", e);
                    return;
                }
                Log.i(TAG, "Number of chats: " + chats.size());
                allChats.addAll(chats);
                adapter.notifyDataSetChanged();
            }
        });
    }
}