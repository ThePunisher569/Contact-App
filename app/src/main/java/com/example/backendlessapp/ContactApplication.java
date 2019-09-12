package com.example.backendlessapp;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class ContactApplication extends Application {
    private static final String APP_ID = "E39B54DA-A82F-DE17-FF21-A5DA081FF000";
    private static final String API_KEY="17BE1BD7-D02E-BFEC-FF51-E8181B765400";
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
