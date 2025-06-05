package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkoutSchedulesResponse {
    @SerializedName("id")
    private Long id;
    
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
    
    @SerializedName("scheduledWorkouts")
    private List<ScheduledWorkoutResponse> scheduledWorkouts;

    // Constructors
    public WorkoutSchedulesResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public List<ScheduledWorkoutResponse> getScheduledWorkouts() {
        return scheduledWorkouts;
    }
    
    public void setScheduledWorkouts(List<ScheduledWorkoutResponse> scheduledWorkouts) {
        this.scheduledWorkouts = scheduledWorkouts;
    }
} 