package com.example.heath_android.data.model.home;

public class HomeResponse {
    private UserInfo userInfo;
    private boolean success;
    private String message;

    public HomeResponse() {}

    // Getters and Setters
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Inner classes
    public static class UserInfo {
        private String name;
        private String avatarUrl;

        public UserInfo() {}

        public UserInfo(String name, String avatarUrl) {
            this.name = name;
            this.avatarUrl = avatarUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
