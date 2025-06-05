package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class WorkoutSchedulesRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("startDate")
    private String startDate;
    
    @SerializedName("endDate")
    private String endDate;
    
    // Constructors
    public WorkoutSchedulesRequest() {}
    
    public WorkoutSchedulesRequest(String name, String description, String userId, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
} 