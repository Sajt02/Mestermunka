package com.mycompany.myrecipmanagerapplication.util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertsManager {

    public static void allFieldHaveToFill() {
        Alert alert = new Alert(Alert.AlertType.ERROR); 
        alert.setTitle("Hiba.");
        alert.setHeaderText(null);
        alert.setContentText("Kérlek töltsd ki mindegyik mezőt.");
        alert.showAndWait();
    }

    public static void recipSuccessfullyAdded() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Siker");
        alert.setHeaderText(null);
        alert.setContentText("A recept sikeresen hozzáadva.");
        alert.showAndWait();
    }

    public static void passwordTooShort() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A jelszónak legalább 8 karakternek kell lennie.");
        alert.showAndWait();
    }

    public static void passwordchangesucc() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("INFO");
        alert.setHeaderText(null);
        alert.setContentText("Sikeresen megváltoztattad a jelszavad");
        alert.showAndWait();
    }

    public static void usernotfound() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A felhasználó nem található.");
        alert.showAndWait();
    }

    public static void passatleasteight() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A jelszónak legalább 8 karakternek kell lennie.");
        alert.showAndWait();
    }

    public static void confpassnotmatch() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A megerősítő jelszó eltér.");
        alert.showAndWait();
    }

    public static void usernamenotfound() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Ez a felhasználó nincs regisztrálva.");
        alert.showAndWait();
    }

    public static void deleteSucces() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("A receptet sikeresen törölted.");
        alert.showAndWait();
    }
    public static void userNotFound() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Nincs ilyen nevű felhasználó");
        alert.showAndWait();
    }

    public static void databaseError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Adatbázis hiba.");
        alert.showAndWait();
    }

    public static void deleteError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Nem sikerült törölni a receptet");
        alert.showAndWait();
    }

    public static void nameTaken() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Ilyen névvel már regisztráltak");
        alert.showAndWait();
    }

    public static void successignup() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Sikeres regisztráció");
        alert.showAndWait();
    }

    public static void successlogin() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Sikeres bejelentkezés");
        alert.showAndWait();
    }

    public static void wrongpass() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba!");
        alert.setHeaderText(null);
        alert.setContentText("Hibás jelszó");
        alert.showAndWait();
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hiba");
        alert.setHeaderText("Hiba történt");
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void maxFields() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hiba.");
        alert.setHeaderText(null);
        alert.setContentText("Elérted a maximális mezők számát");
        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
