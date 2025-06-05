package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealSchedulesResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("startDate")
    private String startDate;
    
    @SerializedName("endDate")
    private String endDate;
    
    @SerializedName("scheduledMeals")
    private List<ScheduledMealResponse> scheduledMeals;
    
    // Constructors
    public MealSchedulesResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public List<ScheduledMealResponse> getScheduledMeals() {
        return scheduledMeals;
    }
    
    public void setScheduledMeals(List<ScheduledMealResponse> scheduledMeals) {
        this.scheduledMeals = scheduledMeals;
    }
} 