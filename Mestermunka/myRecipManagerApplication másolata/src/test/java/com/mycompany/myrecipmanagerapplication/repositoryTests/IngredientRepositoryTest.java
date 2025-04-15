package com.mycompany.myrecipmanagerapplication.repositoryTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.mycompany.myrecipmanagerapplication.repository.IngredientRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IngredientRepositoryTest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Test
    void testGetOrCreateIngredientReturnsId() throws Exception {
        // Mock viselkedés beállítása
        when(mockConnection.prepareStatement(
    anyString(), 
    eq(Statement.RETURN_GENERATED_KEYS))
    ).thenReturn(mockStatement);
        
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        // Tesztelés
        IngredientRepository repository = new IngredientRepository(mockConnection);
        int result = repository.getOrCreateIngredient("Salt");
        
        // Ellenőrzés
        assertEquals(5, result);
        verify(mockStatement).setString(1, "Salt");
        verify(mockStatement).executeUpdate();
    }
}