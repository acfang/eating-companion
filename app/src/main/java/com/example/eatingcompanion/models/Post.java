package com.example.eatingcompanion.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public Post() {}

    public static final String KEY_USER = "userId";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_RESTAURANT = "restaurant";
    public static final String KEY_OTHER_USER = "otherUser";
    public static final String KEY_IMAGE = "image";

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public String getRestaurant() {
        return getString(KEY_RESTAURANT);
    }

    public void setRestaurant(String restaurant) {
        put(KEY_RESTAURANT, restaurant);
    }

    public String getOtherUser() {
        return getString(KEY_OTHER_USER);
    }

    public void setOtherUser(String user) {
        put(KEY_OTHER_USER, user);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }
}
