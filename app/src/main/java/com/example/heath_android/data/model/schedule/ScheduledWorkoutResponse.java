package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class ScheduledWorkoutResponse {
    @SerializedName("id")
    private Long id;
    
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
    
    // Nested workout info (from JOIN)
    @SerializedName("workout")
    private WorkoutResponse workout;
    
    // Constructors
    public ScheduledWorkoutResponse() {}
    
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
    
    public WorkoutResponse getWorkout() {
        return workout;
    }
    
    public void setWorkout(WorkoutResponse workout) {
        this.workout = workout;
    }
} 