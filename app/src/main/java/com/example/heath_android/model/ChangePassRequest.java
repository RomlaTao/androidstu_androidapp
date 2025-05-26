package com.example.heath_android.model;

public class ChangePassRequest {
    private String currentPassword;
    private String newPassword;

    public ChangePassRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
