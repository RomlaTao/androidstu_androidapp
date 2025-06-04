package com.example.heath_android.data.model.home;

import java.util.List;

public class CaloriesInWeeklyResponse {
    private List<CaloriesInWeekly> caloriesInWeekly;

    public CaloriesInWeeklyResponse() {}

    public CaloriesInWeeklyResponse(List<CaloriesInWeekly> caloriesInWeekly) {
        this.caloriesInWeekly = caloriesInWeekly;
    }

        // Getters and Setters
    public List<CaloriesInWeekly> getCaloriesInWeekly() {
        return caloriesInWeekly;
    }

    public void setCaloriesInWeekly(List<CaloriesInWeekly> caloriesInWeekly) {
        this.caloriesInWeekly = caloriesInWeekly;
    }
}
