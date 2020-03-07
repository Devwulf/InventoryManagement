package InventoryManagement.Utils;

import javafx.scene.control.Button;

public class ButtonBuilder
{
    private Button button;

    public ButtonBuilder() {
        button = new Button();
        button.setText("");
        button.setPrefWidth(200);
        button.setPrefHeight(100);
        button.setMnemonicParsing(false);
    }

    public ButtonBuilder setText(String text) {
        button.setText(text);
        return this;
    }

    public ButtonBuilder setPrefWidth(double width) {
        button.setPrefWidth(width);
        return this;
    }

    public ButtonBuilder setPrefHeight (double height) {
        button.setPrefHeight(height);
        return this;
    }

    public Button build() {
        Button newButton = new Button();
        newButton.setText(button.getText());
        newButton.setPrefWidth(button.getPrefWidth());
        newButton.setPrefHeight(button.getPrefHeight());

        return newButton;
    }
}
