package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("calories")
    private int calories; // Auto-calculated from foods
    
    @SerializedName("type")
    private String type; // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
    
    @SerializedName("foods")
    private List<Food> foods;
    
    // Constructors
    public MealResponse() {}
    
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public List<Food> getFoods() {
        return foods;
    }
    
    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
    
    // Helper method to calculate total calories from foods
    public int calculateTotalCalories() {
        if (foods == null || foods.isEmpty()) {
            return calories;
        }
        return foods.stream().mapToInt(Food::getCalories).sum();
    }
}
