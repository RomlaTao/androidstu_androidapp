package com.example.heath_android.data.model.onboarding;

public class UserInfoRequest {
    private String birthDate;
    private String gender;
    private double height;
    private double weight;
    private String initialActivityLevel; // Activity level enum

    public UserInfoRequest(String birthDate, String gender, double height, double weight, String initialActivityLevel) {
        this.birthDate = birthDate;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.initialActivityLevel = initialActivityLevel;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getInitialActivityLevel() {
        return initialActivityLevel;
    }

    public void setInitialActivityLevel(String initialActivityLevel) {
        this.initialActivityLevel = initialActivityLevel;
    }
} 