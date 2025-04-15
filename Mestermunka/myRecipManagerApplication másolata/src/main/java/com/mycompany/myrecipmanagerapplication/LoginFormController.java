package com.mycompany.myrecipmanagerapplication;

import com.mycompany.myrecipmanagerapplication.util.UserSession;
import com.mycompany.myrecipmanagerapplication.util.AlertsManager;
import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;

public class LoginFormController {

    @FXML
    private Button create_btn;
    @FXML
    private AnchorPane forgotpass_form;
    @FXML
    private Button fp_backBtn;
    @FXML
    private Button fp_proceedBtn;
    @FXML
    private TextField fp_username;
    @FXML
    private Button haveaccbtn;
    @FXML
    private Button np_backBtn;
    @FXML
    private Button np_changePassBtn;
    @FXML
    private TextField np_confirmPassword;
    @FXML
    private AnchorPane np_newPassForm;
    @FXML
    private TextField np_newPassword;
    @FXML
    private Hyperlink si_forgotpass;
    @FXML
    private Button si_loginbtn;
    @FXML
    private AnchorPane si_loginform;
    @FXML
    private PasswordField si_password;
    @FXML
    private TextField si_username;
    @FXML
    private AnchorPane side_form;
    @FXML
    private PasswordField su_password;
    @FXML
    private Button su_signupbtn;
    @FXML
    private AnchorPane su_signupform;
    @FXML
    private TextField su_username;
    @FXML

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet Result;
    private Alert alert;
    private DatabaseConnection dbConnection;

    @FXML
private void loginBtn(ActionEvent event) {
    String username = si_username.getText();
    String password = si_password.getText();

    if (username.isEmpty() || password.isEmpty()) {
        AlertsManager.allFieldHaveToFill();
        return;
    }
    if (password.length() < 8) {
        AlertsManager.passatleasteight();
        return;
    }

    try {
        String sql = "SELECT id, password FROM users WHERE username = ?";  // id is kell
        try (PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String storedHash = result.getString("password");

                    if (checkPassword(password, storedHash)) {
                        UserSession.username = username;
                        UserSession.userId = result.getInt("id");

                        AlertsManager.successlogin();
                        loadMainForm();
                    } else {
                        AlertsManager.wrongpass();
                    }
                } else {
                    AlertsManager.userNotFound();
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
       
        dbConnection = DatabaseConnection.getInstance();
        try {
            connect = dbConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Adatbázis hiba",
                    "Nem sikerült csatlakozni az adatbázishoz: " + e.getMessage());
        }
    }

    @FXML
    private void signupbtn(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();

        if (connect == null) {
            showAlert(AlertType.ERROR, "Hiba", "Nincs adatbázis kapcsolat");
            return;
        }

        String username = su_username.getText().trim();
        String password = su_password.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Hiányzó adatok", "Kérjük töltsd ki mindkét mezőt!");
            return;
        }

        try {
            if (isUsernameTaken(username)) {
                showAlert(AlertType.WARNING, "Foglalt felhasználónév",
                        "Ez a felhasználónév már foglalt, kérj válassz másikat!");
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            try (PreparedStatement statement = connect.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                statement.executeUpdate();
                showAlert(AlertType.INFORMATION, "Sikeres regisztráció",
                        "Sikeresen regisztráltál! Most már bejelentkezhetsz.");
                su_username.setText("");
                su_password.setText("");

                slider.setNode(side_form);
                slider.setToX(0);
                slider.setDuration(Duration.seconds(.5));
                slider.play();

            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Adatbázis hiba",
                    "Hiba történt a regisztráció során: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isUsernameTaken(String username) throws SQLException {
        if (connect == null) {
            throw new SQLException("Nincs aktív adatbázis kapcsolat");
        }

        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement statement = connect.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public void switchForgotPass() {
        forgotpass_form.setVisible(true);
        si_loginform.setVisible(false);
    }

    public void proceedbtn() throws SQLException {
        if (fp_username.getText().isEmpty()) {
            AlertsManager.allFieldHaveToFill();
        } else {
            String selectData = "SELECT username FROM users WHERE username = ?";
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, fp_username.getText());

                ResultSet result = prepare.executeQuery();

                if (result.next()) {

                    np_newPassForm.setVisible(true);
                    forgotpass_form.setVisible(false);
                } else {
                    AlertsManager.usernamenotfound();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changePassBtn() throws SQLException {
        if (np_newPassword.getText().isEmpty() || np_confirmPassword.getText().isEmpty()) {
            AlertsManager.allFieldHaveToFill();
            return;
        }

        if (!np_newPassword.getText().equals(np_confirmPassword.getText())) {
            AlertsManager.confpassnotmatch();
            return;
        }

        if (np_newPassword.getText().length() < 8) {
            AlertsManager.passatleasteight();
            return;
        }

        String updatePass = "UPDATE users SET password = ? WHERE username = ?";
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        try {
            
            String hashedPassword = BCrypt.hashpw(np_newPassword.getText(), BCrypt.gensalt());

            prepare = connect.prepareStatement(updatePass);
            prepare.setString(1, hashedPassword);
            prepare.setString(2, fp_username.getText());

            int rowsUpdated = prepare.executeUpdate();

            if (rowsUpdated > 0) {
                AlertsManager.passwordchangesucc();

                si_loginform.setVisible(true);
                np_newPassForm.setVisible(false);

                np_newPassword.setText("");
                np_confirmPassword.setText("");
            } else {
                AlertsManager.usernotfound();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (prepare != null) {
                    prepare.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void backToLoginForm() {
        si_loginform.setVisible(true);
        forgotpass_form.setVisible(false);
    }

    public void backToUserForm() {
        forgotpass_form.setVisible(true);
        np_newPassForm.setVisible(false);
    }

    public void switchFrom(ActionEvent event) {

        TranslateTransition slider = new TranslateTransition();

        if (event.getSource() == create_btn) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));
            slider.play();

        } else if (event.getSource() == haveaccbtn) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));
            slider.play();
        }
    }

    private void initializeDatabaseConnection() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            connect = dbConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Érvénytelen jelszó hash: " + e.getMessage());
            return false;
        }
    }

    private void loadMainForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/myrecipmanagerapplication/MainForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) si_loginbtn.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Hiba", "Nem sikerült betölteni a főoldalt");
        }
    }

}
