package com.example.eatingcompanion.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public Message() {};

    public static final String KEY_USER = "userId";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_CHAT = "chatId";
    public static final String KEY_BODY = "body";
    public static final String KEY_MEDIA = "media";

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public ParseObject getChat() { return getParseObject(KEY_CHAT); }

    public void setChat(ParseObject chat) { put(KEY_CHAT, chat); }

    public ParseFile getMedia() {
        return getParseFile(KEY_MEDIA);
    }

    public void setMedia(ParseFile parseFile) {
        put(KEY_MEDIA, parseFile);
    }
}
