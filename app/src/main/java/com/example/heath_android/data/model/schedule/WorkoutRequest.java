package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkoutRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("durationMinutes")
    private int durationMinutes;
    
    @SerializedName("caloriesBurned")
    private int caloriesBurned;
    
    @SerializedName("exercises")
    private List<Exercise> exercises;
    
    @SerializedName("userId")
    private String userId;

    // Constructors
    public WorkoutRequest() {}
    
    public WorkoutRequest(String name, String description, String type, int durationMinutes, int caloriesBurned, List<Exercise> exercises) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.exercises = exercises;
    }
    
    public WorkoutRequest(String name, String description, String type, int durationMinutes, int caloriesBurned, List<Exercise> exercises, String userId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.exercises = exercises;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public int getCaloriesBurned() {
        return caloriesBurned;
    }
    
    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public List<Exercise> getExercises() {
        return exercises;
    }
    
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
