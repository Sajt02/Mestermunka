package com.mycompany.myrecipmanagerapplication.util;

public class UserSession {
    public static String username;
    public static String path;
    public static int userId;
    

    public static String getUsername() {
        return username;    
    }

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static String getPath() {
        return path;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setPath(String path) {
        UserSession.path = path;
    }

    public static void setUserId(int userId) {
        UserSession.userId = userId;
    }

    
}