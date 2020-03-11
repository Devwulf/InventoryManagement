package InventoryManagement.Services;

import InventoryManagement.Controllers.BaseController;
import InventoryManagement.Controllers.MainController;
import InventoryManagement.Controllers.PopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;

// Shorthand for FXMLLoader.load and everything that comes with it
public class ViewManager
{
    public enum ViewNames
    {
        PartAdd,
        PartModify,
        PartDelete,
        ProductAdd,
        ProductModify,
        ProductDelete,
        Popup
    }

    private HashMap<ViewNames, URL> views;
    private String defaultTitle;

    private static final String VIEWS_PATH = "/InventoryManagement/Views/";

    private MainController mainController;

    // Prevents changing of the primary stage once assigned
    public void initialize(Stage stage, URL mainViewPath, String title, double width, double height)
    {
        if (stage != null)
        {
            defaultTitle = title;

            try
            {
                FXMLLoader loader = new FXMLLoader(mainViewPath);
                Parent root = loader.load();
                mainController = loader.getController();
                mainController.stage = stage;

                stage.setTitle(title);

                Scene scene = new Scene(root, width, height);
                stage.setScene(scene);
                stage.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addView(ViewNames name, String viewName)
    {
        URL path = ViewManager.class.getResource(VIEWS_PATH + viewName + ".fxml");
        views.put(name, path);
    }

    public void removeView(ViewNames name)
    {
        views.remove(name);
    }

    public <TController extends BaseController> TController loadView(ViewNames name)
    {
        return loadView(name, "", 0, 0);
    }

    public <TController extends BaseController> TController loadView(ViewNames name, String viewTitle)
    {
        return loadView(name, viewTitle, 0, 0);
    }

    public <TController extends BaseController> TController loadView(ViewNames name, String viewTitle, double width, double height)
    {
        try
        {
            URL path = views.get(name);
            FXMLLoader loader = new FXMLLoader(path);
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene;
            if (width <= 0 ||
                height <= 0)
                scene = new Scene(root);
            else
                scene = new Scene(root, width, height);

            if (!viewTitle.isEmpty())
                stage.setTitle(viewTitle);
            else
                stage.setTitle(defaultTitle);

            stage.setScene(scene);
            stage.show();

            // Pass in the stage to the controller so the
            // controller can close its own window
            TController controller = loader.getController();
            controller.stage = stage;

            return controller;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void showWarningPopup(String message)
    {
        PopupController controller = loadView(ViewNames.Popup, "Warning");
        controller.showWarningPopup(message);
    }

    public void showConfirmPopup(String message, Runnable onConfirm) {
        PopupController controller = loadView(ViewNames.Popup, "Confirm");
        controller.showConfirmPopup(message, onConfirm);
    }

    public void reloadMainView()
    {
        mainController.reloadView();
    }

    /* Singleton implementation below */

    private static ViewManager instance = null;

    // Prevents instantiation of class
    private ViewManager()
    {
        views = new HashMap<>();
    }

    public static ViewManager getInstance()
    {
        if (instance == null)
            instance = new ViewManager();

        return instance;
    }
}
