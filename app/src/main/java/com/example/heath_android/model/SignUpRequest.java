package com.example.heath_android.model;

public class SignUpRequest {
    private String fullName;
    private String email;
    private String password;
    public SignUpRequest(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
}
