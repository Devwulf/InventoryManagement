package InventoryManagement;

import InventoryManagement.Data.SeedData;
import InventoryManagement.Services.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        SeedData.initialize();

        URL mainViewPath = getClass().getResource("Views/MainView.fxml");

        ViewManager viewManager = ViewManager.getInstance();
        viewManager.initialize(primaryStage, mainViewPath, "Inventory Management", 1140, 446);

        // Add views
        viewManager.addView(ViewManager.ViewNames.PartAdd, "PartAddView");
        viewManager.addView(ViewManager.ViewNames.PartModify, "PartModifyView");
        viewManager.addView(ViewManager.ViewNames.ProductAdd, "ProductAddView");
        viewManager.addView(ViewManager.ViewNames.ProductModify, "ProductModifyView");
        viewManager.addView(ViewManager.ViewNames.Popup, "Popup");
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
