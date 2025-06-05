package com.example.heath_android.data.model.schedule;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("type")
    private String type; // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
    
    @SerializedName("foods")
    private List<Food> foods;
    
    @SerializedName("userId")
    private String userId;
    
    // Constructors
    public MealRequest() {}
    
    public MealRequest(String name, String description, String type, List<Food> foods) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.foods = foods;
    }
    
    public MealRequest(String name, String description, String type, List<Food> foods, String userId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.foods = foods;
        this.userId = userId;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
