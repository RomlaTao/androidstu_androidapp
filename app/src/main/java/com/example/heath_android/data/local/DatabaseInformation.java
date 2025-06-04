package com.example.heath_android.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class DatabaseInformation {
    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "userid";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public DatabaseInformation(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserInfo(String token, String email, String name) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Overloaded method to save user info with userId
    public void saveUserInfo(String token, String email, String name, String userId) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Method to save only userId (for updating userId separately)
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    // Method to get userId
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearUserInfo() {
        editor.clear();
        editor.apply();
    }
} 