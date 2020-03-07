package InventoryManagement.Controllers;

import InventoryManagement.Utils.ButtonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PopupController extends BaseController
{
    @FXML private Label infoIcon;
    @FXML private Label infoLabel;

    @FXML private Text messageText;

    @FXML private HBox buttonArray;

    public void showWarningPopup(String message)
    {
        messageText.setText(message);
        stage.sizeToScene();
    }

    public void showConfirmPopup(String message, Runnable onConfirm)
    {
        messageText.setText(message);

        ButtonBuilder buttonBuilder = new ButtonBuilder();
        buttonBuilder.setPrefWidth(60)
                     .setPrefHeight(30);

        Button confirmButton = buttonBuilder.setText("Yes")
                                            .build();
        confirmButton.addEventHandler(ActionEvent.ACTION, event ->
        {
            onConfirm.run();
            stage.close();
        });

        Button cancelButton = buttonBuilder.setText("No")
                                           .build();
        cancelButton.addEventHandler(ActionEvent.ACTION, event -> stage.close());

        buttonArray.getChildren()
                   .clear();
        buttonArray.getChildren()
                   .add(confirmButton);
        buttonArray.getChildren()
                   .add(cancelButton);

        stage.sizeToScene();
    }

    @FXML
    public void handleOk()
    {
        stage.close();
    }
}
