package com.example.heath_android.data.model.home;

public class CaloriesResponse {
    private double totalCaloriesBurned;

    public CaloriesResponse() {}

    public CaloriesResponse(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    // Getters and Setters
    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
} 