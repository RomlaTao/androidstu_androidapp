package com.example.heath_android.model;

public class ChangePassResponse {
    private String message;
    private String token;
    private long expiresIn;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
