package com.mycompany.myrecipmanagerapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MyRecipManagerApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("loginForm"), 600, 400);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setTitle("Én receptem");
    }
    

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyRecipManagerApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    

    public static void main(String[] args) {
        
        
        launch();
    }

}