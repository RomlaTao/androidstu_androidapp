package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class DayEvent {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("calories")
    private int calories;
    
    @SerializedName("type")
    private String type; // "Lunch", "Dinner", "Workout", etc.
    
    @SerializedName("status")
    private String status; // "SCHEDULED", "COMPLETED", "SKIPPED", etc.
    
    @SerializedName("userId")
    private String userId;
    
    // Constructors
    public DayEvent() {}
    
    public DayEvent(String date, String time, String name, String description, int calories, String type) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.type = type;
        this.status = "SCHEDULED"; // Default status
    }
    
    public DayEvent(String date, String time, String name, String description, int calories, String type, String status) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.type = type;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
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
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "DayEvent{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
} 