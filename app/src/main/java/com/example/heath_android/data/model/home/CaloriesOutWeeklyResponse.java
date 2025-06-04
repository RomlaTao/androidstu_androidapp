package com.example.heath_android.data.model.home;

import java.util.List;

public class CaloriesOutWeeklyResponse {
    private List<CaloriesOutWeekly> caloriesOutWeekly;

    public CaloriesOutWeeklyResponse() {}

    public CaloriesOutWeeklyResponse(List<CaloriesOutWeekly> caloriesOutWeekly) {
        this.caloriesOutWeekly = caloriesOutWeekly;
    }

        // Getters and Setters
    public List<CaloriesOutWeekly> getCaloriesOutWeekly() {
        return caloriesOutWeekly;
    }

    public void setCaloriesOutWeekly(List<CaloriesOutWeekly> caloriesOutWeekly) {
        this.caloriesOutWeekly = caloriesOutWeekly;
    }
}
