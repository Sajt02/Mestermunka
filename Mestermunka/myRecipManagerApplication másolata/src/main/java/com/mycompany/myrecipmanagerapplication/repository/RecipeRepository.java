package com.mycompany.myrecipmanagerapplication.repository;

import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import java.sql.*;
import java.util.List;
import com.mycompany.myrecipmanagerapplication.model.Recipe;
import com.mycompany.myrecipmanagerapplication.model.Ingredient;
import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import com.mycompany.myrecipmanagerapplication.service.IngredientService;

public class RecipeRepository {

    private final DatabaseConnection dbConnection;
    private Connection connection;
    private IngredientRepository ingredientRepository;
    private IngredientService ingredientService;
    

    public RecipeRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
         this.ingredientRepository = ingredientRepository; 
    }

    public int createRecipe(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipes (name, description, prep_time, difficulty, image_path, created_at, username, user_id) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection connection = dbConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            java.sql.Date sqlDate = new java.sql.Date(recipe.getCreatedAt().getTime());

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getDescription());
            pstmt.setInt(3, recipe.getPrepTime());
            pstmt.setString(4, recipe.getDifficulty());
            pstmt.setString(5, recipe.getImagePath());
            pstmt.setDate(6, new java.sql.Date(sqlDate.getTime()));
            pstmt.setString(7, recipe.getUsername());
            pstmt.setInt(8, recipe.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating recipe failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating recipe failed, no ID obtained.");
                }
            }
        }
    }

    public void linkIngredientsToRecipe(int recipeId, List<Ingredient> ingredients) throws SQLException {
        for (Ingredient ingredient : ingredients) {
            int ingredientId = ingredientRepository.getOrCreateIngredient(ingredient.getName());
            ingredientRepository.linkRecipeWithIngredient(recipeId, ingredientId);
        }
    }


    public int countRecipesByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_recipes FROM recipes WHERE user_Id = ?";
        try (Connection connection = dbConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("total_recipes") : 0;
            }
        }
    }

    public int countRecipesByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM recipes WHERE username = ?";
        try (Connection connection = dbConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    public void setIngredientRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public void setIngredientService(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
}

    public void linkIngredientsToRecipe(int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
