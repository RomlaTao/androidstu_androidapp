package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class ScheduledMealRequest {
    @SerializedName("scheduleId")
    private Long scheduleId;
    
    @SerializedName("mealId")
    private Long mealId;
    
    @SerializedName("scheduledDateTime")
    private String scheduledDateTime; // ISO format: 2024-01-02T12:30:00
    
    @SerializedName("status")
    private String status; // "SCHEDULED", "COMPLETED", "SKIPPED"
    
    @SerializedName("notes")
    private String notes;
    
    // Constructors
    public ScheduledMealRequest() {}
    
    public ScheduledMealRequest(Long scheduleId, Long mealId, String scheduledDateTime, String status, String notes) {
        this.scheduleId = scheduleId;
        this.mealId = mealId;
        this.scheduledDateTime = scheduledDateTime;
        this.status = status;
        this.notes = notes;
    }
    
    // Getters and Setters
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