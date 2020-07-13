package com.example.eatingcompanion.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class Chat extends ParseObject {
    public Chat() {};

    public static final String KEY_RESTAURANT = "restaurantId";
    public static final String KEY_TIME = "timeToGo";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_USERS = "usersIn";

    public String getRestaurantId() {
        return getString(KEY_RESTAURANT);
    }

    public void setRestaurantId(String restaurantId) {
        put(KEY_RESTAURANT, restaurantId);
    }

    public String getTime() {
        return getString(KEY_TIME);
    }

    public void setTime(String time) {
        put(KEY_TIME, time);
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
}
