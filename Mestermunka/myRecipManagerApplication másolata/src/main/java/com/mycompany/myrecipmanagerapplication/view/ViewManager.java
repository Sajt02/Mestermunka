
package com.mycompany.myrecipmanagerapplication.view;

import com.mycompany.myrecipmanagerapplication.model.Recipe;
import javafx.scene.layout.Pane;

import java.util.List;
import javafx.fxml.FXML;

public class ViewManager {

     @FXML
    private Pane TableView, InputView, HomeView, FridgeView, DetailsView;
    
    private List<Pane> allViews;
    
    public void showHomeView() {
        switchTo(HomeView);
    }

    public void showInputView() {
        switchTo(InputView);
    }

    public void showTableView() {
        switchTo(TableView);
    }

    public void showFridgeView() {
        switchTo(FridgeView);
    }

    public void showRecipeDetails(Recipe recipe) {
        switchTo(DetailsView);
    }
    
    public ViewManager(List<Pane> allViews) {
        this.allViews = allViews;
    }

    public void switchTo(Pane targetView) {
        hideAll();
        targetView.setVisible(true);
    }

    private void hideAll() {
        for (Pane view : allViews) {
            view.setVisible(false);
        }
    }
}
