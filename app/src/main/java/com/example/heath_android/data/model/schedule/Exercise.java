package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    @SerializedName("id")
    private Long id; // Only in response
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("sets")
    private int sets;
    
    @SerializedName("reps")
    private int reps;
    
    @SerializedName("durationSeconds")
    private int durationSeconds;
    
    // Constructors
    public Exercise() {}
    
    public Exercise(String name, String description, int sets, int reps, int durationSeconds) {
        this.name = name;
        this.description = description;
        this.sets = sets;
        this.reps = reps;
        this.durationSeconds = durationSeconds;
    }
    
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
    
    public int getSets() {
        return sets;
    }
    
    public void setSets(int sets) {
        this.sets = sets;
    }
    
    public int getReps() {
        return reps;
    }
    
    public void setReps(int reps) {
        this.reps = reps;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                ", durationSeconds=" + durationSeconds +
                '}';
    }
}
