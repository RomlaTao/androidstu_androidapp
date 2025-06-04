package com.example.heath_android.data.model.onboarding;

public class UserInfoRequest {
    private String initialActivityLevel; // Activity level enum

    public UserInfoRequest(String initialActivityLevel) {
        this.initialActivityLevel = initialActivityLevel;
    }
    public String getInitialActivityLevel() {
        return initialActivityLevel;
    }

    public void setInitialActivityLevel(String initialActivityLevel) {
        this.initialActivityLevel = initialActivityLevel;
    }
} 