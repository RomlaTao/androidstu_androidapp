package com.example.heath_android.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String token;
    private long expiresIn;
    
    @SerializedName("userId")
    private String userId;
    
    private boolean isUserInfoInitialized;
    
    public LoginResponse() {}

    public LoginResponse(String token, long expiresIn, String userId, boolean isUserInfoInitialized) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.isUserInfoInitialized = isUserInfoInitialized;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() { 
        return userId; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public boolean isUserInfoInitialized() {
        return isUserInfoInitialized;
    }
    
    public void setUserInfoInitialized(boolean userInfoInitialized) {
        isUserInfoInitialized = userInfoInitialized;
    }

    // Keep old method for backward compatibility (deprecated)
    @Deprecated
    public String getUserID() { 
        return userId; 
    }
    
    @Deprecated
    public void setUserID(String userID) { 
        this.userId = userID; 
    }
} 