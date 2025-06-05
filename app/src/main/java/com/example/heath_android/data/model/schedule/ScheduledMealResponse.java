package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class ScheduledMealResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("scheduleId")
    private Long scheduleId;
    
    @SerializedName("mealId")
    private Long mealId;
    
    @SerializedName("meal")
    private MealResponse meal;
    
    @SerializedName("scheduledDateTime")
    private String scheduledDateTime; // ISO format: 2024-01-05T12:30:00
    
    @SerializedName("status")
    private String status; // "SCHEDULED", "COMPLETED", "SKIPPED"
    
    @SerializedName("notes")
    private String notes;
    
    // Constructors
    public ScheduledMealResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public Long getMealId() {
        return mealId;
    }
    
    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }
    
    public MealResponse getMeal() {
        return meal;
    }
    
    public void setMeal(MealResponse meal) {
        this.meal = meal;
    }
    
    public String getScheduledDateTime() {
        return scheduledDateTime;
    }
    
    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 