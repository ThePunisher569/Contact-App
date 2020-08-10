package com.example.backendlessapp;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class ContactApplication extends Application {
    private static final String APP_ID = "YOUR_APP_ID";
    private static final String API_KEY="YOUR_API_KEY";
    private static final String SERVER_URL="https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> list;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(),APP_ID,API_KEY);
    }
}
