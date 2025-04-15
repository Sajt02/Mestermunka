package com.mycompany.myrecipmanagerapplication;

import com.mycompany.myrecipmanagerapplication.data.DatabaseConnection;
import com.mycompany.myrecipmanagerapplication.util.UserSession;
import com.mycompany.myrecipmanagerapplication.util.AlertsManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mycompany.myrecipmanagerapplication.model.Ingredient;
import com.mycompany.myrecipmanagerapplication.model.Recipe;
import com.mycompany.myrecipmanagerapplication.repository.IngredientRepository;
import com.mycompany.myrecipmanagerapplication.repository.RecipeRepository;
import com.mycompany.myrecipmanagerapplication.repository.UserRepository;
import com.mycompany.myrecipmanagerapplication.service.RecipeService;
import com.mycompany.myrecipmanagerapplication.view.RecipeDetailsFormatter;
import com.mycompany.myrecipmanagerapplication.view.ViewManager;
import java.sql.Date;
import com.mycompany.myrecipmanagerapplication.service.ClockService;
import com.mycompany.myrecipmanagerapplication.service.ImageService;
import com.mycompany.myrecipmanagerapplication.service.IngredientService;
import com.mycompany.myrecipmanagerapplication.util.TableViewConfigurator;
import javafx.scene.layout.FlowPane;

public class MainFormController implements Initializable, TableViewConfigurator.RecipeClickListener {

    @FXML
    private TableView<Recipe> Inventory_tableView, fridgeTableView;
    @FXML
    private TableColumn<Recipe, String> RecipColDiff;
    @FXML
    private TableColumn<Recipe, String> RecipColName;
    @FXML
    private TableColumn<Recipe, String> RecipColTime;
    @FXML
    private TableColumn<Recipe, Button> colAction;
    @FXML
    private ComboBox<String> RecipDiff, initialCombo;
    @FXML
    Pane itemInputContainer;
    @FXML
    private ImageView RecipImage;
    @FXML
    private TextField RecipName;
    @FXML
    private TextField RecipRecip;
    @FXML
    private Slider RecipTime;
    @FXML
    private ImageView lblRecipImage;
    @FXML
    private Label username, lblRecipName, lblRecipTime, lblRecipDiff, lblingredients, lblCreated, lblRecipRecip;
    @FXML
    private Button logout_btn;
    @FXML
    private Label cookTimeDisplay;
    @FXML
    private VBox main_form;
    @FXML
    private FlowPane ingredientsContainer;
    @FXML
    private Label lblCurrentTime;
    @FXML
    private Label lblRecipeCount;
    @FXML
    private Pane TableView, InputView, HomeView, FridgeView, DetailsView;

    private Connection connect;
    private PreparedStatement prepare;
    private final RecipeService recipeService;
    private final DatabaseConnection dbConnection;
    private ViewManager viewManager;
    private ClockService clockService;
    private final ImageService imageService = new ImageService();
    private TableViewConfigurator tableViewConfigurator;
    private IngredientService ingredientService;
    private java.sql.Connection connection;

    private int getOrCreateIngredient(String ingredientName) throws SQLException {
        int id = ingredientService.getOrCreateIngredient(ingredientName);

        Platform.runLater(() -> {
            if (!initialCombo.getItems().contains(ingredientName)) {
                initialCombo.getItems().add(ingredientName);
                initialCombo.getSelectionModel().select(ingredientName);
            }
        });

        return id;
    }
    @FXML
    private void switchToHomeView() {
        viewManager.switchTo(HomeView);
    }
    @FXML
    private void switchToInputView() {
        viewManager.switchTo(InputView);
    }
    @FXML
    private void switchToTableView() {
        viewManager.switchTo(TableView);
    }
    @FXML
    private void switchToFridgeView() {
        viewManager.switchTo(FridgeView);
    }

    void detailsActivity(Recipe recip) {
        viewManager.switchTo(DetailsView);

        lblCreated.setText(RecipeDetailsFormatter.formatDate((Date) recip.getCreatedAt()));
        lblRecipName.setText(recip.getName());
        lblRecipRecip.setText(recip.getDescription());
        lblRecipTime.setText(recip.getPrepTime() + " Perc");
        lblRecipDiff.setText(recip.getDifficulty());
        lblingredients.setText(RecipeDetailsFormatter.formatIngredients(recip.getIngredients()));

        Image image = RecipeDetailsFormatter.getImage(recip.getImagePath());
        if (image != null) {
            lblRecipImage.setImage(image);
        }
    }

    public MainFormController() throws SQLException {
        this.dbConnection = DatabaseConnection.getInstance();

        RecipeRepository recipeRepo = new RecipeRepository(dbConnection);
        IngredientRepository ingredientRepo = new IngredientRepository(dbConnection.getConnection());
        UserRepository userRepo = new UserRepository(dbConnection);  

        this.recipeService = new RecipeService(
                recipeRepo,
                ingredientRepo,
                userRepo 
        );

    }

    @FXML
    public void inventoryAddBtn() {
        try {
            if (!isFormValid()) {
                return;
            }

            Recipe recipe = createAndValidateRecipe();
            if (recipe == null) {
                return;
            }

            List<Ingredient> ingredients = collectAndValidateIngredients();
            if (ingredients.isEmpty()) {
                return;
            }

            saveRecipeAndUpdateUI(recipe, ingredients);
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private boolean isFormValid() {
        if (!validateForm()) {
            return false;
        }
        return true;
    }

    private Recipe createAndValidateRecipe() {
        Recipe recipe = createRecipeFromForm();
        if (recipe == null) {
            AlertsManager.showErrorAlert("Érvénytelen recept adatok!");
        }
        return recipe;
    }

    private List<Ingredient> collectAndValidateIngredients() {
        List<Ingredient> ingredients = collectIngredients();
        if (ingredients.isEmpty()) {
            AlertsManager.showErrorAlert("Legalább egy hozzávaló megadása kötelező!");
        }
        return ingredients;
    }

    private void saveRecipeAndUpdateUI(Recipe recipe, List<Ingredient> ingredients) throws SQLException {
        if (recipeService.createRecipe(recipe, ingredients)) {
            updateRecipeCount();
            refreshUIAfterSuccess();
        } else {
            AlertsManager.showErrorAlert("Nem sikerült menteni a receptet!");
        }
    }

    private void updateRecipeCount() throws SQLException {
        int count = recipeService.getRecipeCountForUser(UserSession.username);
        Platform.runLater(() -> lblRecipeCount.setText("Receptek száma: " + count));
    }

    private void refreshUIAfterSuccess() {
        Platform.runLater(() -> {
            try {
                inventoryShowData();
                AlertsManager.recipSuccessfullyAdded();
                loadIngredientsToComboBox();
                clearFormFields();
            } catch (SQLException e) {
                handleDatabaseError(e);
            }
        });
    }

    private void handleDatabaseError(SQLException e) {
        Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, e);
        Platform.runLater(() -> AlertsManager.showErrorAlert("Adatbázis hiba: " + e.getMessage()));
    }

    private void clearFormFields() {
        RecipName.clear();
        RecipRecip.clear();
        RecipTime.setValue(0);
        RecipDiff.getSelectionModel().clearSelection();
        RecipImage.setImage(null);
        itemInputContainer.getChildren().clear();
        UserSession.path = null;
    }

    private boolean validateForm() {
        if (RecipName.getText() == null || RecipName.getText().trim().isEmpty()) {
            AlertsManager.showErrorAlert("A recept neve kötelező!");
            return false;
        }

        if (RecipRecip.getText() == null || RecipRecip.getText().trim().isEmpty()) {
            AlertsManager.showErrorAlert("A recept leírása kötelező!");
            return false;
        }

        if (RecipTime.getValue() <= 0) {
            AlertsManager.showErrorAlert("Érvényes időtartam megadása kötelező!");
            return false;
        }

        if (RecipDiff.getValue() == null) {
            AlertsManager.showErrorAlert("Válasszon nehézségi szintet!");
            return false;
        }

        if (UserSession.path == null || UserSession.path.isEmpty()) {
            AlertsManager.showErrorAlert("Kép kiválasztása kötelező!");
            return false;
        }

        return true;
    }

    private Recipe createRecipeFromForm() {
        try {
            return recipeService.createRecipeFromForm(
                    RecipName.getText().trim(),
                    RecipRecip.getText().trim(),
                    (int) Math.round(RecipTime.getValue()),
                    RecipDiff.getValue().toString(),
                    UserSession.getPath()
            );
        } catch (IllegalArgumentException e) {
            AlertsManager.showErrorAlert(e.getMessage());
            return null;
        }
    }

    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (Node node : itemInputContainer.getChildren()) {
            if (node instanceof TextField) {
                String ingredientName = ((TextField) node).getText().trim();
                if (!ingredientName.isEmpty()) {
                    ingredients.add(new Ingredient(ingredientName));
                }
            }
        }
        return ingredients;
    }

    @FXML
    private void deleteRecip() {
        Recipe selectedRecip = Inventory_tableView.getSelectionModel().getSelectedItem();

        if (selectedRecip == null) {
            AlertsManager.showErrorAlert("Nincs kiválasztva recept!");
            return;
        }

        // Megerősítés
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Recept Törlése");
        confirmAlert.setHeaderText("Biztosan törlöd: " + selectedRecip.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean success = recipeService.deleteRecipe(selectedRecip);
            if (success) {
                Platform.runLater(() -> {
                    Inventory_tableView.getItems().remove(selectedRecip);
                    refreshRecipeCount();
                    AlertsManager.deleteSucces();
                });
            } else {
                Platform.runLater(() -> {
                    AlertsManager.deleteError();
                });
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                AlertsManager.showErrorAlert("Adatbázis hiba: " + e.getMessage());
            });
            e.printStackTrace();
        }
    }

    private void refreshRecipeCount() {
        new Thread(() -> {
            try {
                int count = recipeService.getRecipeCountForUser(UserSession.username);
                Platform.runLater(() -> {
                    lblRecipeCount.setText("Receptek száma: " + count);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertsManager.databaseError();
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void linkRecipeWithIngredient(int recipeId, int ingredientId) throws SQLException {
        String linkSQL = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (?, ?)";
        prepare = connect.prepareStatement(linkSQL);
        prepare.setInt(1, recipeId);
        prepare.setInt(2, ingredientId);
        prepare.executeUpdate();
    }

    @FXML
    private void inventoryImportBtn() {
        imageService.handleImageSelection(main_form, RecipImage::setImage);
    }

    @FXML
    private void loadRecipes() {
        try {
            ObservableList<Recipe> recipes = recipeService.getAllRecipes();
            Inventory_tableView.setItems(recipes);
        } catch (SQLException e) {
            AlertsManager.showErrorAlert("Adatbázis hiba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connect != null && !connect.isClosed()) {
                connect.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ObservableList<Recipe> inventoryRecipData = FXCollections.observableArrayList();

    @FXML
    private void inventoryShowData() throws SQLException {
        ObservableList<Recipe> newData = recipeService.getAllRecipes();

        if (tableViewConfigurator == null) {
            tableViewConfigurator = new TableViewConfigurator(
                    this,
                    recipeService,
                    Inventory_tableView,
                    RecipColName,
                    RecipColTime,
                    RecipColDiff,
                    colAction
            );
        }
        tableViewConfigurator.inventoryShowData(newData);
    }
    private String[] difficulty = {"könnyű", "Közepes", "Nehéz"};

    public void difficultyList() {
        List<String> typeL = new ArrayList<>();

        for (String data : difficulty) {
            typeL.add(data);
        }
        ObservableList listData = FXCollections.observableArrayList(typeL);
        RecipDiff.setItems(listData);
    }

    @FXML
    public void handleSliderChange() {
        int cookingTime = (int) RecipTime.getValue();
        cookTimeDisplay.setText(cookingTime + " perc");
    }

    @FXML
    void addItemInput() {
        if (itemInputContainer.getChildren().size() < 24) {
            TextField field = new TextField();
            field.setPrefWidth(149);
            itemInputContainer.getChildren().add(field);
        } else {
            AlertsManager.maxFields();
        }
    }

    @FXML
    private void setupSliderListener() {
        RecipTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            cookTimeDisplay.setText(newValue.intValue() + " perc");
        });
    }

    public void logout() {

        try {

            if (AlertsManager.showConfirmationDialog("Kilépés", "Biztosan ki akarsz lépni?")) {

                logout_btn.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(getClass().getResource("loginform.FXML"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Én receptem");
                stage.setScene(scene);
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void displayUsername() {
        String user = UserSession.username;
        user = user.substring(0, 1).toUpperCase() + user.substring(1);
        username.setText(user);
    }

    private List<ComboBox<String>> ingredientComboBoxes = new ArrayList<>();

    private void loadIngredientsToComboBox() {
        try {
            ObservableList<String> ingredients = recipeService.getIngredientComboBoxItems();
            initialCombo.setItems(ingredients);
            initialCombo.setValue("-- Válassz --");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertsManager.databaseError();
        }
    }

    @FXML
    private void addNewIngredientComboBox() {
        ComboBox<String> newCombo = new ComboBox<>();
        newCombo.setPrefWidth(150);
        newCombo.setPromptText("Válassz hozzávalót");
        newCombo.setItems(initialCombo.getItems()); 
        newCombo.setValue("-- Válassz --");
        if (ingredientComboBoxes.size() >= 19) {
            AlertsManager.maxFields();
            return;
        }

        Insets margin = new Insets(10, 10, 10, 10);
        FlowPane.setMargin(newCombo, margin);

        newCombo.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #ccc;");

        ingredientsContainer.getChildren().add(newCombo);
        ingredientComboBoxes.add(newCombo);
    }

    @FXML
    private void searchRecipesByIngredients(ActionEvent event) {
        List<String> selectedIngredients = new ArrayList<>();

        if (initialCombo.getValue() != null && !initialCombo.getValue().isEmpty()
                && !initialCombo.getValue().equals("-- Válassz --")) {
            selectedIngredients.add(initialCombo.getValue());
        }

        for (ComboBox<String> combo : ingredientComboBoxes) {
            if (combo != null && combo.getValue() != null && !combo.getValue().isEmpty()
                    && !combo.getValue().equals("-- Válassz --")) {
                selectedIngredients.add(combo.getValue());
            }
        }

        if (selectedIngredients.isEmpty()) {
            try {
                ObservableList<Recipe> recipeList = recipeService.getAllRecipes(); 
                fridgeTableView.setItems(recipeList);
            } catch (SQLException e) {
                e.printStackTrace();
                AlertsManager.databaseError();
            }
            return;
        }
        try {
            ObservableList<Recipe> filteredRecipes = recipeService.searchRecipesByIngredients(selectedIngredients);
            fridgeTableView.setItems(filteredRecipes);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertsManager.databaseError();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            connect = dbConnection.getConnection();
            viewManager = new ViewManager(List.of(HomeView, InputView, TableView, FridgeView, DetailsView));

            displayUsername();
            difficultyList();
            setupSliderListener();
            clockService = new ClockService(lblCurrentTime);
            clockService.startClock();

            loadIngredientsToComboBox();
            Platform.runLater(() -> {
                try {
                    inventoryShowData();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            if (tableViewConfigurator == null) {
                tableViewConfigurator = new TableViewConfigurator(
                        this,
                        recipeService,
                        Inventory_tableView,
                        RecipColName,
                        RecipColTime,
                        RecipColDiff,
                        colAction
                );
            }
            tableViewConfigurator.setupFridgeTable(fridgeTableView);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertsManager.databaseError();
        }
        try {
            int count = recipeService.getRecipeCountForUser(UserSession.username);
            lblRecipeCount.setText("Receptek száma: " + String.valueOf(count));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        detailsActivity(recipe);
    }
}
