package com.sample.instamojo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class InstamojoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Hardcoding these values for timebeing
        String appId = "OiZNKB4iWmdAe5hSmu42Fl9MKVmhR0rH0CWPf7QL";
        String clientKey = "8bDOSdhKuzxmazzXQwMnLYKW8ZQJ0lFGDBHr6m1J";

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, appId, clientKey);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
