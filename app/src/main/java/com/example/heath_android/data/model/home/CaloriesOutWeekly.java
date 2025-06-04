package com.example.heath_android.data.model.home;

import com.google.gson.annotations.SerializedName;

public class CaloriesOutWeekly {
    private String userId;
    private String date;
    
    @SerializedName("totalCaloriesBurned")
    private double totalCaloriesOut;

    public CaloriesOutWeekly() {}

    public CaloriesOutWeekly(String date, double totalCaloriesOut) {
        this.date = date;
        this.totalCaloriesOut = totalCaloriesOut;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalCaloriesOut() {
        return totalCaloriesOut;
    }

    public void setTotalCaloriesOut(double totalCaloriesOut) {
        this.totalCaloriesOut = totalCaloriesOut;
    }
} 