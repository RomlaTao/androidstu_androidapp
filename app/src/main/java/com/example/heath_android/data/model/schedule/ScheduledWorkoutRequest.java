package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class ScheduledWorkoutRequest {
    @SerializedName("scheduleId")
    private Long scheduleId;
    
    @SerializedName("workoutId")
    private Long workoutId;
    
    @SerializedName("scheduledDateTime")
    private String scheduledDateTime; // yyyy-MM-ddTHH:mm:ss
    
    @SerializedName("status")
    private String status; // "SCHEDULED", "COMPLETED", "SKIPPED"
    
    @SerializedName("notes")
    private String notes;
    
    // Constructors
    public ScheduledWorkoutRequest() {}
    
    public ScheduledWorkoutRequest(Long scheduleId, Long workoutId, String scheduledDateTime, String status, String notes) {
        this.scheduleId = scheduleId;
        this.workoutId = workoutId;
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
    
    public Long getWorkoutId() {
        return workoutId;
    }
    
    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
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