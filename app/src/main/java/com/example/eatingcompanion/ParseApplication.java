package com.example.eatingcompanion;

import android.app.Application;

import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Chat.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("eating-companion") // should correspond to APP_ID env variable
                .clientKey(getString(R.string.parse_master_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("http://eating-companion.herokuapp.com/parse/").build());
    }
}
