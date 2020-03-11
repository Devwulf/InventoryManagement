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

public class PartModifyController extends BaseController
{
    @FXML private ToggleGroup manufactureLocation;
    @FXML private RadioButton inHouseRadio;
    @FXML private RadioButton outSourcedRadio;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField invField;
    @FXML private TextField priceField;
    @FXML private TextField maxField;
    @FXML private TextField minField;

    @FXML private Label extraLabel;
    @FXML private TextField extraField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private int min = 0, max = 0;

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

    public void initialize(int partId)
    {
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
                    if (isInvValid(newValue))
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
                    if (isMaxValid(newValue))
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
                    if (isMinValid(newValue))
                    {
                        minField.setStyle(Constants.VALID_STYLE);
                    }
                    else
                    {
                        minField.setStyle(Constants.INVALID_STYLE);
                    }
                });

        Part part = Inventory.getInstance()
                             .lookupPart(partId);

        // Fill in textfields with the part info
        idField.setText(Integer.toString(part.getId()));
        nameField.setText(part.getName());
        invField.setText(Integer.toString(part.getStock()));
        priceField.setText(Double.toString(part.getPrice()));
        maxField.setText(Integer.toString(part.getMax()));
        minField.setText(Integer.toString(part.getMin()));

        if (part instanceof InHouse)
        {
            inHouseRadio.setSelected(true);
            handleInHouseRadio();
            extraField.setText(Integer.toString(((InHouse) part).getMachineId()));
        }
        else
        {
            outSourcedRadio.setSelected(true);
            handleOutsourcedRadio();
            extraField.setText(((Outsourced) part).getCompanyName());
        }
    }

    private boolean isMinValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        min = matches ? Integer.parseInt(newValue) : 0;

        return matches && min <= max;
    }

    private boolean isMaxValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        max = matches ? Integer.parseInt(newValue) : 0;

        return matches && max >= min;
    }

    private boolean isInvValid(String newValue)
    {
        boolean matches = Constants.NUMBERS_ONLY_PATTERN.matcher(newValue)
                                                        .matches();
        int inv = matches ? Integer.parseInt(newValue) : 0;

        return matches && inv >= min && inv <= max;
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
                                        .matches())
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part must have a name.");
            return;
        }

        if (!isMaxValid(maxField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part's max must be a number and must be >= min.");
            return;
        }

        if (!isMinValid(minField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part's min must be a number and must be <= max.");
            return;
        }

        if (!isInvValid(invField.getText()))
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part's inventory level must be a number, must be >= min, and must be <= max.");
            return;
        }

        if (!Constants.MONEY_PATTERN.matcher(priceField.getText())
                                    .matches())
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part's price must be a number or in money format, and the total price must be >= the sum of the price of its parts.");
            return;
        }

        if (inHouseRadio.isSelected() &&
                !Constants.NUMBERS_ONLY_PATTERN.matcher(extraField.getText())
                                               .matches())
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part's machine id must be a number, if in-house.");
            return;
        }

        if (outSourcedRadio.isSelected() &&
                !Constants.NOT_EMPTY_PATTERN.matcher(extraField.getText())
                                            .matches())
        {
            ViewManager.getInstance()
                       .showWarningPopup("The part, if outsourced, must have a company name.");
            return;
        }

        try
        {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int stock = Integer.parseInt(invField.getText());
            double price = Double.parseDouble(priceField.getText());
            int max = Integer.parseInt(maxField.getText());
            int min = Integer.parseInt(minField.getText());

            Part part;
            if (inHouseRadio.isSelected())
            {
                int machineId = Integer.parseInt(extraField.getText());
                part = new InHouse(id, name, price, stock, min, max, machineId);
            }
            else
            {
                String companyName = extraField.getText();
                part = new Outsourced(id, name, price, stock, min, max, companyName);
            }

            Inventory.getInstance()
                     .updatePartById(id, part);
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
        ViewManager.getInstance()
                   .showConfirmPopup("There are unsaved changes. Cancel?", () ->
                   {
                       Inventory.getInstance()
                                .cancelChanges();
                       stage.close();
                   });
    }
}
