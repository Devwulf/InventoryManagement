package InventoryManagement.Controllers;

import InventoryManagement.Data.Inventory;
import InventoryManagement.Models.InHouse;
import InventoryManagement.Models.Outsourced;
import InventoryManagement.Models.Part;
import InventoryManagement.Services.ViewManager;
import InventoryManagement.Utils.Constants;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PartAddController extends BaseController
{
    @FXML private ToggleGroup manufactureLocation;
    @FXML private RadioButton inHouseRadio;
    @FXML private RadioButton outSourcedRadio;

    @FXML private TextField nameField;
    @FXML private TextField invField;
    @FXML private TextField priceField;
    @FXML private TextField maxField;
    @FXML private TextField minField;

    @FXML private Label extraLabel;
    @FXML private TextField extraField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // Listeners for the extra field
    private ChangeListener<String> extraFieldInHouseListener = (observable, oldValue, newValue) ->
    {
        if (Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                          .matches())
        {
            extraField.setStyle(Constants.VALID_STYLE);
        }
        else
        {
            extraField.setStyle(Constants.INVALID_STYLE);
        }
    };

    private ChangeListener<String> extraFieldOutSourcedListener = (observable, oldValue, newValue) ->
    {
        if (Constants.NOT_EMPTY_PATTERN.matcher(newValue)
                                       .matches())
        {
            extraField.setStyle(Constants.VALID_STYLE);
        }
        else
        {
            extraField.setStyle(Constants.INVALID_STYLE);
        }
    };

    @FXML
    public void initialize()
    {
        // Select In-House by default
        inHouseRadio.setSelected(true);

        // Setting the textfield styles for validation
        nameField.textProperty()
                 .addListener((observable, oldValue, newValue) ->
                 {
                     if (Constants.NOT_EMPTY_PATTERN.matcher(newValue)
                                                    .matches())
                     {
                         nameField.setStyle(Constants.VALID_STYLE);
                     }
                     else
                     {
                         nameField.setStyle(Constants.INVALID_STYLE);
                     }
                 });

        invField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                      .matches())
                    {
                        invField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        invField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        priceField.textProperty()
                  .addListener((observable, oldValue, newValue) ->
                  {
                      if (Constants.MONEY_PATTERN.matcher(newValue)
                                                 .matches())
                      {
                          priceField.setStyle(Constants.VALID_STYLE);
                      }
                      else
                      {
                          priceField.setStyle(Constants.INVALID_STYLE);
                      }
                  });

        maxField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                      .matches())
                    {
                        maxField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        maxField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        minField.textProperty()
                .addListener((observable, oldValue, newValue) ->
                {
                    if (Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                      .matches())
                    {
                        minField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        minField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        extraField.textProperty()
                  .addListener(extraFieldInHouseListener);
    }

    @FXML
    public void handleInHouseRadio()
    {
        extraLabel.setText("Machine ID");
        extraField.setPromptText("Machine ID");
        extraField.setText("");

        extraField.textProperty()
                  .removeListener(extraFieldOutSourcedListener);
        extraField.textProperty()
                  .addListener(extraFieldInHouseListener);
    }

    @FXML
    public void handleOutsourcedRadio()
    {
        extraLabel.setText("Company Name");
        extraField.setPromptText("Company Name");
        extraField.setText("");

        extraField.textProperty()
                  .removeListener(extraFieldInHouseListener);
        extraField.textProperty()
                  .addListener(extraFieldOutSourcedListener);
    }

    @FXML
    public void handleSave()
    {
        if (!Constants.NOT_EMPTY_PATTERN.matcher(nameField.getText())
                                        .matches() ||
                !Constants.NUMBERS_ONLY_PATTERN.matcher(invField.getText())
                                               .matches() ||
                !Constants.MONEY_PATTERN.matcher(priceField.getText())
                                        .matches() ||
                !Constants.NUMBERS_ONLY_PATTERN.matcher(maxField.getText())
                                               .matches() ||
                !Constants.NUMBERS_ONLY_PATTERN.matcher(minField.getText())
                                               .matches())
            return;

        if (inHouseRadio.isSelected() &&
                !Constants.NUMBERS_ONLY_PATTERN.matcher(extraField.getText())
                                               .matches())
            return;

        if (outSourcedRadio.isSelected() &&
                !Constants.NOT_EMPTY_PATTERN.matcher(extraField.getText())
                                            .matches())
            return;

        try
        {
            String name = nameField.getText();
            int stock = Integer.parseInt(invField.getText());
            double price = Double.parseDouble(priceField.getText());
            int max = Integer.parseInt(maxField.getText());
            int min = Integer.parseInt(minField.getText());

            Part part;
            if (inHouseRadio.isSelected())
            {
                int machineId = Integer.parseInt(extraField.getText());
                // The id here is 0 because the new id should be assigned
                // in the Inventory service
                part = new InHouse(0, name, price, stock, min, max, machineId);
            }
            else
            {
                String companyName = extraField.getText();
                part = new Outsourced(0, name, price, stock, min, max, companyName);
            }

            Inventory.getInstance()
                     .addPart(part);
            Inventory.getInstance()
                     .saveChanges();

            ViewManager.getInstance()
                       .reloadMainView();
            stage.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel()
    {
        Inventory.getInstance()
                 .cancelChanges();
        stage.close();
    }
}
