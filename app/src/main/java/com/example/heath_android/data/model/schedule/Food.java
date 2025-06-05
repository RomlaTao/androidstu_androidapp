package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;

public class Food {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("name") // Tên món ăn
    private String name;
    
    @SerializedName("description") // Mô tả món ăn
    private String description;
    
    @SerializedName("calories") // Calories của món ăn
    private int calories;

    // Constructors
    public Food() {}
    
    public Food(String name, String description, int calories, String quantity, String category) {
        this.name = name;
        this.description = description;
        this.calories = calories;
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
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
} 