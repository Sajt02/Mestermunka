
package com.mycompany.myrecipmanagerapplication.view;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.scene.image.Image;

public class RecipeDetailsFormatter {
    public static String formatDate(Date date) {
        return (date != null) ? new SimpleDateFormat("yyyy.MM.dd").format(date) : "Nincs dátum";
    }

    public static String formatIngredients(List<String> ingredients) {
        return (ingredients != null && !ingredients.isEmpty())
            ? String.join(", ", ingredients)
            : "Nincsenek hozzávalók";
    }

    public static Image getImage(String path) {
        return (path != null) ? new Image("file:" + path) : null;
    }
}