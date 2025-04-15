package com.mycompany.myrecipmanagerapplication.service;

import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import com.mycompany.myrecipmanagerapplication.model.Ingredient;
import com.mycompany.myrecipmanagerapplication.model.Recipe;
import com.mycompany.myrecipmanagerapplication.repository.IngredientRepository;
import com.mycompany.myrecipmanagerapplication.repository.RecipeRepository;
import com.mycompany.myrecipmanagerapplication.repository.UserRepository;
import com.mycompany.myrecipmanagerapplication.util.AlertsManager;
import com.mycompany.myrecipmanagerapplication.util.UserSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class RecipeService {
    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;
    private final UserRepository userRepo; // Add this

    public RecipeService(RecipeRepository recipeRepo, 
                       IngredientRepository ingredientRepo,
                       UserRepository userRepo) {
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
        this.userRepo = userRepo;
    }

    public boolean createRecipe(Recipe recipe, List<Ingredient> ingredients) throws SQLException {
    if (recipe == null) {
        throw new IllegalArgumentException("Recipe cannot be null");
    }
    
    // Ellenőrizzük, hogy létezik-e a felhasználó
    if (!userRepo.userExists(UserSession.getUserId())) {
        AlertsManager.showErrorAlert("Felhasználó nem található!");
        return false;
    }
    
    // Beállítjuk a felhasználói adatokat
    recipe.setuserId(UserSession.getUserId());
    recipe.setUsername(UserSession.getUsername());
    
    // Recept létrehozása
    int recipeId = recipeRepo.createRecipe(recipe);
    
    // Hozzávalók kezelése
    for (Ingredient ingredient : ingredients) {
        int ingredientId = ingredientRepo.getOrCreateIngredient(ingredient.getName());
        ingredientRepo.linkRecipeWithIngredient(recipeId, ingredientId);
    }
    
    return true;
}
    
    public Recipe createRecipeFromForm(String recipeName, String recipeDescription, int prepTime, String difficulty, String imagePath) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setDescription(recipeDescription);
        recipe.setPrepTime(prepTime);
        recipe.setDifficulty(difficulty);
        recipe.setImagePath(imagePath);
        recipe.setCreatedAt(new java.util.Date());
        return recipe;
    }
    
    

    public boolean deleteRecipe(Recipe selectedRecip) throws SQLException {
        try (Connection connect = DatabaseConnection.getInstance().getConnection()) {
            connect.setAutoCommit(false);

            try {
                // 1. Törlés a kapcsolótáblából
                String deleteIngredientsSQL = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
                try (PreparedStatement stmt = connect.prepareStatement(deleteIngredientsSQL)) {
                    stmt.setInt(1, selectedRecip.getId());
                    stmt.executeUpdate();
                }

                // 2. Törlés a receptek táblából
                String deleteRecipeSQL = "DELETE FROM recipes WHERE id = ? AND user_id = ?";
                try (PreparedStatement stmt = connect.prepareStatement(deleteRecipeSQL)) {
                    stmt.setInt(1, selectedRecip.getId());
                    stmt.setInt(2, UserSession.userId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows == 0) {
                        connect.rollback();
                        return false;
                    }
                }

                // Tranzakció commit
                connect.commit();
                return true;

            } catch (SQLException e) {
                connect.rollback();
                throw e;
            }
        }
    }
    
    
    
    
    public ObservableList<Recipe> getAllRecipes() throws SQLException {
        ObservableList<Recipe> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM recipes WHERE user_id = ?";

        try (PreparedStatement prepare = DatabaseConnection.getInstance().prepareStatement(sql)) {
            prepare.setInt(1, UserSession.userId);
            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                List<String> ingredients = getIngredientsForRecipe(result.getInt("id"));
                Recipe recData = new Recipe(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("description"),
                        result.getInt("prep_time"),
                        result.getDate("created_at"),
                        result.getString("image_path"),
                        result.getString("difficulty"),
                        ingredients
                );
                listData.add(recData);
            }
        }
        return listData;
    }

    // Módszer a hozzávalók lekérésére egy recepthez
    private List<String> getIngredientsForRecipe(int recipeId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        String sql = "SELECT i.name FROM ingredients i "
                + "JOIN recipe_ingredients ri ON i.id = ri.ingredient_id "
                + "WHERE ri.recipe_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ingredients.add(rs.getString("name"));
            }
        }
        return ingredients;
    }
    
    public ObservableList<Recipe> searchRecipesByIngredients(List<String> selectedIngredients) throws SQLException {
    ObservableList<Recipe> filteredRecipes = FXCollections.observableArrayList();
    
    try (Connection connect = DatabaseConnection.getInstance().getConnection()) {
        // SQL lekérdezés a szűréshez
        String sql = "SELECT r.* FROM recipes r "
                + "WHERE r.user_id = ? "
                + "AND EXISTS ("
                + "   SELECT 1 FROM recipe_ingredients ri WHERE ri.recipe_id = r.id"
                + ") "
                + "AND NOT EXISTS ("
                + "   SELECT 1 FROM recipe_ingredients ri "
                + "   JOIN ingredients i ON ri.ingredient_id = i.id "
                + "   WHERE ri.recipe_id = r.id "
                + "   AND i.name NOT IN (" + placeholders(selectedIngredients.size()) + ")"
                + ")";

        try (PreparedStatement prepare = connect.prepareStatement(sql)) {
            prepare.setInt(1, UserSession.userId);

            for (int i = 0; i < selectedIngredients.size(); i++) {
                prepare.setString(i + 2, selectedIngredients.get(i));
            }

            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                List<String> ingredients = getIngredientsForRecipe(result.getInt("id"));
                Recipe recData = new Recipe(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("description"),
                        result.getInt("prep_time"),
                        result.getDate("created_at"),
                        result.getString("image_path"),
                        result.getString("difficulty"),
                        ingredients
                );
                filteredRecipes.add(recData);
            }

            // Rendezés a legkevesebb hozzávaló alapján
            filteredRecipes.sort(Comparator.comparingInt(r -> r.getIngredients().size()));

        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Átadjuk a hibát a Controllernek, hogy megfelelően kezelje.
        }
    }

    return filteredRecipes;
}
    
        private String placeholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }
    
        
  public ObservableList<String> getIngredientComboBoxItems() throws SQLException {
    List<String> ingredientNames = ingredientRepo.getIngredientsForUser(UserSession.getUserId());
    ObservableList<String> items = FXCollections.observableArrayList();
    items.add("-- Válassz --");
    items.addAll(ingredientNames);
    return items;
}
  
  public int getRecipeCountByUser(int userId) throws SQLException {
    return recipeRepo.countRecipesByUserId(userId);
}
  public int getRecipeCountForUser(String username) throws SQLException {
        return recipeRepo.countRecipesByUsername(username);
    }
  
  
  public Recipe createRecipeFromForm(String recipeName, String recipeDescription, 
                                 Integer prepTime, String difficulty, String imagePath) 
                                 throws IllegalArgumentException {
    
    // Null check és alapvető validáció
    if (recipeName == null || recipeName.trim().isEmpty()) {
        throw new IllegalArgumentException("A recept neve kötelező");
    }
    if (recipeDescription == null || recipeDescription.trim().isEmpty()) {
        throw new IllegalArgumentException("A recept leírása kötelező");
    }
    if (prepTime == null || prepTime <= 0) {
        throw new IllegalArgumentException("Az időtartamnak pozitívnak kell lennie");
    }
    if (difficulty == null || difficulty.trim().isEmpty()) {
        throw new IllegalArgumentException("A nehézségi szint megadása kötelező");
    }

    Recipe recipe = new Recipe();
    recipe.setName(recipeName.trim());
    recipe.setDescription(recipeDescription.trim());
    recipe.setPrepTime(prepTime);
    recipe.setDifficulty(difficulty);
    recipe.setImagePath(imagePath);
    recipe.setCreatedAt(new java.util.Date());
    
    return recipe;
}
}


