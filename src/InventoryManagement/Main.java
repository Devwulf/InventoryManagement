package InventoryManagement;

import InventoryManagement.Data.SeedData;
import InventoryManagement.Services.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // TODO: Seed data here since we only want to seed it once
        SeedData.initialize();

        URL mainViewPath = getClass().getResource("Views/MainView.fxml");

        ViewManager viewManager = ViewManager.getInstance();
        viewManager.initialize(primaryStage, mainViewPath, "Inventory Management", 1134, 446);

        // Add views
        viewManager.addView(ViewManager.ViewNames.PartAdd, "PartAddView");
        viewManager.addView(ViewManager.ViewNames.PartModify, "PartModifyView");
        viewManager.addView(ViewManager.ViewNames.Popup, "Popup");
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
