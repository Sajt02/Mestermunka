package com.mycompany.myrecipmanagerapplication.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IngredientService {
    private Connection connect;
    
    public IngredientService(Connection connection) {
        this.connect = connection;
    }

    public int getOrCreateIngredient(String ingredientName) throws SQLException {
        // Ellenőrizzük, hogy létezik-e már a hozzávaló
        Integer existingId = findIngredientId(ingredientName);
        if (existingId != null) {
            return existingId;
        }
        
        // Ha nem létezik, beszúrjuk
        return insertIngredient(ingredientName);
    }
    
    private Integer findIngredientId(String ingredientName) throws SQLException {
        String sql = "SELECT id FROM ingredients WHERE name = ?";
        try (PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, ingredientName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }
    
    private int insertIngredient(String ingredientName) throws SQLException {
        String sql = "INSERT INTO ingredients (name) VALUES (?)";
        try (PreparedStatement stmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ingredientName);
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Hozzávaló beszúrása sikertelen: " + ingredientName);
    }
}
