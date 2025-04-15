package com.mycompany.myrecipmanagerapplication.service;

import com.mycompany.myrecipmanagerapplication.util.UserSession;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.function.Consumer;
import javafx.scene.layout.Pane;

public class ImageService {
    
    public ImageImportResult importImage(Pane parentPane) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(parentPane.getScene().getWindow());
        
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString(), 200, 200, true, true);
            return new ImageImportResult(image, selectedFile.getAbsolutePath());
        }
        return null;
    }
    
    public static class ImageImportResult {
        private final Image image;
        private final String filePath;
        
        public ImageImportResult(Image image, String filePath) {
            this.image = image;
            this.filePath = filePath;
        }
        
        public Image getImage() {
            return image;
        }
        
        public String getFilePath() {
            return filePath;
        }
    }
    public void handleImageSelection(Pane parentPane, Consumer<Image> onSuccess) {
        ImageImportResult result = importImage(parentPane);
        if (result != null) {
            UserSession.path = result.getFilePath();
            onSuccess.accept(result.getImage());
        }
    }
}