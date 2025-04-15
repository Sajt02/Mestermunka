package com.mycompany.myrecipmanagerapplication.util;


import java.sql.SQLException;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import com.mycompany.myrecipmanagerapplication.model.Recipe;
import com.mycompany.myrecipmanagerapplication.service.RecipeService;
import javafx.fxml.FXML;


public class TableViewConfigurator {
    
        @FXML
    private TableColumn<Recipe, String> RecipColName;
    @FXML
    private TableColumn<Recipe, String> RecipColTime;
    @FXML
    private TableView<Recipe> Inventory_tableView, fridgeTableView;
    @FXML
    private TableColumn<Recipe, String> RecipColDiff;
    @FXML
    private TableColumn<Recipe, Button> colAction;

    private final RecipeClickListener recipeClickListener;
    private final RecipeService recipeService;

    public TableViewConfigurator(RecipeClickListener recipeClickListener, 
                           RecipeService recipeService,
                           TableView<Recipe> inventoryTableView,
                           TableColumn<Recipe, String> nameCol,
                           TableColumn<Recipe, String> timeCol,
                           TableColumn<Recipe, String> diffCol,
                           TableColumn<Recipe, Button> actionCol) {
    this.recipeClickListener = recipeClickListener;
    this.recipeService = recipeService;
    this.Inventory_tableView = inventoryTableView;
    this.RecipColName = nameCol;
    this.RecipColTime = timeCol;
    this.RecipColDiff = diffCol;
    this.colAction = actionCol;
}


    public void inventoryShowData(ObservableList<Recipe> newData) throws SQLException {
        if (Inventory_tableView.getItems() == null) {
            Inventory_tableView.setItems(newData);
        } else {
            Inventory_tableView.getItems().setAll(newData);
        }

        // Oszlopok beállítása
        RecipColName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        RecipColTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrepTime() + " perc"));
        RecipColDiff.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty()));

        // Részletek gomb beállítása
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Részletek");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                    detailsButton.setOnAction(event -> {
                        Recipe recipe = getTableView().getItems().get(getIndex());
                        recipeClickListener.onRecipeClick(recipe); 
                    });
                }
            }
        });
    }
    

    public void setupFridgeTable(TableView<Recipe> fridgeTableView) {
        fridgeTableView.getColumns().clear();

        // Recept név oszlop
        TableColumn<Recipe, String> nameCol = new TableColumn<>("Recept neve");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(200);

        // Elkészítési idő oszlop
        TableColumn<Recipe, String> timeCol = new TableColumn<>("Elkészítési idő");
        timeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrepTime() + " perc"));
        timeCol.setPrefWidth(120);

        // Nehézség oszlop
        TableColumn<Recipe, String> diffCol = new TableColumn<>("Nehézség");
        diffCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty()));
        diffCol.setPrefWidth(100);

        // Részletek gomb oszlop
        TableColumn<Recipe, Void> actionCol = new TableColumn<>("Részletek");
        actionCol.setCellFactory(param -> createDetailsButtonCell());
        actionCol.setPrefWidth(100);

        fridgeTableView.getColumns().addAll(nameCol, timeCol, diffCol, actionCol);
    }

    private TableCell<Recipe, Void> createDetailsButtonCell() {
        return new TableCell<>() {
            private final Button detailsButton = new Button("Részletek");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                    detailsButton.setOnAction(event -> {
                        Recipe recipe = getTableView().getItems().get(getIndex());
                        recipeClickListener.onRecipeClick(recipe);
                    });
                }
            }
        };
    }


    public interface RecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
}
