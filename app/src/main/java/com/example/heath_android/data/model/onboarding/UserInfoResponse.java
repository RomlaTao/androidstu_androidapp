package com.example.heath_android.data.model.onboarding;

import java.util.Date;

public class UserInfoResponse {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String gender;
    private Date birthDate;
    private double weight;
    private double height;
    private String initialActivityLevel;
    public UserInfoResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getInitialActivityLevel() {
        return initialActivityLevel;
    }

    public void setInitialActivityLevel(String initialActivityLevel) {
        this.initialActivityLevel = initialActivityLevel;
    }
} 