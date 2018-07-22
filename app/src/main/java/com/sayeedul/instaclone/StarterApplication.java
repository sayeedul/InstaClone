package com.sayeedul.instaclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

         //Enable local data store
           Parse.enableLocalDatastore(this);

        // Add Your Initialization code here.
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        //optionally enable public read access
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL,true);

    }

}
