package com.example.eatingcompanion.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("_User")
public class User extends ParseUser implements Serializable {
    public User() {};

    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_COVER_PICTURE = "coverPicture";
    public static final String KEY_BIO = "bio";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_CHAT = "chatsIn";
    public static final String KEY_NAME = "firstName";
    public static final String KEY_ID = "objectId";

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile file) {
        put(KEY_PROFILE_PICTURE, file);
    }

    public ParseFile getCoverPicture() {
        return getParseFile(KEY_COVER_PICTURE);
    }

    public void setCoverPicture(ParseFile file) {
        put(KEY_COVER_PICTURE, file);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public String getCity() {
        return getString(KEY_CITY);
    }

    public void setCity(String city) {
        put(KEY_CITY, city);
    }

    public String getState() {
        return getString(KEY_STATE);
    }

    public void setState(String state) {
        put(KEY_STATE, state);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
}
