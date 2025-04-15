package com.mycompany.myrecipmanagerapplication.repository;

import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import java.sql.*;

public class UserRepository {
    private final DatabaseConnection dbConnection;

    public UserRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean userExists(int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}