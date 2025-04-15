package com.mycompany.myrecipmanagerapplication.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IngredientRepository {
    private final Connection connection;

    public IngredientRepository(Connection connection) {
        this.connection = connection;
    }

    public int getOrCreateIngredient(String name) throws SQLException {
        String sql = "INSERT INTO ingredients (name) VALUES (?) ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to get or create ingredient");
    }

    public void linkRecipeWithIngredient(int recipeId, int ingredientId) throws SQLException {
        String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.executeUpdate();
        }
    }

    public List<String> getIngredientsForUser(int userId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        String sql = "SELECT DISTINCT i.name FROM ingredients i "
                   + "JOIN recipe_ingredients ri ON i.id = ri.ingredient_id "
                   + "JOIN recipes r ON ri.recipe_id = r.id "
                   + "WHERE r.user_id = ? ORDER BY i.name";

        try (PreparedStatement prepare = connection.prepareStatement(sql)) {
            prepare.setInt(1, userId);
            ResultSet result = prepare.executeQuery();
            while (result.next()) {
                ingredients.add(result.getString("name"));
            }
        }
        return ingredients;
    }
}