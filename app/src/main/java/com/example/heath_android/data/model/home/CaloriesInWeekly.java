package com.example.heath_android.data.model.home;

import com.google.gson.annotations.SerializedName;

public class CaloriesInWeekly {
    private String userId;
    private String date;
    
    // Note: This might need to be adjusted based on actual API response
    // Common possibilities: "totalCaloriesConsumed", "totalCaloriesIntake", "totalCaloriesIn"
    @SerializedName("totalCalories")
    private double totalCaloriesIn;

    public CaloriesInWeekly() {}

    public CaloriesInWeekly(String date, double totalCaloriesIn) {
        this.date = date;
        this.totalCaloriesIn = totalCaloriesIn;
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

    public double getTotalCaloriesIn() {
        return totalCaloriesIn;
    }

    public void setTotalCaloriesIn(double totalCaloriesIn) {
        this.totalCaloriesIn = totalCaloriesIn;
    }
} 