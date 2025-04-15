package com.mycompany.myrecipmanagerapplication.repositoryTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import com.mycompany.myrecipmanagerapplication.model.Recipe;
import com.mycompany.myrecipmanagerapplication.repository.IngredientRepository;
import com.mycompany.myrecipmanagerapplication.repository.RecipeRepository;
import com.mycompany.myrecipmanagerapplication.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;

public class RecipeRepositoryTest {

    @Mock
    private DatabaseConnection mockDbConnection;

    @Mock
    private Connection mockConnection;
    @Mock
    private IngredientRepository mockIngredientRepository;
    @Mock
    private IngredientService mockIngredientService;
    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;
    @InjectMocks
    private RecipeRepository recipeRepository;


    @BeforeEach
    public void setUp() throws SQLException {
        mockDbConnection = mock(DatabaseConnection.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        MockitoAnnotations.openMocks(this);

        when(mockDbConnection.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        
        recipeRepository.setIngredientRepository(mockIngredientRepository); // nagyon fontos!
         recipeRepository.setIngredientService(mockIngredientService);
    }

    @Test
    public void testCreateRecipe() throws SQLException {
        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");
        recipe.setDescription("Delicious recipe");
        recipe.setPrepTime(30);
        recipe.setDifficulty("Easy");
        recipe.setImagePath("path/to/image");
        recipe.setCreatedAt(new java.util.Date());
        recipe.setUsername("user");
        

        String sql = "INSERT INTO recipes (name, description, prep_time, difficulty, image_path, created_at, username, user_id) VALUES(?,?,?,?,?,?,?,?)";

        when(mockConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Simulate returning ID 1

        int result = recipeRepository.createRecipe(recipe);

        assertEquals(1, result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCountRecipesByUserId() throws SQLException {
        int userId = 1;
        String sql = "SELECT COUNT(*) AS total_recipes FROM recipes WHERE user_Id = ?";

        when(mockConnection.prepareStatement(sql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("total_recipes")).thenReturn(5);

        int result = recipeRepository.countRecipesByUserId(userId);

        assertEquals(5, result);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testCountRecipesByUsername() throws SQLException {
        String username = "user";
        String sql = "SELECT COUNT(*) AS count FROM recipes WHERE username = ?";

        when(mockConnection.prepareStatement(sql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("count")).thenReturn(3);

        int result = recipeRepository.countRecipesByUsername(username);

        assertEquals(3, result);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    
}
    

