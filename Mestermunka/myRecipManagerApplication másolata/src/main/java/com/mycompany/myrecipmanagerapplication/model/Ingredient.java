package com.mycompany.myrecipmanagerapplication.model;

public class Ingredient {
    private int id;
    private String name;

    public Ingredient() {}
    public Ingredient(String name) { this.name = name; }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
