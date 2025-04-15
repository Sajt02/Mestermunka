package com.mycompany.myrecipmanagerapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String description;
    private int prepTime; 
    private Date createdAt;
    private List<String> ingredients;
    private String imagePath;
    private String difficulty;
    private String username;
    private int userId;

    // Üres konstruktor
    public Recipe() {
        this.createdAt = new Date();
        this.ingredients = new ArrayList<>();
    }

    // Teljes konstruktor
    public Recipe(int id, String name, String description, int prepTime, 
                 Date createdAt, String imagePath, String difficulty,
                 List<String> ingredients) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
        this.imagePath = imagePath;
        this.difficulty = difficulty;
        if (ingredients != null) {
            this.ingredients = ingredients;
        }
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getPrepTime() {
        return prepTime;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public String getImagePath() {
        return imagePath;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public String getUsername() {
        return username;
    }
    public int getUserId() {
        return userId;
    }
    public List<String> getIngredients() {
     return ingredients;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public void setUsername(String username) {
       this.username = username;
    }

    public void setuserId(int userId) {
        this.userId = userId;
    }


    
    
}
